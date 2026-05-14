package com.example.kashtakala

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<android.widget.Button>(R.id.btnLogin)
        val etEmail = findViewById<android.widget.EditText>(R.id.etEmail)
        val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            Log.d("LoginActivity", "Login attempt: email='$email', password='$password'")

            if (email.equals("admin@kk.com", ignoreCase = true) && password == "admin123") {
                // Admin Login
                UserManager.login(this, isAdmin = true)
                Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else if (email.isNotEmpty() && password.isNotEmpty()) {
                // Customer Login
                UserManager.login(this, isAdmin = false)
                Toast.makeText(this, "Customer Login Successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}