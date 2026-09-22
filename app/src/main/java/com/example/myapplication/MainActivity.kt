package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barras.left, 0, barras.right, barras.bottom)
            binding.toolbar.setPadding(0, barras.top, 0, 0)
            insets
        }

        val adaptador = ArrayAdapter.createFromResource(
            this,
            R.array.carreras,
            android.R.layout.simple_spinner_item
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCarrera.adapter = adaptador

        binding.btnGuardar.setOnClickListener { guardarPerfil() }
        binding.btnEditar.setOnClickListener { volverAlFormulario() }

        // Los errores se quitan apenas el usuario corrige, sin esperar a Guardar.
        binding.editNombre.doOnTextChanged { texto, _, _, _ ->
            if (!texto.isNullOrBlank()) marcarCampo(binding.editNombre, binding.errorNombre, true)
        }
        binding.editMatricula.doOnTextChanged { texto, _, _, _ ->
            if (!texto.isNullOrBlank()) {
                marcarCampo(binding.editMatricula, binding.errorMatricula, true)
            }
        }
        binding.spinnerCarrera.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    padre: AdapterView<*>?,
                    vista: View?,
                    posicion: Int,
                    id: Long
                ) {
                    if (posicion > 0) marcarCarrera(true)
                }

                override fun onNothingSelected(padre: AdapterView<*>?) = Unit
            }
    }

    /**
     * Valida los tres campos. Si alguno esta vacio marca el error y no avanza;
     * si todos estan completos arma el resumen y avisa con un Toast.
     */
    private fun guardarPerfil() {
        val nombre = binding.editNombre.text.toString().trim()
        val matricula = binding.editMatricula.text.toString().trim()
        val carreraElegida = binding.spinnerCarrera.selectedItemPosition > 0

        marcarCampo(binding.editNombre, binding.errorNombre, nombre.isNotEmpty())
        marcarCampo(binding.editMatricula, binding.errorMatricula, matricula.isNotEmpty())
        marcarCarrera(carreraElegida)

        if (nombre.isEmpty() || matricula.isEmpty() || !carreraElegida) {
            return
        }

        mostrarResumen(nombre, matricula, binding.spinnerCarrera.selectedItem.toString())
        Toast.makeText(this, R.string.toast_guardado, Toast.LENGTH_SHORT).show()
    }

    /** Pinta el borde del campo de rojo y muestra su mensaje cuando esta vacio. */
    private fun marcarCampo(campo: EditText, error: TextView, completo: Boolean) {
        campo.setBackgroundResource(
            if (completo) R.drawable.bg_campo else R.drawable.bg_campo_error
        )
        error.visibility = if (completo) View.GONE else View.VISIBLE
    }

    /** La primera opcion del Spinner es el texto de ayuda, asi que no cuenta como seleccion. */
    private fun marcarCarrera(elegida: Boolean) {
        binding.spinnerCarrera.setBackgroundResource(
            if (elegida) R.drawable.bg_spinner else R.drawable.bg_spinner_error
        )
        binding.errorCarrera.visibility = if (elegida) View.GONE else View.VISIBLE
    }

    /** Cambia el formulario por la tarjeta de resumen con el saludo y los datos. */
    private fun mostrarResumen(nombre: String, matricula: String, carrera: String) {
        binding.txtSaludo.text = getString(R.string.saludo, nombre.substringBefore(" "))
        binding.valorNombre.text = nombre
        binding.valorMatricula.text = matricula
        binding.valorCarrera.text = carrera

        binding.grupoFormulario.visibility = View.GONE
        binding.grupoResumen.visibility = View.VISIBLE
    }

    private fun volverAlFormulario() {
        binding.grupoResumen.visibility = View.GONE
        binding.grupoFormulario.visibility = View.VISIBLE
    }
}
