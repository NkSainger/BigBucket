package com.nikhil.bigbucket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikhil.bigbucket.data.Product
import com.nikhil.bigbucket.databinding.BestDealsRvItemBinding

class BestDealsProductsAdapter :
    RecyclerView.Adapter<BestDealsProductsAdapter.BestDealsProductsViewHolder>() {

    inner class BestDealsProductsViewHolder(private val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            val newPrice = product.price - (product.price * product.offerPercentage!!)
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(bestDealProductImage)
                productNewPrice.text = newPrice.toString()
                tvOldPrice.text = product.price.toString()
                tvDealProductName.text = product.name
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsProductsViewHolder {
        return BestDealsProductsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }
}