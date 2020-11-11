package com.yanxing.photo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yanxing.photolibrary.PhotoSelectActivity
import com.yanxing.photolibrary.model.SELECT_MODE
import com.yanxing.photolibrary.model.SHOW_CAMERA
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.setOnClickListener {
            val intent=Intent(this, PhotoSelectActivity::class.java)
            intent.putExtra(SHOW_CAMERA,true)
            intent.putExtra(SELECT_MODE,true)
            startActivity(intent)
        }
    }
}