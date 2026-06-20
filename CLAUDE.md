# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Planning & Exploration

When planning a task, use the file map and architecture section in this CLAUDE.md to identify the specific files relevant to the task, then read those files directly to get their current state. Do not do broad codebase exploration (grep sweeps, find commands, reading many files) when the relevant files are already documented here. The file map is a directory — use it to go straight to the 2-3 files that matter instead of exploring 8-10 defensively.

This is a Kotlin Multiplatform project: shared code lives in `commonMain`, and platform behavior is filled in per-target via `expect`/`actual` in `androidMain`, `iosMain`, and `jvmMain`. When a symbol is `expect`, the real work is in the three `actual`s — go read those, not just the declaration.

User-facing strings are English, declared as Compose resources (`Res.string.*`). Code identifiers and KDoc are English; some inline comments and `//TODO`s are in Danish. Use english when editing, and keep new user-facing text English via Compose resources (not hardcoded).

**Never commit and push on your own in this project.** Make the changes, leave them staged/unstaged for review, and let the user do the committing and pushing.

## Build & Development Commands

All Gradle commands run from the `Vault/` subdirectory (where `gradlew` lives):

```bash
cd Vault

# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run the Desktop (JVM) app
./gradlew :composeApp:run

# Tests
./gradlew :composeApp:allTests          # all platforms
./gradlew :composeApp:jvmTest           # JVM only
./gradlew :composeApp:testDebugUnitTest # Android unit tests

# Clean build
./gradlew clean
```

iOS is built via Xcode from `Vault/iosApp/`. The backend (Pocketbase) lives under `pb/` and runs via `docker-compose` (see `pb/docker-compose.yml`).

## Architecture

Kotlin Multiplatform app targeting **Android, iOS, and Desktop (JVM)** with Compose Multiplatform for shared UI. Vault tracks household **safety stock**: products with best-before/reminder dates, grouped into containers, grouped into storages, kept offline-first locally and synced to a Pocketbase backend.

Three layers, all under `commonMain` unless platform-specific:

- **Domain** (`domain/`) — Pure Kotlin: models, ID value classes, repository/service interfaces, `Result`/`Error`. No platform or framework dependencies.
- **Data** (`data/`) — Room (local persistence), Ktor (Pocketbase HTTP), DataStore (preferences/auth), repository implementations.
- **UI** (`ui/`) — Compose pages (each a Page + ViewModel), shared components, type-safe navigation, theme.

**Key architectural decisions:**

- **Manual DI through `AppContainer`** (no Koin/Hilt). `AppContainer` is a plain class that receives platform-specific dependencies (database, preferences, the three platform services) and lazily constructs the shared repositories/services. It is instantiated **once per platform** in each entry point — Android `MyApplication`, iOS `MainViewController`, JVM `main()` — and threaded down to the nav host. ViewModels are created in `ApplicationNavigationHost` by pulling collaborators off the `AppContainer`; there is no service-locator singleton.
- **`expect`/`actual` for everything platform-bound**: `AppDatabase` construction (`getDatabaseBuilder`), `DataStore` path (`createDataStore`), the `Formatter`, the `PermissionFactory`, and the three services (`NotificationScheduler`, `PermissionController`, `PurchaseManager`). The interface/`expect` is in `commonMain`; the real implementations are in `androidMain`/`iosMain`/`jvmMain`.
- **`Result<D, E: Error>`** sealed interface (`domain/Result.kt`) is the boundary type — network and sync code never throw across layers, they return `Result.Success`/`Result.Error`. Error types live in `domain/Error.kt`: `PurchaseError` (data class), `NetworkError` (enum, with `getResource()` → user-facing `StringResource`), `SyncError` (data class), `InviteError` (enum).
- **ID types are `@JvmInline value class`es** — `ProductID`, `StorageID`, `ContainerID` — each wrapping a UUID `String` with a `.generate()` factory. Domain code passes these, not raw strings; `.value` unwraps for the DB/DTO layer.
- **Type-safe navigation** via `PageNavigation`, a `@Serializable` sealed class. `object` routes for argument-less pages, `data class` for pages with args (`Storage(storageID: String)`). The host declares pages with `composable<PageNavigation.X> { }` and reads args via `backStackEntry.toRoute<PageNavigation.X>()`.
- **Three-way mapping**: Room `*Entity` ↔ network `*DTO` ↔ domain model. `toEntity` (DTO→Entity), `toDTO` (Entity→DTO), `toDomain` (Entity→domain) keep the layers decoupled.

