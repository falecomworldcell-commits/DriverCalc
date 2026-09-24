package com.drivercalc

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.text.NumberFormat
import java.util.*

class OverlayService : Service() {
    
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(1001, createNotification())
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val analysis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("ANALYSIS", AnalysisResult::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra("ANALYSIS")
        }
        
        if (analysis != null) {
            showOverlay(analysis)
        }
        
        return START_NOT_STICKY
    }
    
    private fun showOverlay(analysis: AnalysisResult) {
        removeOverlay()
        
        overlayView = createOverlayView(analysis)
        
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100
        }
        
        windowManager.addView(overlayView, params)
        
        handler.postDelayed({
            removeOverlay()
            stopSelf()
        }, 8000)
    }
    
    private fun createOverlayView(analysis: AnalysisResult): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            
            val corFundo = when (analysis.profitability) {
                AnalysisResult.Profitability.PROFITABLE -> Color.parseColor("#2E7D32")
                AnalysisResult.Profitability.NEUTRAL -> Color.parseColor("#E65100")
                AnalysisResult.Profitability.UNPROFITABLE -> Color.parseColor("#C62828")
            }
            setBackgroundColor(corFundo)
        }
        
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        
        val avisoNota = if (analysis.rideData.passengerRating < 4.70) {
            "\n⚠️ PASSAGEIRO DE RISCO! EVITAR!"
        } else ""
        
        val texto = """
            ANÁLISE DE CORRIDA:
            
            LUCRO LÍQUIDO: ${currencyFormat.format(analysis.lucroLiquido)}$avisoNota
            
            Valor: ${currencyFormat.format(analysis.rideData.totalValue)}
            Distância: ${"%.1f".format(analysis.rideData.totalDistance)} km
            Tempo: ${analysis.rideData.estimatedTimeMinutes} min
            Valor/KM: ${currencyFormat.format(analysis.valuePerKm)}/km
            Valor/Hora: ${currencyFormat.format(analysis.valuePerHour)}/h
            Custo: ${currencyFormat.format(analysis.custoTotal)}
            Nota: ${"%.2f".format(analysis.rideData.passengerRating)}
            
            ${analysis.recommendation}
        """.trimIndent()
        
        val textView = TextView(this).apply {
            text = texto
            textSize = 14f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        
        layout.addView(textView)
        
        return layout
    }
    
    private fun removeOverlay() {
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {}
        }
        overlayView = null
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "overlay_channel",
                "Análise de Corridas",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "overlay_channel")
            .setContentTitle("Driver Calc Ativo")
            .setContentText("Monitorando corridas...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        removeOverlay()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
