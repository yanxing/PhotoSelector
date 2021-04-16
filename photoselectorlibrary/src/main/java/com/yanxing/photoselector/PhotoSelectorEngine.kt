package com.yanxing.photoselector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.yanxing.photoselector.model.*
import com.yanxing.photoselector.util.TakePhotoUtil

/**
 * 启动图片/视频选择器工具
 * @author 李双祥 on 2020/11/11.
 */
object PhotoSelectorEngine {

    private lateinit var intent: Intent
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var uri: Uri? = null
    /**
     * 请求启动图片/视频选择器
     */
    const val REQUEST_PHOTO_CODE=1000

    /**
     * 拍照请求码
     */
    const val TAKE_PHOTO = 1001

    fun create(activity: Activity): PhotoSelectorEngine {
        intent = Intent(activity, PhotoSelectActivity::class.java)
        this.activity = activity
        this.fragment=null
        return this
    }

    fun create(fragment: Fragment): PhotoSelectorEngine {
        intent = Intent(fragment.activity, PhotoSelectActivity::class.java)
        this.fragment = fragment
        this.activity=null
        return this
    }

    /**
     * 是否显示相机拍照，多选情况下，理应不显示相机，如果此时显示相机拍照，将只返回拍照图片的路径
     */
    fun setShowCamera(showCamera: Boolean): PhotoSelectorEngine {
        intent.putExtra(SHOW_CAMERA, showCamera)
        return this
    }

    /**
     * true多选，false单选
     */
    fun setSelectMultiple(selectMultiple: Boolean): PhotoSelectorEngine {
        intent.putExtra(SELECT_MODE, selectMultiple)
        return this
    }

    /**
     * 最大选择图片，默认9张，selectMultiple为true有效，一次性最大只能为9张
     */
    fun setMaxNumber(maxNumber: Int): PhotoSelectorEngine {
        intent.putExtra(MAX_NUM, maxNumber)
        return this
    }

    /**
     * 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
     */
    fun setLoadMediaType(loadMediaType: Int): PhotoSelectorEngine {
        intent.putExtra(LOAD_MEDIA_TYPE, loadMediaType)
        return this
    }

    /**
     * 限制选择视频时长
     */
    fun setLimitVideoDuration(limitVideoDuration: Int): PhotoSelectorEngine {
        intent.putExtra(LIMIT_VIDEO_DURATION, limitVideoDuration)
        return this
    }

    /**
     * 启动图片/视频选择器
     */
    fun start(requestCode: Int) {
        activity?.startActivityForResult(intent, requestCode)
        fragment?.startActivityForResult(intent, requestCode)
    }

    /**
     * 启动选择器后，在onActivityResult中接收选择的图片/视频
     */
    fun getResult(data: Intent?): ArrayList<Photo>? {
        onDestroy()
        return data?.getParcelableArrayListExtra(PHOTO_KEY)
    }

    /**
     * 相机拍照
     */
    fun takePhoto(requestCode: Int) {
        //必须有一个不为空
        if (activity==null) {
            uri = TakePhotoUtil.takePhoto(fragment?.activity,requestCode)
        }else{
            uri = TakePhotoUtil.takePhoto(activity,requestCode)
        }
    }

    /**
     * 启动相机拍照后，在onActivityResult中接收拍的照片
     */
    fun getResult(): Photo? {
        onDestroy()
        return Photo(uri)
    }

    private fun onDestroy(){
        this.fragment=null
        this.activity=null
    }

}