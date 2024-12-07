package com.chalco.jose.lab13

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.chalco.jose.lab13.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// esta clase representa la actividad principal donde manejo la camara, capturo imagenes y navego a la galeria
class MainActivity : AppCompatActivity() {
    // inicializo el binding para usar viewbinding y acceder facilmente a las vistas
    private lateinit var binding: ActivityMainBinding

    // futuro para obtener el proveedor de la camara (camera provider)
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    // selector de camara para alternar entre la camara trasera y frontal
    private lateinit var cameraSelector: CameraSelector

    // objeto para capturar imagenes
    private var imageCapture: ImageCapture? = null

    // executor para manejar las tareas de captura de imagenes en un hilo separado
    private lateinit var imgCaptureExecutor: ExecutorService

    // registro un callback para manejar el resultado de la solicitud de permisos de camara
    private val cameraPermissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                // si el permiso es concedido, inicio la camara
                startCamera()
            } else {
                // si no se concede el permiso, muestro un snackbar informativo
                Snackbar.make(
                    binding.root,
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    // este metodo se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflo el layout usando viewbinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // obtengo el futuro del proveedor de la camara
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // inicio con la camara trasera como predeterminada
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        // creo un executor para manejar la captura de imagenes en un hilo separado
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        // solicito el permiso para usar la camara
        cameraPermissionsResult.launch(android.Manifest.permission.CAMERA)

        // configuro el boton de captura de imagen para llamar al metodo takePhoto
        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()
        }

        // configuro el boton para alternar entre camaras
        binding.switchBtn.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            // reinicio la camara con el nuevo selector
            startCamera()
        }

        // configuro el boton para abrir la galeria
        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    // este metodo configura y enlaza la camara con los casos de uso (preview y captura)
    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            // configuro el surface provider para el preview
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // creo el caso de uso para capturar imagenes
            imageCapture = ImageCapture.Builder().build()

            try {
                // desvinculo todos los casos de uso actuales
                cameraProvider.unbindAll()
                // enlazo el ciclo de vida con los nuevos casos de uso (preview y captura)
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // este metodo maneja la captura de fotos y guarda el archivo en almacenamiento externo
    private fun takePhoto() {
        imageCapture?.let {
            // creo un nombre unico para la imagen
            val fileName = "JPEG_${System.currentTimeMillis()}"

            // creo un archivo en el almacenamiento externo
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                // uso el executor para manejar la tarea en segundo plano
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    // callback cuando la imagen se guarda exitosamente
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i(TAG, "The image has been saved in ${file.toUri()}")
                    }

                    // callback si hay un error al guardar la imagen
                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, "Error taking photo: $exception")
                    }
                }
            )
        }
    }

    // defino una constante para usar en los logs
    companion object {
        val TAG = "MainActivity"
    }
}
