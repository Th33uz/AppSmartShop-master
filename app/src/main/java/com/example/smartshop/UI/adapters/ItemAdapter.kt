package com.example.smartshop.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartshop.R
import com.example.smartshop.data.model.Item
import com.example.smartshop.databinding.ItemProdutoBinding

class ItemAdapter(
    private val onItemCheck: (Item, Boolean) -> Unit,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var items = listOf<Item>()

    inner class ItemViewHolder(private val binding: ItemProdutoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.txtNomeItem.text = item.nome
            binding.txtQuantidade.text = "${item.quantidade} ${item.unidade}"
            binding.checkComprado.isChecked = item.comprado


            val iconResId = when (item.categoria) {
                "Fruta" -> R.drawable.iconemaca
                "Verdura" -> R.drawable.iconecenoura
                "Carne" -> R.drawable.iconecarne
                "Chocolate" -> R.drawable.iconechocolate
                "Pão" -> R.drawable.iconepao
                "Bebida" -> R.drawable.iconegarrafa
                "Limpeza" -> R.drawable.iconelimpeza
                else -> R.drawable.iconeimg
            }


            binding.imgCategoria.setImageResource(iconResId)

            binding.checkComprado.setOnClickListener {
                onItemCheck(item, binding.checkComprado.isChecked)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemProdutoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}
