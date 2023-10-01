package com.example.elementosvisualestarea

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class principal : AppCompatActivity() {
        lateinit var arrayDatos: ArrayList<Bundle>
        private lateinit var recibirNotificacionToggle: ToggleButton
        private lateinit var gustaSerieRadioGroup: RadioGroup

        private lateinit var sharedPreferences: SharedPreferences
        private val PREFS_NAME = "MyPrefs"
        private val CHANNEL_ID = "series_notification_channel"


    override fun onCreate(savedInstanceState: Bundle?) {
        var nombre = intent.getStringExtra("nombre")
        var edad = intent.getStringExtra("edad")
        var genero = intent.getStringExtra("genero")
        var correo = intent.getStringExtra("correo")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        arrayDatos = ArrayList()

        // Crear un Bundle para almacenar los datos
        val datosBundle = Bundle()
        datosBundle.putString("nombre", nombre)
        datosBundle.putString("edad", edad)
        datosBundle.putString("genero", genero)
        datosBundle.putString("correo", correo)

        // Agregar el Bundle al array
        arrayDatos.add(datosBundle)


        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.series)
        val imageView = findViewById<ImageView>(R.id.imagenSerie)

        val seriesArray = resources.getStringArray(R.array.series_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, seriesArray)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            // Cuando se selecciona una serie, obtén el nombre de la serie seleccionada
            val selectedSerie = adapter.getItem(position).toString()

            // Forma el nombre del recurso de imagen sin la extensión .jpg
            val imageName = selectedSerie.toLowerCase().replace(" ", "")

            // Obtiene el ID del recurso de imagen
            val imageResourceId = resources.getIdentifier(imageName, "drawable", packageName)

            // Configura la imagen en el ImageView
            imageView.setImageResource(imageResourceId)

            // Inicializa los elementos de la interfaz
            recibirNotificacionToggle = findViewById(R.id.recibirNotificacion)
            gustaSerieRadioGroup = findViewById(R.id.seriesLike)

            // Inicializa SharedPreferences
            sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // Carga el estado guardado (si existe)
            cargarEstado(autoCompleteTextView)

            // Configura el botón de guardar
            val guardarButton = findViewById<Button>(R.id.guardar)
            guardarButton.setOnClickListener {
                // Guarda el estado actual
                save(autoCompleteTextView)
                // Si el ToggleButton está activado, envía una notificación
                if (recibirNotificacionToggle.isChecked) {
                    enviarNotificacion(autoCompleteTextView)
                }
            }
        }
    }

    private fun enviarNotificacion(autoCompleteTextView: AutoCompleteTextView) {
        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()

        // Crea y muestra una notificación
        createNotificationChannel(nombreSerieActual)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Te suscribiste a una serie")
            .setContentText("Te has suscrito a $nombreSerieActual")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(nombreSerie: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = nombreSerie
            val descriptionText = "Notificaciones para la serie: $nombreSerie"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun save(autoCompleteTextView: AutoCompleteTextView) {
        val editor = sharedPreferences.edit()

        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()

        // Guarda el estado del ToggleButton con el nombre de la serie como parte de la clave
        editor.putBoolean("$nombreSerieActual.recibirNotificacion", recibirNotificacionToggle.isChecked)

        // Guarda el ID del RadioButton seleccionado con el nombre de la serie como parte de la clave
        val radioButtonId = gustaSerieRadioGroup.checkedRadioButtonId
        editor.putInt("$nombreSerieActual.gustaSerieId", radioButtonId)

        // Aplica los cambios
        editor.apply()
    }

    private fun cargarEstado(autoCompleteTextView: AutoCompleteTextView) {
        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()

        // Carga el estado del ToggleButton con el nombre de la serie como parte de la clave
        val recibirNotificacion =
            sharedPreferences.getBoolean("$nombreSerieActual.recibirNotificacion", false)
        recibirNotificacionToggle.isChecked = recibirNotificacion

        // Carga el ID del RadioButton seleccionado con el nombre de la serie como parte de la clave
        val radioButtonId =
            sharedPreferences.getInt("$nombreSerieActual.gustaSerieId", -1)

        // Si no hay un RadioButton seleccionado previamente, radioButtonId será -1
        if (radioButtonId != -1) {
            gustaSerieRadioGroup.check(radioButtonId)
        } else {
            // Si no hay datos guardados previamente, deja el estado sin marcar
            gustaSerieRadioGroup.clearCheck()
        }
    }


    private fun mostrarNombre() {
        val primerDato = arrayDatos.firstOrNull()
        val nombreGuardado = primerDato?.getString("nombre")
        Toast.makeText(this, "Nombre guardado: $nombreGuardado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarEdad() {
        val primerDato = arrayDatos.firstOrNull()
        val edadGuardada = primerDato?.getString("edad")
        Toast.makeText(this, "Edad guardada: $edadGuardada", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarGenero() {
        val primerDato = arrayDatos.firstOrNull()
        val generoGuardado = primerDato?.getString("genero")
        Toast.makeText(this, "Género guardado: $generoGuardado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarCorreo() {
        val primerDato = arrayDatos.firstOrNull()
        val correoGuardado = primerDato?.getString("correo")
        Toast.makeText(this, "Correo guardado: $correoGuardado", Toast.LENGTH_SHORT).show()
    }

    fun showData(view: View) {
        val primerDato = arrayDatos.firstOrNull()
        val nombreGuardado = primerDato?.getString("nombre").toString()
        val edadGuardada = primerDato?.getString("edad").toString()
        val generoGuardado = primerDato?.getString("genero").toString()
        val correoGuardado = primerDato?.getString("correo").toString()

        mostrarDatos(nombreGuardado, edadGuardada, generoGuardado, correoGuardado);
    }
    private fun mostrarDatos(nombreGuardado: String, edadGuardada: String, generoGuardado: String, correoGuardado: String,){
        // Obtén el LayoutInflater
        val inflater = layoutInflater

        // Infla el diseño de tu pantalla emergente personalizada (puede ser un archivo XML)
        val dialogView = inflater.inflate(R.layout.layout_popup, null)

        // Configura los datos en la pantalla emergente
        val textView = dialogView.findViewById<TextView>(R.id.textViewPopup)
        textView.text = "Nombre: $nombreGuardado \nEdad: $edadGuardada \nGenero: $generoGuardado\nCorreo: $correoGuardado"

        // Crea el cuadro de diálogo
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
            .setTitle("Datos")
            .setPositiveButton("Aceptar") { dialog, _ ->
                // Código a ejecutar cuando se hace clic en Aceptar
                dialog.dismiss() // Cierra la pantalla emergente
            }

        // Muestra el cuadro de diálogo
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}