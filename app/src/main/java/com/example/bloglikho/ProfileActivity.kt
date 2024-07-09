package com.example.bloglikho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bloglikho.Register.WelcomeActivity
import com.example.bloglikho.databinding.ActivityMainBinding
import com.example.bloglikho.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addblogBtn.setOnClickListener {
            startActivity(Intent(this,AddArticleActivity::class.java))
        }

        binding.yourarticleBtn.setOnClickListener {
            startActivity(Intent(this,ArticleActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,WelcomeActivity::class.java))
            finish()
        }

        databaseReference=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference.child("users")
        auth=FirebaseAuth.getInstance()

        val userid=auth.currentUser!!.uid

        if(userid!=null){
            loadUserProfileData(userid)
        }
    }

    private fun loadUserProfileData(userid: String) {
        val userRef=databaseReference.child(userid)

        //load userprofile image
        userRef.child("profileImage").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileUrl=snapshot.getValue(String::class.java)
                if(profileUrl!=null){
                    Glide.with(this@ProfileActivity).load(profileUrl)
                        .into(binding.userProfile)
                }
                else{
                    Toast.makeText(this@ProfileActivity,"Failed to Profile Image",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        userRef.child("name").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName=snapshot.getValue(String::class.java)
                if(userName!=null){
                    binding.username.text=userName
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}