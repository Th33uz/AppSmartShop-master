package com.example.smartshop.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState.asStateFlow()

    fun tryLogin(email: String, senha: String) {
        viewModelScope.launch {
            val ok = repo.login(email, senha)
            _loginState.value = ok
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }
}
