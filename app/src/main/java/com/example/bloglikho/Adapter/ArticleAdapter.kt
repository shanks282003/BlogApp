package com.example.bloglikho.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloglikho.Model.BlogItemModel
import com.example.bloglikho.databinding.ArticleItemBinding

class ArticleAdapter(
    private val context: Context,
    private var bloglist: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
): RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    interface OnItemClickListener{
        fun OnEditClick(blogItem: BlogItemModel)
        fun OnDeleteClick(blogItem: BlogItemModel)
        fun OnReadMoreClick(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=ArticleItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bloglist.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem=bloglist[position]
        holder.bind(blogItem)
    }

    fun setData(blogSaveList: ArrayList<BlogItemModel>) {
        this.bloglist=blogSaveList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(private val binding: ArticleItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: BlogItemModel) {
            binding.heading.text=blogItem.heading
            Glide.with(binding.profile.context).load(blogItem.profileImage).into(binding.profile)
            binding.username.text=blogItem.username
            binding.date.text=blogItem.date
            binding.blog.text=blogItem.post

            binding.readmoreBtn.setOnClickListener {
                itemClickListener.OnReadMoreClick(blogItem)
            }
            binding.deleteBtn.setOnClickListener {
                itemClickListener.OnDeleteClick(blogItem)
            }
            binding.editBtn.setOnClickListener {
                itemClickListener.OnEditClick(blogItem)
            }
        }

    }
}