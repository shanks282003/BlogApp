package com.example.bloglikho.Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bloglikho.MainActivity
import com.example.bloglikho.R
import com.example.bloglikho.SignInandRegisterActivity
import com.example.bloglikho.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        binding.buttonLogin.setOnClickListener {
            val intent=Intent(this,SignInandRegisterActivity::class.java)
            intent.putExtra("action","login")
            startActivity(intent)
        }
        binding.buttonRegister.setOnClickListener {
            val intent=Intent(this,SignInandRegisterActivity::class.java)
            intent.putExtra("action","register")
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}