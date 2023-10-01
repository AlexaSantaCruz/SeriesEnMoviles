package com.example.elementosvisualestarea

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var editTextNombre: EditText
    lateinit var editTextCorreo: EditText
    lateinit var radioButtonGenero: RadioGroup
    lateinit var generoSeleccionado: String
    lateinit var spinnerAges: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        spinnerAges = findViewById(R.id.personAge)
        editTextNombre = findViewById(R.id.personName)
        editTextCorreo = findViewById(R.id.personMail)
        radioButtonGenero = findViewById(R.id.personGender)

        val radioGroup: RadioGroup = findViewById(R.id.personGender)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton: RadioButton? = findViewById(checkedId)
            generoSeleccionado = selectedRadioButton?.text.toString()
        }




        // Crear un ArrayAdapter utilizando el array de recursos
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.age_categories,
            android.R.layout.simple_spinner_item
        )

        // Especificar el layout para los elementos de la lista
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Aplicar el adaptador al Spinner
        spinnerAges.adapter = adapter
    }

    fun register(view: View) {
        val checkBoxAccept: CheckBox = findViewById(R.id.checkBoxAccept)

        validar(checkBoxAccept)
        if(checkBoxAccept.isChecked){
            guardarDatos()
        }else{
            Toast.makeText(this@MainActivity, "LLENE TODOS LOS DATOS Y ACEPTE LAS POLITICAS DE PRIVACIDAD", Toast.LENGTH_SHORT).show()
        }

    }

    private fun validar(checkBoxAccept: CheckBox) {
        // Obtiene los valores de los campos
        val name = editTextNombre.text.toString()
        val email = editTextCorreo.text.toString()
        val genderId = radioButtonGenero.checkedRadioButtonId
        val age = spinnerAges.selectedItem.toString()
        val acceptTerms = checkBoxAccept.isChecked

        // Realiza la validación
        if (name.isEmpty() || email.isEmpty() || genderId == -1 ) {
            // Muestra un mensaje indicando que los campos son obligatorios
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()

            // Asigna mensajes de error a las vistas correspondientes
            if (name.isEmpty()) {
                editTextNombre.error = getString(R.string.activity_main_error)
            }
            if (email.isEmpty()) {
                editTextCorreo.error = getString(R.string.activity_main_error)
            }
            if (genderId == -1) {
                Toast.makeText(this, "Selecciona Género", Toast.LENGTH_SHORT).show()

            }


        }
    }

    private fun guardarDatos(){

        val nombre = editTextNombre.text.toString()
        val edad = spinnerAges.selectedItem.toString()
        val genero = generoSeleccionado.toString()
        val correo = editTextCorreo.text.toString()


        val intent = Intent(this, principal::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("edad", edad)
        intent.putExtra("genero", genero)
        intent.putExtra("correo", correo)
        Toast.makeText(this@MainActivity, "Bienvenido!", Toast.LENGTH_SHORT).show()

        startActivity(intent)
    }
}
