package com.example.bloglikho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.Model.UserData
import com.example.bloglikho.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddArticleBinding
    private var auth:FirebaseAuth=FirebaseAuth.getInstance()
    private var databaseRef: DatabaseReference=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
    private var userRef: DatabaseReference=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addblogBtn.setOnClickListener {
            val title=binding.blogTitle.editableText.toString().trim()
            val description=binding.blogDescription.editableText.toString().trim()

            if(title.isEmpty() || description.isEmpty()){
                Toast.makeText(this,"Please fill all the details",Toast.LENGTH_SHORT).show()
            }

            val currentuser=auth.currentUser

            if(currentuser!=null){
                val userid=currentuser.uid
                val username=currentuser.displayName?:"Anonymous"
                val userImage=currentuser.photoUrl?:""

                userRef.child(userid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userdata=snapshot.getValue(UserData::class.java)

                        if(userdata!=null){
                            val usernamefrmDB=userdata.name
                            val userImageurl=userdata.profileImage

                            val currentdate=SimpleDateFormat("yyy-MM-dd").format(Date())

                            val blogitem=BlogItemModel(
                                title,
                                usernamefrmDB,
                                currentdate,
                                userid,
                                description,
                                0,
                                userImageurl
                            )
                            //generate unique key for blog post
                            val key=databaseRef.push().key
                            if(key!=null){
                                blogitem.postId=key
                                val blogref=databaseRef.child(key)
                                blogref.setValue(blogitem).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        finish()
                                    }
                                    else{
                                        Toast.makeText(this@AddArticleActivity,"Failed to add blog",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }
}