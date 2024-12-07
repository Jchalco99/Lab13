package com.chalco.jose.lab13

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chalco.jose.lab13.databinding.ActivityGalleryBinding
import java.io.File

// esta clase es una actividad que muestra una galeria de imagenes usando un viewpager
class GalleryActivity : AppCompatActivity() {
    // inicializo el objeto binding para usar viewbinding y acceder facilmente a las vistas
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // aqui inflo el layout asociado a esta actividad usando viewbinding
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root) // asocio la vista principal al contenido de la actividad

        // obtengo la ruta al directorio de almacenamiento externo
        val directory = File(externalMediaDirs[0].absolutePath)
        // obtengo una lista de archivos en ese directorio y la convierto en un array
        val files = directory.listFiles() as Array<File>

        // creo un adaptador para mostrar las imágenes en orden inverso
        val adapter = GalleryAdapter(files.reversedArray())
        // asocio el adaptador al viewpager de la vista
        binding.viewPager.adapter = adapter
    }
}
