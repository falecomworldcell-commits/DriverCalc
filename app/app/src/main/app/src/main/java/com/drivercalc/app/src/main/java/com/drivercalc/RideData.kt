package com.drivercalc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RideData(
    val appSource: String,
    val passengerRating: Float,
    val totalValue: Float,
    val distanceToPassenger: Float,
    val distanceToDestination: Float,
    val estimatedTimeMinutes: Int
) : Parcelable {
    val totalDistance: Float
        get() = distanceToPassenger + distanceToDestination
}

@Parcelize
data class AnalysisResult(
    val rideData: RideData,
    val valuePerKm: Float,
    val valuePerHour: Float,
    val lucroLiquido: Float,
    val custoTotal: Float,
    val profitability: Profitability,
    val recommendation: String
) : Parcelable {
    enum class Profitability {
        PROFITABLE, NEUTRAL, UNPROFITABLE
    }
}
