package com.example.smartshop.ui.itens

import androidx.lifecycle.ViewModel
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.Collator
import java.util.Comparator
import java.util.Locale

class ItensViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _lista = MutableStateFlow<Lista?>(null)
    val lista: StateFlow<Lista?> = _lista.asStateFlow()

    private var allItems: MutableList<Item> = mutableListOf()
    private var currentTitle: String = ""
    private var currentQuery: String = ""

    private val collator = Collator.getInstance(Locale("pt", "BR")).apply {
        strength = Collator.PRIMARY
    }

    private val categoriaOrder = listOf(
        "Fruta", "Verdura", "Carne", "Chocolate", "Pão", "Bebida", "Limpeza", "Outros"
    )

    fun load(title: String) {
        currentTitle = title
        val currentList = repo.getListaByTitle(title)
        allItems = currentList?.itens ?: mutableListOf()
        _sortAndPublish(allItems)
    }

    private fun catRank(cat: String): Int {
        val i = categoriaOrder.indexOf(cat)
        return if (i == -1) Int.MAX_VALUE else i
    }

    private fun _sortAndPublish(listToPublish: List<Item>) {
        val comparator =
            compareBy<Item> { it.comprado }
                .thenBy { catRank(it.categoria) }
                .then(Comparator { a, b ->
                    collator.compare(a.nome, b.nome)
                })

        val sortedList = listToPublish.sortedWith(comparator)

        _lista.value = repo.getListaByTitle(currentTitle)?.copy(
            itens = sortedList.toMutableList()
        )
    }

    fun filterItems(query: String) {
        currentQuery = query
        val filteredItems = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter {
                it.nome.contains(query, ignoreCase = true)
            }
        }
        _sortAndPublish(filteredItems)
    }

    fun toggleItemChecked(item: Item, isChecked: Boolean) {
        val foundItem = allItems.find { it.nome == item.nome && it.categoria == item.categoria }
        foundItem?.comprado = isChecked
        filterItems(currentQuery)
    }

    fun removeItem(item: Item) {
        allItems.remove(item)
        repo.getListaByTitle(currentTitle)?.itens?.remove(item)
        filterItems(currentQuery)
    }

    fun renameItem(itemAntigo: Item, nomeNovo: String, qtdNova: Int) {
        val foundItem = allItems.find { it == itemAntigo }
        foundItem?.apply {
            nome = nomeNovo
            quantidade = qtdNova
        }
        filterItems(currentQuery)
    }
}