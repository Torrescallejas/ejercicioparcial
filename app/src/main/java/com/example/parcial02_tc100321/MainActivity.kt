package com.example.parcial02_tc100321

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private lateinit var textViewCameraStatus: TextView
    private lateinit var textViewLocationStatus: TextView
    private lateinit var textViewStorageStatus: TextView
    private lateinit var imageView: ImageView // Para mostrar la imagen capturada
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val CAMERA_REQUEST_CODE = 1001
    private val CAMERA_PERMISSION_CODE = 1002
    private val LOCATION_PERMISSION_CODE = 1003

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Asignar botones y vistas
        val buttonCamera = findViewById<Button>(R.id.buttonCamera)
        val buttonLocation = findViewById<Button>(R.id.buttonLocation)
        val buttonStorage = findViewById<Button>(R.id.buttonStorage)
        val buttonOpenCamera = findViewById<Button>(R.id.buttonOpenCamera)
        val buttonShowLocation = findViewById<Button>(R.id.buttonShowLocation) // Botón para mostrar la ubicación
        imageView = findViewById(R.id.imageView)

        textViewCameraStatus = findViewById(R.id.textViewCameraStatus)
        textViewLocationStatus = findViewById(R.id.textViewLocationStatus)
        textViewStorageStatus = findViewById(R.id.textViewStorageStatus)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Listener para pedir permisos de cámara
        buttonCamera.setOnClickListener {
            requestPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
        }

        // Listener para pedir permisos de ubicación
        buttonLocation.setOnClickListener {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE)
        }

        // Listener para abrir la cámara si el permiso fue concedido
        buttonOpenCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                Toast.makeText(this, "El permiso de la cámara no ha sido concedido", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para mostrar la ubicación
        buttonShowLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showLocation()
            } else {
                Toast.makeText(this, "El permiso de la ubicación no ha sido concedido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para solicitar permisos
    private fun requestPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            when (permission) {
                Manifest.permission.CAMERA -> textViewCameraStatus.text = "Cámara: Concedido"
                Manifest.permission.ACCESS_FINE_LOCATION -> textViewLocationStatus.text = "Ubicación: Concedido"
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    // Manejar el resultado de los permisos solicitados
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    textViewCameraStatus.text = "Cámara: Concedido"
                } else {
                    textViewCameraStatus.text = "Cámara: Denegado"
                    Toast.makeText(this, "El permiso de la cámara ha sido denegado", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    textViewLocationStatus.text = "Ubicación: Concedido"
                } else {
                    textViewLocationStatus.text = "Ubicación: Denegado"
                    Toast.makeText(this, "El permiso de la ubicación ha sido denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Método para abrir la cámara
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara compatible", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para mostrar la ubicación
    @SuppressLint("MissingPermission")
    private fun showLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Toast.makeText(this, "Latitud: $latitude, Longitud: $longitude", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Manejar el resultado de la cámara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap) // Mostrar la imagen en el ImageView
        }
    }

}