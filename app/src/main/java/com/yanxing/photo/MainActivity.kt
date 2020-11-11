package com.yanxing.photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yanxing.photolibrary.PhotoSelectorEngine
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.setOnClickListener {
            PhotoSelectorEngine
                .setShowCamera(true)
                .setSelectMultiple(true)
                .start(this)
        }
    }
}