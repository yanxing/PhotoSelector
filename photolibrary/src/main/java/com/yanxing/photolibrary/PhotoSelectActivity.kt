package com.yanxing.photolibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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
            bundle.putInt(MAX_NUM,getIntExtra(MAX_NUM,1))
            fragment.arguments=bundle
            add(fragment,R.id.fragment)
        }
    }

    /**
     * 添加Fragment
     */
    private fun add(fragment: Fragment, id: Int) {
        val tag = fragment.javaClass.toString()
        supportFragmentManager
                .beginTransaction()
                .add(id, fragment, tag)
                .commit()
    }

}