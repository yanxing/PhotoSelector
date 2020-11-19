package com.yanxing.photo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yanxing.photolibrary.PhotoSelectorEngine
import com.yanxing.photolibrary.model.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.setOnClickListener {
            PhotoSelectorEngine
                .create(this)
                .setShowCamera(true)
                .setSelectMultiple(true)
                .setLoadMediaType(0)
                .setLimitVideoDuration(30)
                .start(PhotoSelectorEngine.REQUEST_PHOTO_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK&&requestCode==PhotoSelectorEngine.REQUEST_PHOTO_CODE) {
            val photos=PhotoSelectorEngine.getResult(data)
            photos?.forEach {
                showToast(applicationContext,it.path.toString())
            }
        }
    }
}