package com.albar.computerstore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.ItemComputerStoreListBinding
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ComputerStoreListAdapter(
    val onItemClicked: (Int, ComputerStore) -> Unit,
    val onCallClicked: (Int, String) -> Unit,
    val onDetailClicked: (Int, ComputerStore) -> Unit,
    val glide: RequestManager
) : RecyclerView.Adapter<ComputerStoreListAdapter.MyViewHolder>() {

    private var list: MutableList<ComputerStore> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            ItemComputerStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<ComputerStore>) {
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

    inner class MyViewHolder(val binding: ItemComputerStoreListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ComputerStore) {
            binding.apply {
                tvName.text = item.name
                tvAddress.text = item.address
                glide
                    .load(item.image)
                    .placeholder(R.drawable.ic_broke_image)
                    .transform(CenterCrop(), RoundedCorners(10))
                    .into(imgComputerStore)
                detail.setOnClickListener { onDetailClicked.invoke(adapterPosition, item) }
                call.setOnClickListener {
                    onCallClicked.invoke(
                        adapterPosition,
                        item.phone
                    )
                }
                itemComputerStoreListLayout.setOnClickListener {
                    onItemClicked.invoke(
                        adapterPosition,
                        item
                    )
                }
            }
        }
    }
}