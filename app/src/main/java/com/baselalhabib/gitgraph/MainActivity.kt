package com.baselalhabib.gitgraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.baselalhabib.gitgraph.ui.navigation.GitGraphNavigation
import com.baselalhabib.gitgraph.ui.theme.GitGraphTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitGraphTheme {
                GitGraphNavigation()
            }
        }
    }
}
