package org.creategoodthings.vault

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.creategoodthings.vault.data.local.DATA_STORE_FILE_NAME
import org.creategoodthings.vault.data.local.createDataStore

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}