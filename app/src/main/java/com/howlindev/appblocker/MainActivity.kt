package com.howlindev.appblocker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.howlindev.appblocker.navigation.MainNavHost
import com.howlindev.appblocker.navigation.entry.EntryViewModel
import com.howlindev.appblocker.presentation.root.RootScreen
import com.howlindev.appblocker.presentation.root.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val rootViewModel: RootViewModel by viewModel()
    private val entryViewModel: EntryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val startTime = System.currentTimeMillis()
        super.onCreate(savedInstanceState)

        val isRecreation = savedInstanceState != null

        splashScreen.setKeepOnScreenCondition {
            val elapsed = System.currentTimeMillis() - startTime
            val isWaitingForData = rootViewModel.settings.value == null || entryViewModel.state.value.arePermissionsNeeded == null
            if (isRecreation) {
                isWaitingForData
            } else {
                elapsed < 500L || isWaitingForData
            }
        }

        enableEdgeToEdge()
        setContent {
            RootScreen(viewModel = rootViewModel) {
                MainNavHost(viewModel = entryViewModel)
            }
        }
    }
}

