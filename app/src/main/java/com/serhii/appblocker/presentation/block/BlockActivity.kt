package com.serhii.appblocker.presentation.block

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.core.ui.theme.AppBlockerTheme

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppBlockerTheme {
                AppScaffold {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text("BLOCKED!")
                    }
                }
            }
        }
    }
}