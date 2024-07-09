package com.example.bloglikho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.set
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.databinding.ActivityEditBlogBinding
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBlogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val blogItemModel=intent.getParcelableExtra<BlogItemModel>("blogitem")

        binding.blogTitle.setText(blogItemModel?.heading)
        binding.blogDescription.setText(blogItemModel?.post)


        binding.saveblogBtn.setOnClickListener {
            val updatedTitle=binding.blogTitle.text.toString().trim()
            val updatedDescription=binding.blogDescription.text.toString().trim()

            if(updatedTitle.isEmpty() || updatedDescription.isEmpty()){
                Toast.makeText(this@EditBlogActivity,"Please Fill all the Details",Toast.LENGTH_SHORT).show()
            }
            else{
                blogItemModel?.heading=updatedTitle
                blogItemModel?.post=updatedDescription

                if(blogItemModel!=null){
                    updateDataInFirebase(blogItemModel)
                }
            }
        }
    }

    private fun updateDataInFirebase(blogItemModel: BlogItemModel) {
        val databaseRef=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("blogs")

        val postId=blogItemModel.postId
        databaseRef.child(postId).setValue(blogItemModel)
            .addOnSuccessListener {
                Toast.makeText(this@EditBlogActivity,"Blog Updated Successfully",Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@EditBlogActivity,"Oops! Something went wrong",Toast.LENGTH_SHORT).show()
            }
    }
}