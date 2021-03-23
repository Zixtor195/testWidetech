package com.example.testwidetech

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object ImageController {
    fun selectPhotoFromGalery(activity: Activity, code: Int){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, code)
    }

    fun saveImage(context: Context, id: Long, uri: Uri){
        val file = File(context.filesDir, id.toString())
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()!!
        file.writeBytes(bytes)
    }

    fun createImageFile(context: Context, id: Long) {



    }

    fun getImageUri(context: Context, id: Long): Uri{
        val file= File(context.filesDir, id.toString())
        return if(file.exists()) Uri.fromFile(file)
        else Uri.parse("android.resource:://com.example.testwidetech/drawable/user_image")
    }

    fun takePhotoFromCamera(activity: Activity, code: Int){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                activity.startActivityForResult(takePictureIntent, code)
            }
        }
    }

}
