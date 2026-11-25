package com.gusbarzala.digimon

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var dbHelper: DigimonDbHelper
    private var webView: WebView? = null
    private var statusLabel: TextView? = null

    private var evolucionLista = false
    private val umbralImpulso = 14.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DigimonDbHelper(this)
        dbHelper.insertarRegistroSiVacio("Agumon", "Rookie / Cresta del Valor", "Estable")

        statusLabel = findViewById(R.id.tv_estado_valor)
        actualizarTextoEstado("CRESTA DEL VALOR: EN ESPERA (AGUMON)")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        webView = findViewById(R.id.webview_digimon)
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = WebViewClient()
        webView?.loadUrl("https://gusbarzala.github.io/dig.mon1/")
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val magnitud = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            dbHelper.registrarLecturaSensor(x, y, z)

            if (magnitud > umbralImpulso && !evolucionLista) {
                evolucionLista = true
                val mensaje = "VALOR ACTIVADO: WARGREYMON LISTO"
                actualizarTextoEstado(mensaje)
                dbHelper.registrarEventoEvolucion("Agumon -> WarGreymon", "Cresta del Valor activada")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sin cambios necesarios para este prototipo
    }

    private fun actualizarTextoEstado(texto: String) {
        statusLabel?.text = texto
    }
}
