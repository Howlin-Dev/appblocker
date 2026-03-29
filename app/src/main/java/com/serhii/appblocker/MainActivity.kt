package com.serhii.appblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.serhii.appblocker.navigation.MainNavHost
import com.serhii.appblocker.core.ui.theme.AppBlockerTheme
import com.serhii.appblocker.core.util.LocalAppIconProvider
import com.serhii.appblocker.util.AppIconLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppBlockerTheme {
                CompositionLocalProvider(
                    LocalAppIconProvider provides AppIconLoader(context = this)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MainNavHost()
                    }
                }
            }
        }
    }
}