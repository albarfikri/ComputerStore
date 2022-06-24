package com.albar.computerstore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.ItemComputerStoreListBinding

class ComputerStoreListAdapter(
    val onItemClicked: (Int, ComputerStore) -> Unit,
    val onEditClicked: (Int, ComputerStore) -> Unit,
    val onDeleteClicked: (Int, ComputerStore) -> Unit,
) : RecyclerView.Adapter<ComputerStoreListAdapter.MyViewHolder>() {

    private var list: MutableList<ComputerStore> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ComputerStoreListAdapter.MyViewHolder {
        val itemView =
            ItemComputerStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComputerStoreListAdapter.MyViewHolder, position: Int) {
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
            binding.tvName.text = item.name
            binding.tvAddress.text = item.address
            binding.edit.setOnClickListener { onEditClicked.invoke(adapterPosition, item) }
            binding.delete.setOnClickListener { onDeleteClicked.invoke(adapterPosition, item) }
            binding.itemComputerStoreListLayout.setOnClickListener {
                onItemClicked.invoke(
                    adapterPosition,
                    item
                )
            }
        }
    }
}