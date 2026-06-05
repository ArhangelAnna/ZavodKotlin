package com.example.zavod.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.zavod.R
import com.example.zavod.model.auth.LoginState
import com.example.zavod.repository.AuthRepository
import com.example.zavod.viewmodel.LoginViewModel
import com.example.zavod.viewmodel.LoginViewModelFactory
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var tabField: EditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }


        setContentView(R.layout.activity_login)

        tabField = findViewById(R.id.etTabName)
        loginBtn = findViewById(R.id.Auth)

        val repo = AuthRepository(this)

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(repo)
        )[LoginViewModel::class.java]

        viewModel.state.observe(this) { state ->
            renderState(state)
        }

        loginBtn.setOnClickListener {
            val passId = tabField.text
                .toString()
                .trim()

            if (passId.isEmpty()) {
                toast("Введите табельный номер")
                return@setOnClickListener
            }

            viewModel.login(passId)
        }
    }

    private fun renderState(state: LoginState?) {
        if (state == null) return

        loginBtn.isEnabled = !state.loading

        if (state.error != null) {
            toast(state.error!!)
            return
        }

        if (state.success) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}