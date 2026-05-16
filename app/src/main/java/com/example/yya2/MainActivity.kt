package com.example.yya2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.yya2.screen.PantallaContratacion
import com.example.yya2.screen.PantallaReservaConfirmada
import com.example.yya2.ui.theme.Yáya2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("YAYA", "Punto de partida: MainActivity iniciada")
        setContent {
            Yáya2Theme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    // Estado global de navegación
    var pantallaActual by remember { mutableStateOf("contratacion") }

    when (pantallaActual) {
        "contratacion" -> {
            PantallaContratacion(onContratarClick = {
                pantallaActual = "confirmacion"
            })
        }
        "confirmacion" -> {
            PantallaReservaConfirmada(onContinuarClick = {
                pantallaActual = "contratacion"
            })
        }
    }
}
