# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Vault is a Kotlin Multiplatform (KMP) app for tracking household safety stock. It targets Android, iOS, and Desktop (JVM) using Compose Multiplatform for shared UI.

## Build Commands

All Gradle commands run from the `Vault/` subdirectory (where `gradlew` lives):

```bash
cd Vault

# Build Android APK
./gradlew :composeApp:assembleDebug

# Run Desktop (JVM) app
./gradlew :composeApp:run

# Run tests
./gradlew :composeApp:allTests          # all platforms
./gradlew :composeApp:jvmTest           # JVM only
./gradlew :composeApp:testDebugUnitTest # Android unit tests
```

iOS is built via Xcode from `Vault/iosApp/`.

## Architecture

Three-layer architecture with manual dependency injection:

- **Domain** (`domain/`) — Pure Kotlin interfaces, models, error types, and service contracts. No platform dependencies.
- **Data** (`data/`) — Repository implementations, Room database (local), Ktor HTTP client (network against Pocketbase backend).
- **UI** (`ui/`) — Compose Multiplatform pages, ViewModels with StateFlow, type-safe navigation.

### Dependency Injection

No DI framework (no Koin/Hilt). `AppContainer` is a manual service locator instantiated per-platform in each main entry point (Android `MyApplication`, iOS `MainViewController`, JVM `main()`). It receives platform-specific dependencies (database, DataStore, services) and lazily creates shared repositories/services.

### Key Patterns

- **Offline-first with sync**: All data stored locally in Room. `SyncManager` orchestrates push-pull sync via `PocketbaseSyncRepository`. Dirty tracking (`isDirty` flag) and soft deletes (`isDeleted` flag) on entities.
- **Result type**: `Result<D, E: Error>` sealed interface in `domain/Result.kt`. Error types: `PurchaseError`, `NetworkError`, `SyncError`, `InviteError` in `domain/Error.kt`.
- **Platform abstractions**: `expect`/`actual` for `AppDatabase` construction, `DataStore` paths, and services (`NotificationScheduler`, `PermissionController`, `PurchaseManager`). Implementations live in `androidMain/`, `iosMain/`, `jvmMain/`.
- **Navigation**: Type-safe routes via `@Serializable` sealed class `PageNavigation` in `ui/navigation/PageNavigation.kt`. Graph defined in `ApplicationNavigationHost.kt`.
- **ViewModels**: Use `StateFlow`, `combine`, and `stateIn(SharingStarted.WhileSubscribed(5000))`.

### Backend

Pocketbase REST API. Endpoints configured in `config/AppConfig.kt`. `IS_DEV` flag switches between local emulator address (`10.0.2.2:8090`) and production. Auth uses Bearer tokens stored in DataStore with automatic refresh via Ktor Auth plugin.

### Database

Room with KSP. Entities: `ProductEntity`, `StorageEntity`, `ContainerEntity`. Type converters for `LocalDate` ↔ epoch days. Schema directory: `Vault/composeApp/schemas/`. Destructive migration fallback enabled.

## Source Layout

All shared code is under `Vault/composeApp/src/commonMain/kotlin/org/creategoodthings/vault/`:

```
config/          # AppConfig (endpoints, dev flag)
data/local/      # Room DB, DAOs, entities, DataStore
data/network/    # Ktor HTTP client, DTOs, PocketbaseSyncRepository
data/repositories/ # Repository implementations
domain/          # Models, repository interfaces, services interfaces, Result/Error
ui/navigation/   # Routes and nav host
ui/pages/        # Home, Storage, Premium, Settings, Suggestions (each with Page + ViewModel)
ui/components/   # Shared composables
ui/theme/        # Material3 theming
```

## Dependency Management

Version catalog at `Vault/gradle/libs.versions.toml`. Key versions: Kotlin 2.2.x, Compose Multiplatform 1.9.x, Room 2.8.x, Ktor 3.3.x, RevenueCat KMP for in-app purchases.
