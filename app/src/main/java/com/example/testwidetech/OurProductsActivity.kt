package com.example.testwidetech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testwidetech.adapters.TestDataAdapter
import com.example.testwidetech.databinding.ActivityOurProductsBinding
import com.example.testwidetech.rest.APIservice
import com.example.testwidetech.rest.TestData
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OurProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOurProductsBinding
    private lateinit var adapter: TestDataAdapter
    private val testData = mutableListOf<TestData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOurProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        initRecyclerView()
        getProducts()

    }

    private fun initRecyclerView() {
        adapter = TestDataAdapter(testData)
        binding.productsReciclerView.layoutManager = LinearLayoutManager(this)
        binding.productsReciclerView.adapter = adapter
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://ws4.shareservice.co/TestMobile/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getProducts(){
        doAsync {
            val call = getRetrofit().create(APIservice::class.java).getProducts("GetProductsData").execute()
            val products = call.body() as List<TestData>
            uiThread {
                if (call.isSuccessful){
                    val datas:List<TestData> = products
                    testData.clear()
                    testData.addAll(datas)
                    adapter.notifyDataSetChanged()
                    } else {
                    Toast.makeText(applicationContext, "Ha Ocurrido un Error ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners(){
        binding.backArrowProducts.setOnClickListener{
            val profileIntent: Intent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
            finish()
        }
    }
}