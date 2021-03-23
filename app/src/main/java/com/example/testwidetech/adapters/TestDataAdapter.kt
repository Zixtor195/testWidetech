package com.example.testwidetech.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testwidetech.R
import com.example.testwidetech.rest.TestData

class TestDataAdapter (val testData: List<TestData>):RecyclerView.Adapter<TestDataViewHolder>(){

    override fun getItemCount(): Int = testData.size

    override fun onBindViewHolder(holder: TestDataViewHolder, position: Int) {
        val item:TestData = testData[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestDataViewHolder {
        val layoutInflater:LayoutInflater = LayoutInflater.from(parent.context)
        return TestDataViewHolder(layoutInflater.inflate(R.layout.item_test_data,parent, false))
    }




}