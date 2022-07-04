package com.albar.computerstore.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.ItemUnverifiedListBinding
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class UnverifiedListAdapter(
    val onItemClicked: (Int, ComputerStore) -> Unit,
    val onCallClicked: (Int, String) -> Unit,
    val onDetailClicked: (Int, ComputerStore) -> Unit,
    val glide: RequestManager,
    val context: Context
) : RecyclerView.Adapter<UnverifiedListAdapter.MyViewHolder>() {

    private var list: MutableList<ComputerStore> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            ItemUnverifiedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class MyViewHolder(val binding: ItemUnverifiedListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ComputerStore) {
            binding.apply {
                tvName.text = item.name
                tvAddress.text = item.address
                tvRegisteredTime.text = item.createAt.toString().replace("GMT+07:00 ", "")
                glide
                    .load(item.image)
                    .placeholder(R.drawable.ic_broke_image)
                    .transform(CenterCrop(), RoundedCorners(10))
                    .into(imgComputerStore)
                if (!item.isVerified) {
                    tvVerifiedStatus.setTextColor(ContextCompat.getColor(context, R.color.unverified))
                    tvVerifiedStatus.text = "Not verified yet"
                } else {
                    tvVerifiedStatus.setTextColor(ContextCompat.getColor(context, R.color.verified))
                    tvVerifiedStatus.text = "Already verified"
                }
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