package com.yanxing.photolibrary

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.yanxing.photolibrary.model.*

/**
 * 启动图片/视频选择器工具
 * @author 李双祥 on 2020/11/11.
 */
object PhotoSelectorEngine {

    private lateinit var intent: Intent
    private var activity: Activity? = null
    private var fragment: Fragment? = null

    fun create(activity: Activity): PhotoSelectorEngine {
        intent = Intent(activity, PhotoSelectActivity::class.java)
        this.activity = activity
        return this
    }

    fun create(fragment: Fragment): PhotoSelectorEngine {
        intent = Intent(fragment.activity, PhotoSelectActivity::class.java)
        this.fragment = fragment
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
    fun start() {
        activity?.startActivityForResult(intent, REQUEST_PHOTO_CODE)
        fragment?.startActivityForResult(intent, REQUEST_PHOTO_CODE)
    }

    /**
     * 在onActivityResult中接收数据，返回选择的图片/视频
     * @param requestCode onActivityResult方法中的requestCode
     */
    fun getResult(requestCode: Int, data: Intent?): ArrayList<Photo>? {
        if (requestCode != REQUEST_PHOTO_CODE) {
            return null
        }
        return data?.getParcelableArrayListExtra(PHOTO_KEY)
    }

}