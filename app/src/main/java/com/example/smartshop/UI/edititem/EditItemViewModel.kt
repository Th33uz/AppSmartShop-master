package com.example.smartshop.ui.edititem

import androidx.lifecycle.ViewModel
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditItemViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private var allItems: MutableList<Item> = mutableListOf()

    private val _eventoConcluido = MutableStateFlow<Boolean>(false)
    val eventoConcluido: StateFlow<Boolean> = _eventoConcluido.asStateFlow()

    fun loadItem(listTitle: String, itemName: String, itemCategory: String) {
        val currentList = repo.getListaByTitle(listTitle)
        allItems = currentList?.itens ?: mutableListOf()

        _item.value = allItems.find { it.nome == itemName && it.categoria == itemCategory }
    }

    fun updateItem(
        itemAntigo: Item,
        nomeNovo: String,
        qtdNova: Int,
        unidadeNova: String,
        categoriaNova: String
    ) {
        val foundItem = allItems.find { it == itemAntigo }
        foundItem?.apply {
            nome = nomeNovo
            quantidade = qtdNova
            unidade = unidadeNova
            categoria = categoriaNova
        }
        _eventoConcluido.value = true
    }

    fun removeItem(item: Item) {
        allItems.remove(item)
        _eventoConcluido.value = true
    }
}