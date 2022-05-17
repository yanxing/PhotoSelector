package com.yanxing.photo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.yanxing.photo.databinding.ActivityMainBinding
import com.yanxing.photoselector.PhotoSelectorEngine
import com.yanxing.photoselector.model.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.setOnClickListener {
            PhotoSelectorEngine
                .create(this)
                .setShowCamera(false)
                .setSelectMultiple(true)
                .setLoadMediaType(0)
                .start(PhotoSelectorEngine.REQUEST_PHOTO_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK&&requestCode==PhotoSelectorEngine.REQUEST_PHOTO_CODE) {
            val photos=PhotoSelectorEngine.getResult(data)
            photos?.forEach {
                Glide.with(this).load(it.path).into(binding.image)
                showToast(applicationContext,it.path.toString())
            }
        }
    }
}