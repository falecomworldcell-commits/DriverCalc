package com.drivercalc

import kotlin.math.roundToInt

class RideCalculator {
    
    companion object {
        private const val CUSTO_POR_KM_PADRAO = 0.65f
        private const val NOTA_MINIMA_SEGURA = 4.70f
        private const val KM_MIN_VERDE = 3.0f
        private const val HORA_MIN_VERDE = 35.0f
        private const val KM_MIN_AMARELO = 2.0f
    }
    
    fun analisarCorrida(rideData: RideData): AnalysisResult {
        val kmTotal = if (rideData.totalDistance > 0) rideData.totalDistance else 0.1f
        
        val custoTotal = kmTotal * CUSTO_POR_KM_PADRAO
        val lucroLiquido = rideData.totalValue - custoTotal
        val ganhoPorKm = rideData.totalValue / kmTotal
        
        val horas = rideData.estimatedTimeMinutes / 60.0f
        val ganhoPorHora = if (horas > 0) rideData.totalValue / horas else rideData.totalValue
        
        val (profitability, mensagem) = when {
            rideData.passengerRating < NOTA_MINIMA_SEGURA -> 
                Pair(AnalysisResult.Profitability.UNPROFITABLE, "🔴 VERMELHO (RISCO - PASSAGEIRO PROBLEMÁTICO!)")
            
            ganhoPorKm >= KM_MIN_VERDE && ganhoPorHora >= HORA_MIN_VERDE -> 
                Pair(AnalysisResult.Profitability.PROFITABLE, "🟢 VERDE - ACEITE AGORA!")
            
            ganhoPorKm >= KM_MIN_AMARELO -> 
                Pair(AnalysisResult.Profitability.NEUTRAL, "🟡 AMARELO - AVALIE COM CUIDADO")
            
            else -> 
                Pair(AnalysisResult.Profitability.UNPROFITABLE, "🔴 VERMELHO - RECUSE!")
        }
        
        return AnalysisResult(
            rideData = rideData,
            valuePerKm = (ganhoPorKm * 100).roundToInt() / 100f,
            valuePerHour = (ganhoPorHora * 100).roundToInt() / 100f,
            lucroLiquido = (lucroLiquido * 100).roundToInt() / 100f,
            custoTotal = (custoTotal * 100).roundToInt() / 100f,
            profitability = profitability,
            recommendation = mensagem
        )
    }
}
