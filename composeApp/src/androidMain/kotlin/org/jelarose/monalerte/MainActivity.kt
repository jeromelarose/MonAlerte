package org.jelarose.monalerte

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.architect.kmpessentials.KmpAndroid

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Koin est initialisé automatiquement dans MonAlerteApplication
        KmpAndroid.initializeApp(this) {
            // optional action to invoke for any permissions disabled by the user.
            // Used only by the internal permissions module.
            // You can present a toast message or any error popup of some kind.
        }
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}