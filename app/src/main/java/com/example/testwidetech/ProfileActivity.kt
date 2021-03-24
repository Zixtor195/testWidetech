package com.example.testwidetech

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.testwidetech.databinding.ActivityProfileBinding
import com.example.testwidetech.db.DataBaseRoom
import com.example.testwidetech.db.UserInfoDB
import com.example.testwidetech.rest.APIservice
import com.example.testwidetech.rest.UserInfo
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

enum class ProviderType{
    BASIC,
    GOOGLE
}

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var imageUri : Uri
    private val GET_IMAGE_GALERY = 1
    private val GET_IMAGE_CAMERA = 0

    companion object {
        const val REQUEST_CODE_CAMERA = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = Room.databaseBuilder(this, DataBaseRoom::class.java, "database-room").build()

        imageUri = ImageController.getImageUri(this, 10)
        binding.imageProfileImageView.setImageURI(imageUri)


        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")

        val preferences: SharedPreferences.Editor? = getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        ).edit()
        preferences?.putString("email", email)
        preferences?.putString("provider", provider)
        preferences?.apply()
        doAsync {
            if (email != null && provider != null) {
                addUserRest(email, provider)
                app.DaoRoom().addUser(UserInfoDB(email, provider))
            }
        }

        binding.productsButton.setOnClickListener {
            val profileIntent: Intent = Intent(this, OurProductsActivity::class.java)
            startActivity(profileIntent)
        }

        binding.contactButton.setOnClickListener {
            val profileIntent: Intent = Intent(this, ContactActivity::class.java)
            startActivity(profileIntent)
        }

        binding.uploadImageButton.setOnClickListener{
            var opciones = arrayOf("Tomar Foto", "Elegir de Galeria", "Cancelar")
            setupPermissions()
            val builder = AlertDialog.Builder(this@ProfileActivity)
            with(builder){
                setTitle("Selecciona una Opcion")
                setItems(opciones, { dialog, which ->
                    when (which){
                        0 -> ImageController.takePhotoFromCamera(
                            this@ProfileActivity as Activity,
                            GET_IMAGE_CAMERA
                        )
                        1 -> ImageController.selectPhotoFromGalery(
                            this@ProfileActivity as Activity,
                            GET_IMAGE_GALERY
                        )
                        2 -> closeContextMenu()
                    }
                })
            }
            builder.show()
        }
    }

    private fun cameraPermissionCheck() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun setupPermissions() {
        if (cameraPermissionCheck()) {
        } else {
            ActivityCompat.requestPermissions(this@ProfileActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when{
            requestCode == GET_IMAGE_GALERY && resultCode == Activity.RESULT_OK ->{
                imageUri = data!!.data!!
                binding.imageProfileImageView.setImageURI(imageUri)
                ImageController.saveImage(this@ProfileActivity, 10, imageUri)
            }
            requestCode == GET_IMAGE_CAMERA && resultCode == Activity.RESULT_OK -> {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.imageProfileImageView.setImageBitmap(imageBitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            REQUEST_CODE_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(applicationContext,"Debes Aceptar los Permisos para Usar esta Funcion ", Toast.LENGTH_SHORT).show()
            } else -> {
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ws4.shareservice.co/TestMobile/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun addUserRest(email:String, provider:String) {
        doAsync {
            var userInfo = UserInfo(email, provider)
            val call =
                getRetrofit().create(APIservice::class.java).addUser("Login", userInfo).execute()
            val products = call.body() as String
            uiThread {
                if (!call.isSuccessful) {
                    Toast.makeText(applicationContext, "Ha Ocurrido un Error ", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}