package com.example.smartshop.ui.itens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            currentTitle = title
            val currentList = repo.getListaByTitle(title)
            allItems = currentList?.itens ?: mutableListOf()
            _sortAndPublish(allItems)
        }
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
        _lista.value = Lista(
            titulo = currentTitle,
            dono = "",
            imagemUri = null,
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
        viewModelScope.launch {
            repo.toggleItemComprado(currentTitle, item, isChecked)
            load(currentTitle)
        }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch {
            repo.removeItem(currentTitle, item)
            load(currentTitle)
        }
    }

    fun renameItem(itemAntigo: Item, nomeNovo: String, qtdNova: Int) {
        viewModelScope.launch {
            val newItem = itemAntigo.copy(nome = nomeNovo, quantidade = qtdNova)
            repo.updateItem(currentTitle, itemAntigo, newItem)
            load(currentTitle)
        }
    }
}
