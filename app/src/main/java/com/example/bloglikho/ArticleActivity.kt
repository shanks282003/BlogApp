package com.example.bloglikho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloglikho.Adapter.ArticleAdapter
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.databinding.ActivityArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding
    private lateinit var databaseReference: DatabaseReference
    private val auth=FirebaseAuth.getInstance()
    private lateinit var blogAdapter: ArticleAdapter
    private val EDIT_BLOG_REQUEST_CODE=123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val userid=auth.currentUser?.uid
        val recyclerView=binding.articlerRecyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)

        if (userid!=null){
            blogAdapter= ArticleAdapter(this, emptyList(),object: ArticleAdapter.OnItemClickListener{
                override fun OnEditClick(blogItem: BlogItemModel) {
                    val intent= Intent(this@ArticleActivity,EditBlogActivity::class.java)
                    intent.putExtra("blogitem",blogItem)
                    startActivityForResult(intent,EDIT_BLOG_REQUEST_CODE)
                }

                override fun OnDeleteClick(blogItem: BlogItemModel) {
                    val postid=blogItem.postId
                    val blog=databaseReference.child(postid)

                    blog.removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this@ArticleActivity,"Blog Deleted Successfully",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ArticleActivity,"Failed to Delete",Toast.LENGTH_SHORT).show()
                        }
                }

                override fun OnReadMoreClick(blogItem: BlogItemModel) {
                    val intent= Intent(this@ArticleActivity,ReadMoreActivity::class.java)
                    intent.putExtra("blogitem",blogItem)
                    startActivity(intent)
                }

            })
        }

        blogAdapter= ArticleAdapter(this, emptyList(),object: ArticleAdapter.OnItemClickListener{
            override fun OnEditClick(blogItem: BlogItemModel) {
                val intent= Intent(this@ArticleActivity,EditBlogActivity::class.java)
                intent.putExtra("blogitem",blogItem)
                startActivityForResult(intent,EDIT_BLOG_REQUEST_CODE)
            }

            override fun OnDeleteClick(blogItem: BlogItemModel) {
                val postid=blogItem.postId
                val blog=databaseReference.child(postid)

                blog.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this@ArticleActivity,"Blog Deleted Successfully",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@ArticleActivity,"Failed to Delete",Toast.LENGTH_SHORT).show()
                    }
            }

            override fun OnReadMoreClick(blogItem: BlogItemModel) {
                val intent= Intent(this@ArticleActivity,ReadMoreActivity::class.java)
                intent.putExtra("blogitem",blogItem)
                startActivity(intent)
            }

        })
        recyclerView.adapter=blogAdapter

        databaseReference=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference.child("blogs")

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogSaveList=ArrayList<BlogItemModel>()
                for(postsnapshot in snapshot.children){
                    val blogSaved=postsnapshot.getValue(BlogItemModel::class.java)
                    if(blogSaved!=null && userid==blogSaved.userid){
                        blogSaveList.add(blogSaved)
                    }
                }
                blogAdapter.setData(blogSaveList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}