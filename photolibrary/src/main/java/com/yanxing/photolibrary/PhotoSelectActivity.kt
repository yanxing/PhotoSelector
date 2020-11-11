package com.yanxing.photolibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yanxing.photolibrary.model.*

/**
 * 图片/视频选择，兼容Android10存储权限
 * @author 李双祥 on 2020/11/9.
 */
class PhotoSelectActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_select)

        intent.apply {
            val fragment=PhotoSelectFragment()
            val bundle=Bundle()
            bundle.putBoolean(SHOW_CAMERA,getBooleanExtra(SHOW_CAMERA,true))
            bundle.putBoolean(SELECT_MODE,getBooleanExtra(SELECT_MODE,false))
            bundle.putInt(MAX_NUM,getIntExtra(MAX_NUM,9))
            bundle.putInt(LOAD_MEDIA_TYPE,getIntExtra(LOAD_MEDIA_TYPE,1))
            bundle.putInt(LIMIT_VIDEO_DURATION,30)
            fragment.arguments=bundle
            add(fragment,R.id.fragment)
        }
    }

    private fun add(fragment: Fragment, id: Int) {
        val tag = fragment.javaClass.toString()
        supportFragmentManager
                .beginTransaction()
                .add(id, fragment, tag)
                .commit()
    }

}