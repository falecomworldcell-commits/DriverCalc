package com.drivercalc

import android.view.accessibility.AccessibilityNodeInfo

object TextExtractor {
    
    fun extractAllText(node: AccessibilityNodeInfo?): String {
        val textBuilder = StringBuilder()
        extractRecursive(node, textBuilder)
        return textBuilder.toString()
    }
    
    private fun extractRecursive(node: AccessibilityNodeInfo?, builder: StringBuilder) {
        if (node == null) return
        
        node.text?.let { builder.append(it).append(" ") }
        node.contentDescription?.let { builder.append(it).append(" ") }
        
        for (i in 0 until node.childCount) {
            extractRecursive(node.getChild(i), builder)
        }
    }
}
