package com.example.smartshop.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.model.User
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _registerOk = MutableStateFlow<Boolean?>(null)
    val registerOk: StateFlow<Boolean?> = _registerOk.asStateFlow()

    fun register(nome: String, email: String, senha: String) {
        viewModelScope.launch {
            val ok = repo.register(User(nome, email, senha))
            _registerOk.value = ok
        }
    }
}
