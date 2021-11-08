package com.chitranjank.notepad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        supportActionBar?.title = "Register"

        auth = FirebaseAuth.getInstance()

        val emailEdt: EditText = findViewById(R.id.login_email_edt)
        val passEdt: EditText = findViewById(R.id.login_pin_edt)

        val btnLogin: Button = findViewById(R.id.login_btn)
        val btnSignUp: Button = findViewById(R.id.btn_sign_up)

        if (firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        btnLogin.setOnClickListener { v ->
            val email = emailEdt.text.toString().trim()
            val pass = passEdt.text.toString().trim()

            if (!email.isEmpty() && !pass.isEmpty()) {
                login_with_email_and_pin(email, pass)
            } else {
                Toast.makeText(this, "Both fields are required!", Toast.LENGTH_LONG).show()
            }
        }

        btnSignUp.setOnClickListener { v ->
            val email = emailEdt.text.toString().trim()
            val pass = passEdt.text.toString().trim()

            if (!email.isEmpty() && !pass.isEmpty()) {
                create_user_with_email_and_pin(email, pass)
            } else {
                Toast.makeText(this, "Both fields are required!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun create_user_with_email_and_pin(email: String, pin: String) {
        auth.createUserWithEmailAndPassword(email, pin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account created!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun login_with_email_and_pin(email: String, pin: String) {
        auth.signInWithEmailAndPassword(email, pin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}