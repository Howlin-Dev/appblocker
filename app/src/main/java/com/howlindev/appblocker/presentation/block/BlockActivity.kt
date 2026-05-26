package com.howlindev.appblocker.presentation.block

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.howlindev.appblocker.presentation.root.RootScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class BlockActivity : AppCompatActivity() {

    private val viewModel: BlockViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootScreen {
                BlockScreen(
                    viewModel = viewModel,
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        if (packageName != null) {
            viewModel.updateBlockedPackage(packageName)
        }
    }

    private fun goToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }
}

