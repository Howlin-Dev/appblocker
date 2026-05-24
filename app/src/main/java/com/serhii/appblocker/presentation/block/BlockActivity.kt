package com.serhii.appblocker.presentation.block

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import com.serhii.appblocker.core.domain.model.ThemeMode
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.core.ui.theme.AppBlockerTheme
import com.serhii.appblocker.core.util.LocalAppIconProvider
import com.serhii.appblocker.util.AppIconLoader

class BlockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppBlockerTheme {
                CompositionLocalProvider(
                    LocalAppIconProvider provides AppIconLoader(context = this)
                ) {
                    Surface {
                        BlockScreen(
                            onClose = {
                                goToHomeScreen()
                                finish()
                            },
                            onTimerRunsOut = {
                                finish()
                            },
                        )
                    }
                }
            }
        }
    }

    private fun goToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }
}