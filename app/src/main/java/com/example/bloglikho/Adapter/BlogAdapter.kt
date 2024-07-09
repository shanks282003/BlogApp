package com.example.bloglikho.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.R
import com.example.bloglikho.ReadMoreActivity
import com.example.bloglikho.databinding.BlogItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.security.auth.login.LoginException

class BlogAdapter(private val items: MutableList<BlogItemModel>,private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
    private val databaseReference=FirebaseDatabase.getInstance("https://blogapp-7328f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
    private val currentuser=FirebaseAuth.getInstance().currentUser


    interface OnItemClickListener{
        fun OnReadMoreClick(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=BlogItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem=items[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
            val postId=blogItemModel.postId
            binding.heading.text=blogItemModel.heading
            Glide.with(binding.profile.context).load(blogItemModel.profileImage).into(binding.profile)
            binding.username.text=blogItemModel.username
            binding.date.text=blogItemModel.date
            binding.blog.text=blogItemModel.post
            binding.likecount.text=blogItemModel.likecount.toString()


            binding.readmoreBtn.setOnClickListener {
                itemClickListener.OnReadMoreClick(blogItemModel)
            }

//            binding.root.setOnClickListener {
//                val context=binding.root.context
//                val intent= Intent(context,ReadMoreActivity::class.java)
//                intent.putExtra("blogitem",blogItemModel)
//                context.startActivity(intent)
//            }

            //check if currentuser has liked or not
            val postlikeReference = postId.let { databaseReference.child("blogs").child(it).child("likes") }
            val currentuserLiked=currentuser?.uid?.let { uid->
                postlikeReference.child(uid).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            binding.likeBtn.setImageResource(R.drawable.filled_heart)
                        } else{
                            binding.likeBtn.setImageResource(R.drawable.unliked_heart)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
            binding.likeBtn.setOnClickListener {
                if(currentuser!=null){
                    handleLikeBtnClick(postId,blogItemModel,binding)
                }
                else{
                    val context=binding.root.context
                    Toast.makeText(context,"Please Login",Toast.LENGTH_SHORT).show()
                }
            }

            //initially check the save icon
            val userRef=databaseReference.child("users").child(currentuser!!.uid)
            val postSaveRef=userRef.child("savePosts").child(postId)

            postSaveRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        binding.saveBtn.setImageResource(R.drawable.filled_save)
                    }
                    else{
                        binding.saveBtn.setImageResource(R.drawable.bookmark)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            binding.saveBtn.setOnClickListener {
                if(currentuser!=null){
                    handleSaveBtnClick(postId,blogItemModel,binding)
                }
                else{
                    val context=binding.root.context
                    Toast.makeText(context,"Please Login",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun handleLikeBtnClick(postId: String, blogItemModel: BlogItemModel,binding: BlogItemBinding) {
        val userReference=databaseReference.child("users").child(currentuser!!.uid)
        val postLikedRef= postId.let { databaseReference.child("blogs").child(it).child("likes") }

        //check if user has already liked or not
        postLikedRef.child(currentuser.uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userReference.child("likes").child(postId).removeValue()
                        .addOnSuccessListener {
                            postLikedRef.child(currentuser.uid).removeValue()
                            blogItemModel.likedBy?.remove(currentuser.uid)

                            updateLikedBtn(binding,false)

                            val newlikecount=blogItemModel.likecount-1
                            blogItemModel.likecount=newlikecount
                            databaseReference.child("blogs").child(postId).child("likecount").setValue(newlikecount)

                            notifyDataSetChanged()
                        }
                        .addOnFailureListener { e->
                            Log.e("LikeClicked", "onDataChange: Failed to unlike the blog $e", )
                        }
                }
                else{
                    userReference.child("likes").child(postId).setValue(true)
                        .addOnSuccessListener {
                            postLikedRef.child(currentuser.uid).setValue(true)
                            blogItemModel.likedBy?.add(currentuser.uid)
                            updateLikedBtn(binding,true)

                            //incr like count
                            val newlikecount=blogItemModel.likecount+1
                            blogItemModel.likecount=newlikecount
                            databaseReference.child("blogs").child(postId).child("likecount").setValue(newlikecount)
                            notifyDataSetChanged()
                        }
                        .addOnFailureListener { e->
                            Log.e("LikeClicked", "onDataChange: Failed to like the blog $e", )
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateLikedBtn(binding: BlogItemBinding, liked: Boolean) {
        if(liked){
            binding.likeBtn.setImageResource(R.drawable.unliked_heart)
        }
        else{
            binding.likeBtn.setImageResource(R.drawable.filled_heart)
        }
    }

    private fun handleSaveBtnClick(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
        val userRef=databaseReference.child("users").child(currentuser!!.uid)
        userRef.child("savePosts").child(postId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    userRef.child("savePosts").child(postId).removeValue()
                        .addOnSuccessListener {
                            val clickedBlogItem=items.find { it.postId==postId }
                            clickedBlogItem?.isSaved=false
                            notifyDataSetChanged()

                            Toast.makeText(binding.root.context,"Blog Unsaved",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(binding.root.context,"Failed to Unsave the Blog",Toast.LENGTH_SHORT).show()
                        }

                    binding.saveBtn.setImageResource(R.drawable.bookmark)
                }
                else{
                    //the blog is not saved soo save it
                    userRef.child("savePosts").child(postId).setValue(true)
                        .addOnSuccessListener {
                            val clickedBlogItem=items.find{it.postId==postId}
                            clickedBlogItem?.isSaved=true
                            notifyDataSetChanged()

                            Toast.makeText(binding.root.context,"Blog Saved",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(binding.root.context,"Failed to Save the Blog",Toast.LENGTH_SHORT).show()
                        }

                    binding.saveBtn.setImageResource(R.drawable.filled_save)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun update(savedBlogArticleList: List<BlogItemModel>) {
        items.clear()
        items.addAll(savedBlogArticleList)

        notifyDataSetChanged()
    }
}