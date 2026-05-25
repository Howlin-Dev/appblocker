package com.serhii.appblocker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.serhii.appblocker.navigation.MainNavHost
import com.serhii.appblocker.navigation.entry.EntryViewModel
import com.serhii.appblocker.presentation.root.RootScreen
import com.serhii.appblocker.presentation.root.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val rootViewModel: RootViewModel by viewModel()
    private val entryViewModel: EntryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            rootViewModel.settings.value == null || entryViewModel.state.value.arePermissionsNeeded == null
        }

        enableEdgeToEdge()
        setContent {
            RootScreen(viewModel = rootViewModel) {
                MainNavHost(viewModel = entryViewModel)
            }
        }
    }
}