## Offline-First Sync

This is the core data-flow concern and the part most likely to bite you. Everything the user does is written **locally first**; the network is a background reconciler, never on the critical path.

- **Single local source of truth.** All reads/writes go through `OfflineProductRepository` → `ProductDao` → Room. The UI observes Room `Flow`s and never waits on the network to render.
- **Dirty tracking.** Every entity (`ProductEntity`, `StorageEntity`, `ContainerEntity`) carries `isDirty` and `isDeleted` flags. Any local insert/update sets `isDirty = true`. **Deletes are soft** — `deleteProduct` sets `isDeleted = true, isDirty = true`; nothing is physically removed, and every query filters `WHERE isDeleted = false`.
- **Sync orchestration.** `SyncManager.startSync()` guards against concurrent runs (`isSyncing`) and delegates to `PocketbaseSyncRepository.sync()`, which:
  1. **Pushes** dirty rows in dependency order — storages, then containers, then products. Each row is `PATCH`ed; a `404` means it doesn't exist remotely yet, so it falls back to `POST` (upsert). On success the row is marked clean (`markXAsClean`).
  2. **Pulls** changes filtered by `updated >= lastSync`, upserts them into Room via `syncX` (`OnConflictStrategy.REPLACE`, written with `isDirty = false`), then advances `lastSync` in preferences.
- **Destructive local DB is acceptable.** All three platforms build Room with `fallbackToDestructiveMigration(true)` (DB version `4`, schemas exported to `composeApp/schemas/`). The local DB is a disposable cache — if a migration is missing it is wiped and re-pulled from Pocketbase. This is the **opposite** of an app where local data is precious: do not write hand-rolled Room migrations here; the source of truth is the backend.
- **Auth feeds sync.** `KtorAuthRepository` holds the `currentUser` and persists the bearer token + userID to DataStore. Ktor's `Auth` plugin (`loadTokens`) reads that token on every request; `initialize()` refreshes it on launch. Sync needs a non-null `userID` or it returns a `SyncError`.

## File Map

All paths relative to `Vault/composeApp/src/`. Shared code is under `commonMain/kotlin/org/creategoodthings/vault/`; platform code mirrors the same package under `androidMain/`, `iosMain/`, `jvmMain/`.

**App entry & DI:**
- `commonMain/.../AppContainer.kt` — **the manual DI container**. Lazily builds `productRepo`, `httpClient`, `authRepository`, `syncRepository`, `syncManager`. Constructed per-platform.
- `commonMain/.../App.kt` — root composable; wraps `ApplicationNavigationHost` in `VaultTheme`.
- `androidMain/.../MyApplication.kt` — Android `Application`; builds the DB/DataStore/services, constructs `AppContainer`, kicks off `authRepository.initialize()`.
- `iosMain/.../MainViewController.kt` — iOS entry point; same wiring for iOS.
- `jvmMain/.../main.kt` — Desktop entry point; same wiring in a Compose `Window`. The least-complete target.
- `androidMain/.../MainActivity.kt` — single Activity hosting the Compose content.

**Config:**
- `config/AppConfig.kt` — **`IS_DEV` flag** (flip before release builds — it routes `BASE_URL` between the `10.0.2.2:8090` Android emulator host and the production API) plus all Pocketbase collection endpoint paths.

**Domain (`domain/`):**
- `Result.kt` — `Result<D, E: Error>` sealed interface (`Success`/`Error`).
- `Error.kt` — `Error` sealed interface + `PurchaseError`, `NetworkError` (enum, `getResource()`), `SyncError`, `InviteError`.
- `Product.kt` / `Storage.kt` / `Container.kt` — domain models + their `value class` ID types (`.generate()`). `Product.calculateDaysRemaining()` lives here.
- `SuggestedProduct.kt` — onboarding suggestion model (arbitrary best-before/reminder defaults).
- `User.kt` — authenticated user (id, email, token, isPremium, username).
- `repositories/` — interfaces: `ProductRepository` (+ `StorageWithProducts`/`ContainerWithProducts` projections, `ContainerSortOrder`, `toDomain` mappers), `PreferencesRepository`, `AuthRepository`, `SyncRepository`.
- `services/` — interfaces: `NotificationScheduler` (+ `NotificationData`), `PermissionController`, `PurchaseManager` (+ `SubscriptionOption`), and **`SyncManager`** (concrete — the only service with shared logic).

