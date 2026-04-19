package com.fernanda.medialert.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernanda.medialert.R
import com.fernanda.medialert.ui.auth.LoginActivity
import com.fernanda.medialert.ui.theme.MediAlertTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("APP_START", "MainActivity iniciada")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_TOKEN", "Error obteniendo token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", "TOKEN: $token")
        }

        setContent {
            MediAlertTheme {
                SplashScreen {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onTimeout()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color(0xFF66B2FF), Color(0xFF0086FF)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = (-90).dp)) {
            Image(painter = painterResource(id = R.drawable.logo_medialert), contentDescription = null, modifier = Modifier.size(350.dp))
            // CAMBIADO: Nombre oficial MediAlert
            Text(text = "MediAlert", fontSize = 50.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

