package com.yanxing.photolibrary

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.yanxing.photolibrary.model.*
import com.yanxing.photolibrary.util.PermissionUtil
import com.yanxing.photolibrary.util.TakePhotoUtil
import com.yanxing.photolibrary.util.getPhotos
import kotlinx.android.synthetic.main.activity_photo_select.*

/**
 * 图片/视频选择，兼容Android10存储权限
 * @author 李双祥 on 2020/11/9.
 */
class PhotoSelectActivity : AppCompatActivity() {

    /**
     * 是否显示相机拍照，多选情况下，理应不显示相机，如果此时显示相机拍照，拍照后将返回结果并关闭
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
    private val currentPhotoList = ArrayList<Photo>()

    /**
     * 全部图片/视频
     */
    private var allPhotoFolderList = ArrayList<PhotoFolder>()

    /**
     * 已经选择的图片/视频
     */
    private var photoSelectedList = ArrayList<Photo>()
    private val queryThread = HandlerThread("query_thread")
    private lateinit var queryHandler: Handler
    private lateinit var photoAdapter: RecyclerViewAdapter<Photo>
    private var takePhotoImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_select)
        setPhotoConfig()
        initView()
        checkPermission()
    }

    /**
     * 参数配置
     */
    private fun setPhotoConfig() {
        intent?.apply {
            showCamera = getBooleanExtra(SHOW_CAMERA, true)
            selectMultiple = getBooleanExtra(SELECT_MODE, false)
            maxNumber = getIntExtra(MAX_NUM, 9)
            loadMediaType = getIntExtra(LOAD_MEDIA_TYPE, 1)
            limitVideoDuration = getIntExtra(LIMIT_VIDEO_DURATION, 12)
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

    private fun initView() {
        cancel.setOnClickListener {
            finish()
        }
        if (!selectMultiple) {
            confirm.visibility = View.GONE
        }
        val popWindowFolder = PopWindowFolder()
        //点击标题，真是图片视频文件夹
        titleTxt.setOnClickListener {
            arrow.rotation=180f
            popWindowFolder.showFolder(this, titleTxt, allPhotoFolderList) {
                if (it < allPhotoFolderList.size) {
                    titleTxt.text = formatString(allPhotoFolderList[it].name)
                    currentPhotoList.clear()
                    currentPhotoList.addAll(allPhotoFolderList[it].photos)
                    photoAdapter.update(currentPhotoList)
                }
            }
            popWindowFolder.popupWindow.setOnDismissListener {
                arrow.rotation=0f
            }
        }
        //点击确定
        confirm.setOnClickListener {
            if (photoSelectedList.size > 0) {
                val intent = Intent()
                intent.putParcelableArrayListExtra(PHOTO_KEY, photoSelectedList)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        val width = (getScreenMetrics(this).widthPixels - dp2px(this, 2) * 5) / 4
        photoRecyclerView.layoutManager = GridLayoutManager(this, 4)
        photoAdapter = object : RecyclerViewAdapter<Photo>(currentPhotoList, R.layout.item_photo) {
            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                mDataList[position].apply {
                    holder.findViewById<ImageView>(R.id.image).apply {
                        //即使showCamera为true仅在全部图片里面显示相机
                        if (type == -1) {
                            Glide.with(this@PhotoSelectActivity).load(R.mipmap.camera_icon)
                                .override(width, width).into(this)
                        } else {
                            Glide.with(this@PhotoSelectActivity).load(path).override(width!!, width)
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
                    holder.findViewById<MaterialButton>(R.id.state).apply {
                        //已选择的
                        if (select) {
                            backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.check))
                            strokeColor = ColorStateList.valueOf(resources.getColor(R.color.check))
                            setTextColor(resources.getColor(android.R.color.white))
                            if (photoSelectedList.size > 0) {
                                for (i in 0 until photoSelectedList.size) {
                                    if (path != null && path == photoSelectedList[i].path) {
                                        text = (i + 1).toString()
                                        break
                                    }
                                }
                            }
                        } else {
                            backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(android.R.color.transparent))
                            strokeColor =
                                ColorStateList.valueOf(resources.getColor(android.R.color.white))
                            text = ""
                            setTextColor(resources.getColor(android.R.color.white))
                        }
                    }
                }
            }
        }
        photoRecyclerView.adapter = photoAdapter
        photoAdapter.setOnItemClick { viewHolder, position ->
            currentPhotoList[position].apply {
                //多选
                if (selectMultiple) {
                    //点击是相机
                    if (type == -1) {
                        takePhotoImage = TakePhotoUtil.takePhoto(this@PhotoSelectActivity)
                    } else {
                        viewHolder.findViewById<MaterialButton>(R.id.state).let {
                            if (select) {
                                photoSelectedList.remove(this)
                                confirm.text =
                                    "确定(" + photoSelectedList.size + "/" + maxNumber + ")"
                            } else {
                                if (photoSelectedList.size < maxNumber) {
                                    photoSelectedList.add(this)
                                    confirm.text =
                                        "确定(" + photoSelectedList.size + "/" + maxNumber + ")"
                                } else {
                                    showToast(applicationContext,
                                        "最多只能选择" + maxNumber.toString() + "个")
                                }
                            }
                            select = !select
                            photoAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }


    /**
     * 申请权限
     */
    private fun checkPermission() {
        if (PermissionUtil.findNeedRequestPermissions(this,
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
                allPhotoFolderList = getPhotos(this@PhotoSelectActivity, loadMediaType)
                if (showCamera) {
                    val photo = Photo(null, -1)
                    allPhotoFolderList[0].photos.add(photo)
                }
                titleTxt.post {
                    titleTxt.text=allPhotoFolderList[0].name
                }
                currentPhotoList.addAll(allPhotoFolderList[0].photos)
                photoRecyclerView.post { photoAdapter.notifyDataSetChanged() }
                progressBar.post { progressBar.visibility = View.GONE }
            }
        }
        queryHandler.sendEmptyMessage(0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TakePhotoUtil.TAKE_PHOTO) {
                val photo = Photo(takePhotoImage)
                photoSelectedList.add(photo)
                val intent = Intent()
                intent.putParcelableArrayListExtra(PHOTO_KEY, photoSelectedList)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == QUESTION_AUTH) {
            var grant = true
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    showToast(applicationContext, "未授权")
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


    override fun onDestroy() {
        super.onDestroy()
        queryThread.quit()
    }
}