**Data — local (`data/local/`):**
- `Entities.kt` — **all three Room entities**: `ProductEntity`, `StorageEntity`, `ContainerEntity`. Each carries `isDirty`/`isDeleted`; foreign keys + indices defined here.
- `ProductDao.kt` — **the single DAO** for all three entities: inserts, soft-delete, `Flow` projection queries (storages-with-containers, containers-with-products, sorted by name/best-before), the dirty-row getters (`getAllDirtyX`), `markXAsClean`, and the `syncX` upserts.
- `AppDatabase.kt` — `@Database` (version `4`, `exportSchema`), `expect AppDatabaseConstructor`. Per-platform builders in `androidMain/AppDatabase.android.kt`, `jvmMain/.../AppDatabase.jvm.kt`, `iosMain/Database.kt` — all use `fallbackToDestructiveMigration(true)`.
- `createDataStore.kt` (+ `.android`/`.ios` actuals) — DataStore construction and file name.

**Data — network (`data/network/`):**
- `NetworkModule.kt` — `createHttpClient`: ContentNegotiation (lenient JSON), timeouts, `Auth` bearer (`loadTokens` from prefs), `defaultRequest { url(BASE_URL) }`, logging when `IS_DEV`.
- `PocketbaseSyncRepository.kt` — **`SyncRepository` impl**: the push (PATCH→POST upsert) / pull (filtered by `lastSync`) logic described above.
- `DTOs.kt` — all Pocketbase DTOs (`AuthResponseDTO`, `UserDTO`, `ProductDTO`, `StorageDTO`, `ContainerDTO`, `PocketBaseResponse<T>`, request DTOs) + their `toEntity`/`toDTO` mappers.

**Data — repositories (`data/repositories/`):**
- `OfflineProductRepository.kt` — `ProductRepository` impl over `ProductDao`; sets `isDirty = true` on writes, maps entities→domain.
- `KtorAuthRepository.kt` — `AuthRepository` impl: `initialize`/`login`/`register`/`logout`/`refreshUser`/`inviteUserToStorage`, holds `currentUser`, persists token+userID, drives `PurchaseManager.logIn/logOut`.
- `OfflinePreferencesRepository.kt` — `PreferencesRepository` impl over DataStore (standard storage, sort options, reminder time, token/userID/email/isPremium/lastSync).
- `Converters.kt` — Room `TypeConverters` (`LocalDate` ↔ epoch days, etc.).

**UI — navigation (`ui/navigation/`):**
- `PageNavigation.kt` — **route registration**: `@Serializable sealed class` (`Home`, `Storage(storageID)`, `Settings`, `Suggestions`, `Register`).
- `ApplicationNavigationHost.kt` — `NavHost`; **per-route ViewModel construction** by pulling collaborators off `AppContainer`.

