package com.example.smartshop.data.repository


import com.example.smartshop.data.local.FakeDataSource
import com.example.smartshop.data.model.Item
import com.example.smartshop.data.model.Lista
import com.example.smartshop.data.model.User
import kotlinx.coroutines.flow.StateFlow


class ShoppingRepository(private val local: FakeDataSource) {
    val userLogged: StateFlow<User?> = local.userLogged


    fun login(email: String, senha: String): Boolean = local.login(email, senha)
    fun logout() = local.logout()
    fun register(user: User): Boolean = local.register(user)
    fun currentUser(): User? = local.currentUser()


    fun minhasListas(): List<Lista> {
        val u = currentUser() ?: return emptyList()
        return local.allLists().filter { it.dono == u.email }
    }


    fun addLista(titulo: String, imagemUri: String?) {
        val u = currentUser() ?: return
        local.addLista(Lista(titulo = titulo, dono = u.email, imagemUri = imagemUri))
    }


    fun updateListaTitle(oldTitle: String, newTitle: String) = local.updateListaTitle(oldTitle, newTitle)
    fun removeListaByTitle(title: String) = local.removeListaByTitle(title)
    fun getListaByTitle(title: String): Lista? = local.getListaByTitle(title)


    fun addItemToLista(title: String, item: Item) = local.addItemToLista(title, item)
}