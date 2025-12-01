package com.example.smartshop.ui.additem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smartshop.R
import com.example.smartshop.databinding.ActivityAddItemBinding
import com.example.smartshop.data.model.Item
import com.example.smartshop.di.ServiceLocator

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var tituloLista: String

    val viewModel: AddItemViewModel by viewModels { AddItemViewModelFactory(ServiceLocator.provideRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tituloLista = intent.getStringExtra("titulolista") ?: run { finish(); return }

        val unidades = listOf("un", "kg", "g", "L")
        val categorias = listOf("Fruta", "Verdura", "Carne", "Chocolate", "Pão", "Bebida", "Limpeza", "Outros")

        val unidadesAdapter = ArrayAdapter(this, R.layout.simple_spinner_item_white, unidades)
        unidadesAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white)
        binding.spinnerUnidade.adapter = unidadesAdapter

        val categoriasAdapter = ArrayAdapter(this, R.layout.simple_spinner_item_white, categorias)
        categoriasAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white)
        binding.spinnerCategoria.adapter = categoriasAdapter

        binding.btnAdicionarItem.setOnClickListener {
            val nome = binding.inputNomeItem.text.toString().trim()
            val quantidadeTxt = binding.inputQuantidade.text.toString().trim()
            val unidade = binding.spinnerUnidade.selectedItem.toString()
            val categoria = binding.spinnerCategoria.selectedItem.toString()

            if (nome.isEmpty() || quantidadeTxt.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val quantidade = quantidadeTxt.toIntOrNull()
            if (quantidade == null) { Toast.makeText(this, "Quantidade inválida", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            val novoItem = Item(nome = nome, quantidade = quantidade, unidade = unidade, categoria = categoria)
            viewModel.addItem(tituloLista, novoItem)
            Toast.makeText(this, "Item adicionado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}