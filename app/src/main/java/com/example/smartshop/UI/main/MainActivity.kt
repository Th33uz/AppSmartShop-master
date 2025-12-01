package com.example.smartshop.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartshop.databinding.ActivityMainBinding
import com.example.smartshop.di.ServiceLocator
import com.example.smartshop.ui.home.HomeActivity
import com.example.smartshop.ui.register.RegisterActivity  // ✅ CORRETO (ui minúsculo)
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(ServiceLocator.provideRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest { isLoggedIn ->
                    when (isLoggedIn) {
                        true -> {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        }
                        false -> {
                            Toast.makeText(this@MainActivity, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show()
                            viewModel.resetLoginState()
                        }
                        null -> { /* Estado inicial */ }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.BtnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val senha = binding.inputSenha.text.toString().trim()

            if (email.isNotBlank() && senha.isNotBlank()) {
                viewModel.tryLogin(email, senha)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.BtnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
