package com.example.bloglikho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReadMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbutton.setOnClickListener {
            finish()
        }

        val blog=intent.getParcelableExtra<BlogItemModel>("blogitem")

        if(blog!=null){
            binding.titletext.text=blog.heading
            binding.userName.text=blog.username
            binding.blogDescription.text=blog.post
            binding.date.text=blog.date
            Glide.with(this).load(blog.profileImage).apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)
        }
        else{
            Toast.makeText(this@ReadMoreActivity,"Failed to load",Toast.LENGTH_SHORT).show()
        }
    }
}