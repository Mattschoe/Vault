package org.creategoodthings.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val builder = getDatabaseBuilder(applicationContext)
        val dbInstance = builder
            .setDriver(BundledSQLiteDriver())
            .build()
        val dataStore = createDataStore(this)

        val appContainer = AppContainer(
            database = dbInstance,
            dataStore = dataStore
        )

        setContent {
            App(appContainer)
        }
    }
}