package com.example.testwidetech.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.testwidetech.databinding.ItemTestDataBinding
import com.example.testwidetech.rest.TestData
import com.squareup.picasso.Picasso

class TestDataViewHolder(view: View):RecyclerView.ViewHolder(view) {

    private val binding = ItemTestDataBinding.bind(view)
    fun bind (data:TestData){
        binding.tvProductName.text = data.Name
        binding.tvProductDescription.text = data.Description
        Picasso.get().load(data.ImageUrl).into(binding.tvProductImage)

    }

}