package com.example.smartshop.ui.edititem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartshop.R
import com.example.smartshop.databinding.ActivityEditItemBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.data.model.Item
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItemBinding
    val viewModel: EditItemViewModel by viewModels { EditItemViewModelFactory(ServiceLocator.provideRepository()) }

    private var currentItem: Item? = null

    private val unidades = listOf("un", "kg", "g", "L")
    private val categorias = listOf("Fruta", "Verdura", "Carne", "Chocolate", "Pão", "Bebida", "Limpeza", "Outros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listTitle = intent.getStringExtra("LIST_TITLE")
        val itemName = intent.getStringExtra("ITEM_NAME")
        val itemCategory = intent.getStringExtra("ITEM_CATEGORY")

        if (listTitle == null || itemName == null || itemCategory == null) {
            Toast.makeText(this, "Erro: Item não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val unidadeAdapter = ArrayAdapter(this, R.layout.simple_spinner_item_white, unidades)
        unidadeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white)
        binding.spinnerUnidade.adapter = unidadeAdapter

        val categoriaAdapter = ArrayAdapter(this, R.layout.simple_spinner_item_white, categorias)
        categoriaAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white)
        binding.spinnerCategoria.adapter = categoriaAdapter

        viewModel.loadItem(listTitle, itemName, itemCategory)

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.btnSalvar.setOnClickListener {
            val itemParaSalvar = currentItem
            if (itemParaSalvar == null) {
                Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoNome = binding.inputNomeItem.text.toString().trim()
            val novaQtd = binding.inputQuantidade.text.toString().toIntOrNull()
            val novaUnidade = binding.spinnerUnidade.selectedItem.toString()
            val novaCategoria = binding.spinnerCategoria.selectedItem.toString()

            if (novoNome.isEmpty() || novaQtd == null) {
                Toast.makeText(this, "Nome ou quantidade inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateItem(itemParaSalvar, novoNome, novaQtd, novaUnidade, novaCategoria)
            Toast.makeText(this, "Item salvo!", Toast.LENGTH_SHORT).show()
        }

        binding.btnExcluir.setOnClickListener {
            val itemParaExcluir = currentItem
            if (itemParaExcluir == null) {
                Toast.makeText(this, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this, R.style.Theme_SmartShop_AlertDialog)
                .setTitle("Excluir Item")
                .setMessage("Tem certeza que deseja excluir o item \"${itemParaExcluir.nome}\"?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir") { _, _ ->
                    viewModel.removeItem(itemParaExcluir)
                    Toast.makeText(this, "Item excluído", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        lifecycleScope.launch {
            viewModel.item.collectLatest { item ->
                if (item != null) {
                    currentItem = item
                    binding.inputNomeItem.setText(item.nome)
                    binding.inputQuantidade.setText(item.quantidade.toString())


                    val unidadeIndex = unidades.indexOf(item.unidade).coerceAtLeast(0)
                    binding.spinnerUnidade.setSelection(unidadeIndex)

                    val categoriaIndex = categorias.indexOf(item.categoria).coerceAtLeast(0)
                    binding.spinnerCategoria.setSelection(categoriaIndex)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventoConcluido.collectLatest { concluido ->
                if (concluido) {
                    finish()
                }
            }
        }
    }
}