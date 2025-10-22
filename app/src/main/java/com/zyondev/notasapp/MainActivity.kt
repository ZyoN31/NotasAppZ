package com.zyon31.notasapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var txtNota: EditText
    private lateinit var txtResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtNota = findViewById(R.id.txtNota)
        txtResultado = findViewById(R.id.txtResultado)

        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardarNotaArchivo() }
        findViewById<Button>(R.id.btnMostrar).setOnClickListener { mostrarNotaArchivo() }

        findViewById<Button>(R.id.btnGuardarRoom).setOnClickListener { guardarNotaRoom() }
        findViewById<Button>(R.id.btnMostrarRoom).setOnClickListener { mostrarNotasRoom() }
    }

    private fun guardarNotaArchivo() {
        val texto = txtNota.text.toString()
        if (texto.isBlank()) {
            Toast.makeText(this, "Escribe una nota antes de guardar", Toast.LENGTH_SHORT).show()
            return
        }

        openFileOutput("nota.txt", MODE_PRIVATE).use { fos ->
            fos.write(texto.toByteArray())
        }

        Toast.makeText(this, "Nota guardada correctamente (archivo)", Toast.LENGTH_SHORT).show()
        txtNota.text.clear()
    }

    private fun mostrarNotaArchivo() {
        try {
            val texto = openFileInput("nota.txt").bufferedReader().use { it.readText() }
            txtResultado.text = texto
        } catch (e: Exception) {
            txtResultado.text = "No se encontró nota guardada (archivo)."
        }
    }

    private fun guardarNotaRoom() {
        val texto = txtNota.text.toString()
        if (texto.isBlank()) {
            Toast.makeText(this, "Escribe una nota antes de guardar", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val nueva = Nota(contenido = texto)
            withContext(Dispatchers.IO) {
                db.notaDao().insertar(nueva)
            }
            Toast.makeText(this@MainActivity, "Nota guardada en Room", Toast.LENGTH_SHORT).show()
            txtNota.text.clear()
        }
    }

    private fun mostrarNotasRoom() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val lista = withContext(Dispatchers.IO) {
                db.notaDao().obtenerNotas()
            }
            if (lista.isEmpty()) {
                txtResultado.text = "No hay notas en la base de datos."
            } else {
                txtResultado.text = lista.joinToString(separator = "\n\n") { "• ${it.contenido}" }
            }
        }
    }
}