**UI — pages (`ui/pages/`)** — each folder has a screen composable + a ViewModel:
- `home/` — `HomePage` + `HomePageViewModel` (dashboard; the most-wired VM — repo, prefs, scheduler, permissions, sync, purchases, auth).
- `storage/` — `StoragePage` + `StoragePageViewModel` (a single storage's containers/products; `SortOption`).
- `suggestionsPage/` — `SuggestionsPage` + `SuggestionsPageViewModel` (onboarding suggested products).
- `settings/` — `SettingsPage` + `SettingsViewModel`.
- `premium/` — premium/auth surface. **Naming quirk:** the page composable is `RegisterPage` (in `PremiumPage.kt`) and the ViewModel is `LoginViewModel` (in `PremiumViewModel.kt`, which also defines `LoginStateError`/`PurchaseOptionsState`/`ShareState`). Sub-sections: `LoginSection`, `UnlockPremiumSection`, `ShareStorageSection`, `PremiumSubscriptionButton`.
- `PageShell.kt` — scaffold wrapper (top/bottom bars) — wrap pages in it.

**UI — components (`ui/components/`):** `ProductCard`, `Dialogs`, `FAB`, `AnnotatedStrings`, `PermissionFactory` (+ platform actuals).

**UI — other:** `ui/Formatter.kt` (+ platform actuals) — date/number formatting. `ui/theme/` — `Color`, `Theme` (`VaultTheme`), `Type`.

**Platform services** (`*Main/.../domain/services/`): Android has `AndroidNotificationScheduler`/`AndroidPermissionController`/`AndroidPurchaseManager` plus `BootReceiver`/`NotificationReceiver`; iOS has `IOSNotificationScheduler`/`IOSPermissionController`; JVM has `JvmNotificationScheduler`/`JvmPermissionController`. Purchases (RevenueCat) are Android/iOS only.

**Backend (`pb/`, repo root):** Pocketbase — `Dockerfile`/`docker-compose.yml`, `pb_migrations/` (collection schema history), `pb_hooks/` (`invite.pb.js`, `revenuecat.pb.js`). Collections: `users`, `product`, `storage`, `container`, `invitations`. Auto-deploys to a VPS via CI.

## Registration Points (Adding New Things)

**New navigation page:**
1. Add a `@Serializable` route to `PageNavigation` (`object` for no args, `data class` with typed fields for arguments).
2. Add a `composable<PageNavigation.X> { }` block in `ApplicationNavigationHost`; read args via `backStackEntry.toRoute<PageNavigation.X>()`, build the ViewModel there from `appContainer`.
3. Create the page composable + its ViewModel under `ui/pages/<name>/`, and wrap the screen in `PageShell`.

**New persisted + synced data (entity or field):**
1. Add/modify the `@Entity` in `Entities.kt` (new entities also go in `AppDatabase`'s `entities` list); add `TypeConverters` in `Converters.kt` if needed; **bump the DB version**. No Room migration needed — destructive fallback wipes and re-pulls.
2. Add DAO queries to `ProductDao` (remember the `isDeleted = false` filter and, for synced rows, the dirty/clean/upsert variants); expose them through `ProductRepository` + `OfflineProductRepository`. Writes must set `isDirty = true`.
3. Mirror the change in the network layer: add/extend the `*DTO` in `DTOs.kt` and its `toEntity`/`toDTO` mappers, and wire push/pull in `PocketbaseSyncRepository` if it's a new collection.
4. Add the matching **Pocketbase collection/field** under `pb/` (migration in `pb_migrations/`) and update `AppConfig` endpoints if it's a new collection.

**New repository:** add the interface under `domain/repositories/`, the impl under `data/repositories/`, then construct it (lazily) in `AppContainer` so ViewModels can reach it.

**New platform service:** declare the interface under `domain/services/`, add the `actual`/implementation in each of `androidMain`/`iosMain`/`jvmMain`, then construct it in each platform entry point and pass it into `AppContainer`.

## Key Conventions

- **ViewModels** use `StateFlow` + `combine` + `stateIn(SharingStarted.WhileSubscribed(5000))`; the UI collects with `collectAsStateWithLifecycle()`.
- **Cross-layer errors are values, not exceptions.** Network/sync/purchase code returns `Result<_, E: Error>`. Surface `NetworkError` to users via `getResource()`; don't let raw exceptions escape the data layer.
- **Offline-first writes**: write to Room and set `isDirty = true`; never block the UI on the network. Deletes are soft (`isDeleted = true`). Let `SyncManager` reconcile.
- **IDs**: pass the `value class` ID types around; create new ones with `ProductID.generate()` etc.; unwrap with `.value` only at the DB/DTO boundary.
- **`AppConfig.IS_DEV`** must be flipped to `false` before any release build (it controls the API base URL and HTTP logging).
- **User-facing strings are English Compose resources** (`Res.string.*`), not hardcoded.
- **Never auto-commit/auto-push** — leave changes for the user to commit. Single-user project, no CI on the app side; manual validation is the norm.

## SDK Targets & Dependencies

- **Targets**: `androidTarget`, `iosX64`/`iosArm64`/`iosSimulatorArm64`, `jvm`. **minSdk** 26, **target/compileSdk** 36. **JVM/Kotlin jvmTarget** 11. **versionName** `1.0.0-alpha2`. Android `release` currently has `isMinifyEnabled = false`.
- **Core versions**: Kotlin 2.2.21, Compose Multiplatform 1.9.3, AGP 8.13.2, Room 2.8.4 (via KSP), Ktor 3.3.3, Navigation Compose 2.9.1, kotlinx-datetime 0.7.1, kotlinx-serialization 1.9.0.
- **Notable libs**: Room + bundled SQLite (local DB), DataStore (preferences/auth), Ktor client (OkHttp on Android/JVM, Darwin on iOS) with Auth + ContentNegotiation + Logging, RevenueCat `purchases-kmp` (in-app purchases, Android/iOS), `ksafe` (secure storage), AndroidX WorkManager (Android notifications).
- Version catalog: `Vault/gradle/libs.versions.toml`. Room schema export dir: `Vault/composeApp/schemas/`.
