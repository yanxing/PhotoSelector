package com.yanxing.photolibrary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
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

    /**
     * 当前展示的图片视频
     */
    private val photoList=ArrayList<Photo>()
    private val queryThread = HandlerThread("query_thread")
    private lateinit var queryHandler:Handler
    /**
     * 全部图片
     */
    private var photoFolderMap=ArrayMap<String, PhotoFolder>()

    private val photoAdapter by lazy {
        photoRecyclerView.layoutManager=GridLayoutManager(activity, 4)
        object :RecyclerViewAdapter<Photo>(photoList, R.layout.item_photo){
            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                mDataList[position].apply {
                    Glide.with(this@PhotoSelectFragment).load(path).into(holder.findViewById(R.id.image))
                    if (type==2){
                        //视频，显示时长
                        holder.findViewById<TextView>(R.id.duration).apply {
                            visibility=View.VISIBLE
                            text= formatDuration(duration)
                        }
                    }else{
                        holder.findViewById<TextView>(R.id.duration).visibility=View.GONE
                    }
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_photo_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun initView(){
        cancel.setOnClickListener { activity?.finish() }
        photoRecyclerView.adapter=photoAdapter
        progressBar.show()
    }

    /**
     * 查询本地图片和视频
     */
    private fun getPhotoVideo(){
        queryThread.start()
        queryHandler=object :Handler(queryThread.looper){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                activity?.let {
                    photoFolderMap= getPhotos(it,loadMediaType)
                    for (photoFolder in photoFolderMap){
                        if (photoFolder.value.isSelected){

                        }
                    }
                }

            }
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}