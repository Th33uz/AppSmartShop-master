package com.example.smartshop.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartshop.R
import com.example.smartshop.databinding.ItemListaBinding
import com.example.smartshop.data.model.Lista
import com.example.smartshop.ui.itens.ItensActivity


class ListaAdapter(
    private val onItemLongClick: (Lista) -> Unit
) : RecyclerView.Adapter<ListaAdapter.ListaViewHolder>() {

    private var listas: List<Lista> = emptyList()

    inner class ListaViewHolder(val binding: ItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: Lista) {
            val b = binding

            b.txtNomeLista.text = lista.titulo
            if (!lista.imagemUri.isNullOrEmpty()) {
                b.imgLista.setImageURI(Uri.parse(lista.imagemUri))
            } else {
                b.imgLista.setImageResource(R.drawable.iconeimg) // Use 'R' importado
            }

            b.root.setOnClickListener {
                val ctx = b.root.context
                ctx.startActivity(
                    Intent(ctx, ItensActivity::class.java)
                        .putExtra("titulolista", lista.titulo)
                )
            }


            b.root.setOnLongClickListener {
                onItemLongClick(lista)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val binding = ItemListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val lista = listas[position]
        holder.bind(lista)
    }

    override fun getItemCount(): Int = listas.size

    fun updateList(newList: List<Lista>) {
        listas = newList.sortedBy { it.titulo }
        notifyDataSetChanged()
    }
}