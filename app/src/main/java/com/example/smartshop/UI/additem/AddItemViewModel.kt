package com.example.smartshop.ui.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.launch

class AddItemViewModel(private val repo: ShoppingRepository): ViewModel() {

    fun addItem(listTitle: String, item: Item) {
        viewModelScope.launch {
            repo.addItemToLista(listTitle, item)
        }
    }
}
