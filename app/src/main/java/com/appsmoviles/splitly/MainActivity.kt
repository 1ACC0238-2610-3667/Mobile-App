package com.appsmoviles.splitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import com.appsmoviles.splitly.ui.theme.SplitlyTheme
import com.appsmoviles.splitly.util.LocalTranslation
import com.appsmoviles.splitly.util.Translations
import com.appsmoviles.splitly.view.nav.Navigator
import com.appsmoviles.splitly.viewmodel.AuthViewModel
import com.appsmoviles.splitly.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val authViewModel by viewModels<AuthViewModel>()
        val settingsViewModel by viewModels<SettingsViewModel>()

        super.onCreate(savedInstanceState)
        settingsViewModel.loadSettings(applicationContext)

        enableEdgeToEdge()
        setContent {
            val translationMap = if (settingsViewModel.language == "es") Translations.es else Translations.en
            CompositionLocalProvider(LocalTranslation provides translationMap) {
                SplitlyTheme(darkTheme = settingsViewModel.darkMode) {
                    Navigator(authViewModel, settingsViewModel)
                }
            }
        }
    }
}
