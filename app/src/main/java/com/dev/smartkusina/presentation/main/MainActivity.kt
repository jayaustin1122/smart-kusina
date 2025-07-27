    package com.dev.smartkusina.presentation.main

    import SmartKusinaApp
    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.enableEdgeToEdge
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.ui.Modifier
    import com.dev.smartkusina.ui.theme.SmartKusinaTheme
    import dagger.hilt.android.AndroidEntryPoint

    @AndroidEntryPoint
    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                SmartKusinaTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SmartKusinaApp()
                    }
                }
            }
        }
    }
