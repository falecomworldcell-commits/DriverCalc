package com.drivercalc

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class RideAccessibilityService : AccessibilityService() {
    
    private var lastProcessedTime = 0L
    private val debounceDelay = 500L
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        val packageName = event.packageName?.toString() ?: return
        
        if (packageName != "com.ubercab" && packageName != "com.taxis99") return
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessedTime < debounceDelay) return
        lastProcessedTime = currentTime
        
        processRideRequest(packageName)
    }
    
    private fun processRideRequest(packageName: String) {
        try {
            val rootNode = rootInActiveWindow ?: return
            val allText = TextExtractor.extractAllText(rootNode)
            
            Log.d("DriverCalc", "Texto: $allText")
            
            val rideData = when (packageName) {
                "com.ubercab" -> RideParser.parseUber(allText)
                "com.taxis99" -> RideParser.parse99(allText)
                else -> null
            } ?: return
            
            val calculator = RideCalculator()
            val analysis = calculator.analisarCorrida(rideData)
            
            showOverlay(analysis)
            
        } catch (e: Exception) {
            Log.e("DriverCalc", "Erro", e)
        }
    }
    
    private fun showOverlay(analysis: AnalysisResult) {
        val intent = Intent(this, OverlayService::class.java).apply {
            putExtra("ANALYSIS", analysis)
        }
        startService(intent)
    }
    
    override fun onInterrupt() {}
}
