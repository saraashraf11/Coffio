package com.coffeehub.pos

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.coffeehub.pos.data.preferences.UserPreferencesManager
import com.coffeehub.pos.presentation.navigation.BrewPointNavGraph
import com.coffeehub.pos.presentation.theme.BrewPointTheme
import com.coffeehub.pos.utils.LocaleUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    override fun attachBaseContext(newBase: Context) {
        // Read saved language synchronously before the activity UI loads
        val lang = runBlocking { newBase.let {
            try { com.coffeehub.pos.data.preferences.UserPreferencesManager(it).language.first() }
            catch (e: Exception) { "en" }
        }}
        super.attachBaseContext(LocaleUtils.applyLocale(newBase, lang))
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by preferencesManager.isDarkTheme.collectAsState(initial = true)
            val windowSizeClass = calculateWindowSizeClass(this)
            BrewPointTheme(darkTheme = isDarkTheme) {
                BrewPointNavGraph(windowSizeClass = windowSizeClass)
            }
        }
    }
}
