package com.albar.computerstore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct
import com.albar.computerstore.databinding.ItemComputerStoreProductBinding
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ComputerStoreProductAdapter(
    val onItemClicked: (Int, ComputerStoreProduct) -> Unit,
    val glide: RequestManager
) : RecyclerView.Adapter<ComputerStoreProductAdapter.MyViewHolder>() {

    var list: MutableList<ComputerStoreProduct> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            ItemComputerStoreProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<ComputerStoreProduct>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun removeList(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: ItemComputerStoreProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ComputerStoreProduct) {
            binding.apply {
                glide
                    .load(item.productImage)
                    .placeholder(R.drawable.ic_broke_image)
                    .transform(CenterCrop(), RoundedCorners(10))
                    .into(ivProduct)
                tvProduct.text = item.productName
                tvPrice.text = item.productPrice
                cvComputerProduct.setOnClickListener {
                    onItemClicked.invoke(adapterPosition, item)
                }
            }
        }
    }
}