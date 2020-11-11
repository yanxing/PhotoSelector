package com.yanxing.photolibrary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.yanxing.photolibrary.model.*

/**
 * 启动图片/视频选择器工具
 * @author 李双祥 on 2020/11/11.
 */
object PhotoSelectorEngine {

    private var showCamera = false
    private var selectMultiple = false
    private var maxNumber = 9
    private var loadMediaType = 1
    private var limitVideoDuration = 12

    /**
     * 返回一个fragment 图片/视频选择器，可以添加自己的Activity中
     */
    fun create(): Fragment {
        val fragment = PhotoSelectFragment()
        val bundle = Bundle()
        bundle.putBoolean(SHOW_CAMERA, showCamera)
        bundle.putBoolean(SELECT_MODE, selectMultiple)
        bundle.putInt(MAX_NUM, maxNumber)
        bundle.putInt(LOAD_MEDIA_TYPE, loadMediaType)
        bundle.putInt(LIMIT_VIDEO_DURATION, limitVideoDuration)
        fragment.arguments = bundle
        return fragment
    }


    /**
     * 是否显示相机拍照，多选情况下，理应不显示相机，如果此时显示相机拍照，将只返回拍照图片的路径
     */
    fun setShowCamera(showCamera: Boolean): PhotoSelectorEngine  {
        this.showCamera = showCamera
        return this
    }

    /**
     * true多选，false单选
     */
    fun setSelectMultiple(selectMultiple: Boolean): PhotoSelectorEngine {
        this.selectMultiple = selectMultiple
        return this
    }

    /**
     * 最大选择图片，默认9张，selectMultiple为true有效，一次性最大只能为9张
     */
    fun setMaxNumber(maxNumber: Int): PhotoSelectorEngine {
        this.maxNumber = maxNumber
        return this
    }

    /**
     * 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
     */
    fun setLoadMediaType(loadMediaType: Int): PhotoSelectorEngine {
        this.loadMediaType = loadMediaType
        return this
    }

    /**
     * 限制选择视频时长
     */
    fun setLimitVideoDuration(limitVideoDuration: Int): PhotoSelectorEngine {
        this.limitVideoDuration = limitVideoDuration
        return this
    }

    /**
     * 以Activity形式启动图片/视频选择器
     */
    fun start(context: Context) {
        val intent = Intent(context, PhotoSelectActivity::class.java)
        intent.putExtra(SHOW_CAMERA, showCamera)
        intent.putExtra(SELECT_MODE, selectMultiple)
        intent.putExtra(MAX_NUM, maxNumber)
        intent.putExtra(LOAD_MEDIA_TYPE, loadMediaType)
        intent.putExtra(LIMIT_VIDEO_DURATION, limitVideoDuration)
        context.startActivity(intent)
    }

}