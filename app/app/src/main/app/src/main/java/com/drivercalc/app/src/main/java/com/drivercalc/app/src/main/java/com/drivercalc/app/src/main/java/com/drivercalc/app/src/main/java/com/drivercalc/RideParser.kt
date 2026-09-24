package com.drivercalc

object RideParser {
    
    fun parseUber(text: String): RideData? {
        return try {
            val ratingRegex = """(\d+[.,]\d+)\s*★""".toRegex()
            val valueRegex = """R\$\s*(\d+[.,]\d+)""".toRegex()
            val distanceRegex = """(\d+[.,]?\d*)\s*km""".toRegex(RegexOption.IGNORE_CASE)
            val timeRegex = """(\d+)\s*min""".toRegex(RegexOption.IGNORE_CASE)
            
            val rating = ratingRegex.find(text)?.groupValues?.get(1)?.replace(",", ".")?.toFloatOrNull() ?: 5.0f
            val value = valueRegex.find(text)?.groupValues?.get(1)?.replace(",", ".")?.toFloatOrNull() ?: return null
            
            val distances = distanceRegex.findAll(text).mapNotNull { 
                it.groupValues[1].replace(",", ".").toFloatOrNull() 
            }.toList()
            
            val distanceToPassenger = distances.getOrNull(0) ?: 0f
            val distanceToDestination = distances.getOrNull(1) ?: distances.getOrNull(0) ?: 0f
            
            val time = timeRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 15
            
            RideData(
                appSource = "Uber",
                passengerRating = rating,
                totalValue = value,
                distanceToPassenger = distanceToPassenger,
                distanceToDestination = distanceToDestination,
                estimatedTimeMinutes = time
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun parse99(text: String): RideData? {
        return parseUber(text)?.copy(appSource = "99")
    }
}
