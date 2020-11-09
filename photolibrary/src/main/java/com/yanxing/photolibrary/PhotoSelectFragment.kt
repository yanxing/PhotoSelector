package com.yanxing.photolibrary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_photo_select.*

/**
 * 图片/视频选择，兼容Android10存储权限
 * @author 李双祥 on 2020/11/9.
 */
class PhotoSelectFragment:Fragment() {

    /**
     * 是否显示相机，允许拍照
     */
    private var showCamera=false
    /**
     * true多选，false单选
     */
    private var selectMultiple=false
    /**
     * 最大选择图片，默认9张，selectMultiple为true有效
     */
    private var defaultNumber=9
    /**
     * 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
     */
    private var loadMediaType=1
    /**
     * 限制选择视频时长12秒
     */
    private var limitVideoDuration=12

    private val photoList=ArrayList<Photo>()

    private val photoAdapter by lazy {
        photoRecyclerView.layoutManager=GridLayoutManager(activity,4)
        object :RecyclerViewAdapter<Photo>(photoList,R.layout.item_photo){
            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)



            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_photo_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }

    private fun initView(){
        cancel.setOnClickListener { activity?.finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}