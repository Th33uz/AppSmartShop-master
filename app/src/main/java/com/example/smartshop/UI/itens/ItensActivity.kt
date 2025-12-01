package com.example.smartshop.ui.itens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartshop.databinding.ActivityItensBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.ui.adapters.ItemAdapter
import com.example.smartshop.ui.additem.AddItemActivity
import com.example.smartshop.ui.edititem.EditItemActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItensBinding
    private lateinit var adapter: ItemAdapter

    val viewModel: ItensViewModel by viewModels { ItensViewModelFactory(ServiceLocator.provideRepository()) }

    private lateinit var tituloLista: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tituloLista = intent.getStringExtra("titulolista") ?: run {
            finish()
            return
        }

        adapter = ItemAdapter(
            onItemCheck = { item, isChecked ->
                viewModel.toggleItemChecked(item, isChecked)
            },
            onItemClick = { item ->
                val intent = Intent(this, EditItemActivity::class.java)
                intent.putExtra("LIST_TITLE", tituloLista)
                intent.putExtra("ITEM_NAME", item.nome)
                intent.putExtra("ITEM_CATEGORY", item.categoria)
                startActivity(intent)
            }
        )

        binding.recyclerItens.layoutManager = LinearLayoutManager(this)
        binding.recyclerItens.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lista.collectLatest { lista ->
                    binding.txtTituloLista.text = lista?.titulo ?: ""
                    adapter.updateList(lista?.itens ?: emptyList())
                }
            }
        }

        binding.BtnLogoutitens.setOnClickListener {
            finish()
        }

        setupSearch()

        binding.fabAddItem.setOnClickListener {
            startActivity(
                Intent(this, AddItemActivity::class.java)
                    .putExtra("titulolista", tituloLista)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load(tituloLista)
    }

    private fun setupSearch() {
        binding.inputBuscarItens.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterItems(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
