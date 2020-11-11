package com.yanxing.photolibrary

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.yanxing.photolibrary.model.*
import com.yanxing.photolibrary.util.PermissionUtil
import com.yanxing.photolibrary.util.TakePhotoUtil
import com.yanxing.photolibrary.util.getPhotos
import kotlinx.android.synthetic.main.fragment_photo_select.*

/**
 * 图片/视频选择，兼容Android10存储权限
 * @author 李双祥 on 2020/11/9.
 */
class PhotoSelectFragment : Fragment() {

    /**
     * 是否显示相机拍照，多选情况下，理应不显示相机，如果此时显示相机拍照，将只返回拍照图片的路径
     */
    private var showCamera = false

    /**
     * true多选，false单选
     */
    private var selectMultiple = false

    /**
     * 最大选择图片，默认9张，selectMultiple为true有效，一次性最大只能为9张
     */
    private var maxNumber = 9

    /**
     * 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
     */
    private var loadMediaType = 1

    /**
     * 限制选择视频时长
     */
    private var limitVideoDuration = 12
    private val QUESTION_AUTH = 1

    /**
     * 当前展示的图片视频
     */
    private val photoList = ArrayList<Photo>()

    /**
     * 全部图片/视频
     */
    private var photoFolderList = ArrayList<PhotoFolder>()

    /**
     * 已经选择的图片/视频
     */
    private var photoSelectedList = ArrayList<Photo>()
    private val queryThread = HandlerThread("query_thread")
    private lateinit var queryHandler: Handler
    private lateinit var photoAdapter: RecyclerViewAdapter<Photo>
    private var takePhotoImage:Uri?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_photo_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPhotoConfig()
        initView()
        checkPermission()
    }

    private fun initView() {
        cancel.setOnClickListener { activity?.finish() }
        val width = activity?.let { (getScreenMetrics(it).widthPixels - dp2px(it, 2) * 5) / 4 }
        photoRecyclerView.layoutManager = GridLayoutManager(activity, 4)
        photoAdapter = object : RecyclerViewAdapter<Photo>(photoList, R.layout.item_photo) {
            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                mDataList[position].apply {
                    holder.findViewById<ImageView>(R.id.image).apply {
                        //即使showCamera为true仅在全部图片里面显示相机
                        if (type == -1) {
                            Glide.with(this@PhotoSelectFragment).load(R.mipmap.camera_icon)
                                .override(width!!, width).into(this)
                        } else {
                            Glide.with(this@PhotoSelectFragment).load(path).override(width!!, width)
                                .into(this)
                        }
                    }
                    if (type == 2) {
                        //视频，显示时长
                        holder.findViewById<TextView>(R.id.duration).apply {
                            visibility = View.VISIBLE
                            text = formatDuration(duration)
                        }
                    } else {
                        holder.findViewById<TextView>(R.id.duration).visibility = View.GONE
                    }
                    if (select) {

                    }
                }
            }
        }
        photoRecyclerView.adapter = photoAdapter
        photoAdapter.setOnItemClick { viewHolder, position ->
            photoList[position].apply {
                //多选
                if (selectMultiple) {
                    //点击是相机
                    if (type == -1) {
                        takePhotoImage=TakePhotoUtil.takePhoto(activity)
                    }
                }
            }

        }
    }

    /**
     * 参数配置
     */
    private fun setPhotoConfig() {
        arguments?.apply {
            showCamera = getBoolean(SHOW_CAMERA, true)
            selectMultiple = getBoolean(SELECT_MODE, false)
            maxNumber = getInt(MAX_NUM, 9)
            loadMediaType = getInt(LOAD_MEDIA_TYPE, 1)
            limitVideoDuration = getInt(LIMIT_VIDEO_DURATION, 12)
            //校验传入的参数
            if (loadMediaType != 0 && loadMediaType != 1 && loadMediaType != 2) {
                loadMediaType = 1
            }
            if (selectMultiple) {
                if (maxNumber > 9 || maxNumber < 1) {
                    maxNumber = 9
                }
            }
            if (limitVideoDuration < 1) {
                limitVideoDuration = 12
            }
        }
    }

    /**
     * 申请权限
     */
    private fun checkPermission() {
        if (PermissionUtil.findNeedRequestPermissions(activity,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)).isNotEmpty()) {
            PermissionUtil.requestPermission(this,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),
                QUESTION_AUTH)
        } else {
            getPhotoVideo()
        }
    }

    /**
     * 查询本地图片和视频
     */
    private fun getPhotoVideo() {
        progressBar.visibility = View.VISIBLE
        queryThread.start()
        queryHandler = object : Handler(queryThread.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                activity?.let {
                    photoFolderList = getPhotos(it, loadMediaType)
                    if (showCamera) {
                        val photo = Photo(null, -1)
                        photoFolderList[0].photos.add(photo)
                    }
                    title.post {
                        photoFolderList[0].name
                    }
                    photoList.addAll(photoFolderList[0].photos)
                    photoRecyclerView.post { photoAdapter.notifyDataSetChanged() }
                    progressBar.post { progressBar.visibility = View.GONE }
                }
            }
        }
        queryHandler.sendEmptyMessage(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        queryThread.quit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK){
            if (requestCode==TakePhotoUtil.TAKE_PHOTO){
                val photo=Photo(takePhotoImage)
                photoSelectedList.add(photo)
//                val data = Intent()
//                data.putExtra(Activity.RESULT_OK,photoSelectedList)
//                activity?.setResult(Activity.RESULT_OK, data)
//                activity?.finish()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == QUESTION_AUTH) {
            var grant = true
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    showToast(activity!!, "未授权")
                    grant = false
                    break
                }
            }
            if (grant) {
                getPhotoVideo()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}