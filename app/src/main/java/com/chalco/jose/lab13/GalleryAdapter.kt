package com.chalco.jose.lab13

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chalco.jose.lab13.databinding.ListItemImgBinding
import java.io.File

// esta clase es un adaptador para un recycler view que muestra una galeria de imagenes
class GalleryAdapter(private val fileArray: Array<File>):
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    // esta clase interna define el viewholder, que representa cada elemento de la lista
    class ViewHolder(private val binding: ListItemImgBinding):
        RecyclerView.ViewHolder(binding.root) {

        // este metodo asocia un archivo a la vista usando glide para cargar la imagen
        fun bind(file: File) {
            Glide.with(binding.root).load(file).into(binding.localImg)
        }
    }

    // aqui creo y devuelvo una instancia del viewholder, inflando el layout correspondiente
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ListItemImgBinding.inflate(layoutInflater, parent, false))
    }

    // este metodo se llama para cada posición de la lista y vincula los datos al viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position])
    }

    // este metodo indica cuantos elementos hay en el array para que el recycler view sepa cuantas vistas mostrar
    override fun getItemCount(): Int {
        return fileArray.size
    }
}
