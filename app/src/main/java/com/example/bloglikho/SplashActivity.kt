package com.example.bloglikho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.bloglikho.Register.WelcomeActivity
import com.example.bloglikho.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this,WelcomeActivity::class.java))
//            finish()
//        },2000)

        binding.splash.alpha=0f
        binding.splash.animate().setDuration(1500).alpha(1f).withEndAction{
            startActivity(Intent(this,WelcomeActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}