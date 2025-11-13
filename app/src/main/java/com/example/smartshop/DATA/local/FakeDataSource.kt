package com.example.smartshop.data.local


import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class FakeDataSource {
    private val _users = mutableListOf(User("Dev", "dev@dev.com", "dev"))
    private val _listas = mutableListOf<Lista>()



    private val _userLogged = MutableStateFlow<User?>(null)
    val userLogged: StateFlow<User?> = _userLogged.asStateFlow()


    fun login(email: String, senha: String): Boolean {
        val user = _users.find { it.email == email && it.senha == senha }
        _userLogged.value = user
        return user != null
    }


    fun logout() { _userLogged.value = null }


    fun register(user: User): Boolean {
        if (_users.any { it.email == user.email }) return false
        _users.add(user)
        return true
    }


    fun currentUser(): User? = _userLogged.value


    fun allLists(): List<Lista> = _listas


    fun addLista(lista: Lista) { _listas.add(lista) }


    fun updateListaTitle(oldTitle: String, newTitle: String) {
        _listas.find { it.titulo == oldTitle }?.apply { titulo = newTitle }
    }


    fun removeListaByTitle(title: String) {
        val lista = _listas.find { it.titulo == title } ?: return
        lista.itens.clear()
        _listas.remove(lista)
    }


    fun getListaByTitle(title: String): Lista? = _listas.find { it.titulo == title }


    fun addItemToLista(title: String, item: Item) {
        _listas.find { it.titulo == title }?.itens?.add(item)
    }
}