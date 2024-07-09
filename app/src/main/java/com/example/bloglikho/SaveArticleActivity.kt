package com.example.bloglikho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloglikho.Adapter.BlogAdapter
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.databinding.ActivityAddArticleBinding
import com.example.bloglikho.databinding.ActivitySaveArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SaveArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveArticleBinding
    private val savedBlogArticleList= mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySaveArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        blogAdapter=BlogAdapter(savedBlogArticleList.filter { it.isSaved }.toMutableList(),object : BlogAdapter.OnItemClickListener{
            override fun OnReadMoreClick(blogItem: BlogItemModel) {
                val intent= Intent(this@SaveArticleActivity,ReadMoreActivity::class.java)
                intent.putExtra("blogitem",blogItem)
                startActivity(intent)
            }

        })
        binding.savedArticleRecyclerView.adapter=blogAdapter
        binding.savedArticleRecyclerView.layoutManager=LinearLayoutManager(this)

        val userid=auth.currentUser?.uid

        if(userid!=null){
            val userRef=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(userid).child("savePosts")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children){
                        val postId=postSnapshot.key
                        val isSaved=postSnapshot.value as Boolean

                        if(postId!=null && isSaved){
                            CoroutineScope(Dispatchers.IO).launch{
                                val blogItem=fetchBlogitem(postId)
                                if(blogItem!=null){
                                    savedBlogArticleList.add(blogItem)

                                    launch (Dispatchers.Main){
                                        blogAdapter.update(savedBlogArticleList)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
        binding.backbutton.setOnClickListener {
            finish()
        }
    }

    private suspend fun fetchBlogitem(postId: String): BlogItemModel? {
        val blogReference=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("blogs")

        return try {
            val dataSnapshot=blogReference.child(postId).get().await()
            val blogdata=dataSnapshot.getValue(BlogItemModel::class.java)
            blogdata
        }catch (e:Exception){
            null
        }
    }
}