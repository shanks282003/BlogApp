package com.example.bloglikho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bloglikho.Adapter.BlogAdapter
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val blogitems= mutableListOf<BlogItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        databaseRef=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("blogs")

        binding.addArticleButton.setOnClickListener{
            startActivity(Intent(this,AddArticleActivity::class.java))
        }

        binding.profileImage.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        binding.saveArticleBtn.setOnClickListener {
            startActivity(Intent(this,SaveArticleActivity::class.java))
        }

        binding.cardView2.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        val userid=auth.currentUser?.uid
        if(userid!=null){
            loadUserProfileImg(userid)
        }

        //set recyclerview
        val recyclerView=binding.blogRecyclerView
        val blogAdapter=BlogAdapter(blogitems,object : BlogAdapter.OnItemClickListener{
            override fun OnReadMoreClick(blogItem: BlogItemModel) {
                val intent= Intent(this@MainActivity,ReadMoreActivity::class.java)
                intent.putExtra("blogitem",blogItem)
                startActivity(intent)
            }

        })
        recyclerView.adapter=blogAdapter
        recyclerView.layoutManager=LinearLayoutManager(this)

        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                blogitems.clear()
                for(snapshot in snapshot.children){
                    val blogItem=snapshot.getValue(BlogItemModel::class.java)

                    if(blogItem!=null){
                        blogitems.add(blogItem)
                    }
                }

                blogitems.reverse()
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Error Loading Blog",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun loadUserProfileImg(userid: String) {
        val userRef=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userid)

        userRef.child("profileImage").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl=snapshot.getValue(String::class.java)

                if(profileImageUrl!=null){
                    Glide.with(this@MainActivity).load(profileImageUrl).into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Error Loading ProfileImage",Toast.LENGTH_SHORT).show()
            }

        })
    }
}