package com.example.smartshop.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartshop.databinding.ItemProdutoBinding
import com.example.smartshop.data.model.Item
import java.text.Collator
import java.util.Comparator
import java.util.Locale

class ItemAdapter(
    // CORREÇÃO: Mudei os parâmetros do construtor
    private val onItemCheck: (Item, Boolean) -> Unit,
    private val onLongAction: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemProdutoBinding) : RecyclerView.ViewHolder(binding.root)

    // A lista agora é interna e imutável (List)
    private var itens: List<Item> = emptyList()

    private val collator = Collator.getInstance(Locale("pt", "BR")).apply {
        strength = Collator.PRIMARY
    }

    private val categoriaOrder = listOf(
        "Fruta", "Verdura", "Carne", "Chocolate", "Pão", "Bebida", "Limpeza", "Outros"
    )

    private fun catRank(cat: String): Int {
        val i = categoriaOrder.indexOf(cat)
        return if (i == -1) Int.MAX_VALUE else i
    }

    // CORREÇÃO: A ordenação agora é feita aqui, ao receber a lista
    private fun sort(list: List<Item>): List<Item> {
        val comparator =
            compareBy<Item> { it.comprado }
                .thenBy { catRank(it.categoria) }
                .then(Comparator { a, b ->
                    collator.compare(a.nome, b.nome)
                })
        return list.sortedWith(comparator)
    }

    // CORREÇÃO: 'updateList' agora aceita List e chama a ordenação
    fun updateList(newItens: List<Item>) {
        itens = sort(newItens)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemProdutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itens[position]
        val b = holder.binding

        b.txtNomeItem.text = item.nome
        b.txtQuantidade.text = "${item.quantidade} ${item.unidade}"

        // Efeito de "riscar" o texto se estiver comprado
        if (item.comprado) {
            b.txtNomeItem.paintFlags = b.txtNomeItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            b.txtNomeItem.alpha = 0.5f
        } else {
            b.txtNomeItem.paintFlags = b.txtNomeItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            b.txtNomeItem.alpha = 1.0f
        }


        when (item.categoria) {
            "Fruta" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconemaca)
            "Verdura" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconecenoura)
            "Carne" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconecarne)
            "Chocolate" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconechocolate)
            "Pão" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconepao)
            "Bebida" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconegarrafa)
            "Limpeza" -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconelimpeza)
            else -> b.imgCategoria.setImageResource(com.example.smartshop.R.drawable.iconeimg)
        }

        b.checkComprado.setOnCheckedChangeListener(null)
        b.checkComprado.isChecked = item.comprado
        b.checkComprado.setOnCheckedChangeListener { _, checked ->
            onItemCheck(item, checked)
        }

        b.root.setOnLongClickListener {
            onLongAction(item)
            true
        }
    }

    override fun getItemCount(): Int = itens.size
}