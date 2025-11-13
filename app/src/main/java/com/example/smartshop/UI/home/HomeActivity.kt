package com.example.smartshop.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smartshop.databinding.ActivityHomeBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.ui.adapters.ListaAdapter
import com.example.smartshop.ui.addlista.AddListaActivity
import com.example.smartshop.ui.editlista.EditListaActivity
import com.example.smartshop.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ListaAdapter

    private val vm: HomeViewModel by viewModels {
        HomeViewModelFactory(ServiceLocator.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerListas.layoutManager = GridLayoutManager(this, 2)

            adapter = ListaAdapter(
            onItemLongClick = { listaClicada ->
                val intent = Intent(this, EditListaActivity::class.java).apply {
                    putExtra("TITULO_LISTA", listaClicada.titulo)
                }
                startActivity(intent)
            }
        )
        binding.recyclerListas.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.listas.collectLatest { listas ->
                    adapter.updateList(listas)
                }
            }
        }

        setupSearch()

        binding.fabHome.setOnClickListener {
            startActivity(Intent(this, AddListaActivity::class.java))
        }

        binding.BtnLogout.setOnClickListener {
            vm.logout()
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }

    private fun setupSearch() {
        binding.inputBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.filterLists(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        vm.load()
    }
}