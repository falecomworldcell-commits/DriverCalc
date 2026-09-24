package com.drivercalc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }
        
        val title = TextView(this).apply {
            text = "🚖 Driver Calc - BETA"
            textSize = 24f
            setPadding(0, 0, 0, 30)
        }
        layout.addView(title)
        
        val btnAccessibility = Button(this).apply {
            text = "1️⃣ Ativar Serviço de Acessibilidade"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        layout.addView(btnAccessibility)
        
        val btnOverlay = Button(this).apply {
            text = "2️⃣ Permitir Sobrepor Telas"
            setOnClickListener {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
        layout.addView(btnOverlay)
        
        val info = TextView(this).apply {
            text = """
                ✅ COMO USAR:
                
                1. Ative as 2 permissões acima
                2. Abra Uber ou 99
                3. Aguarde corrida aparecer
                4. Veja análise automática!
                
                🟢 = ACEITE
                🟡 = AVALIE
                🔴 = RECUSE
            """.trimIndent()
            setPadding(0, 30, 0, 0)
        }
        layout.addView(info)
        
        setContentView(layout)
    }
}
