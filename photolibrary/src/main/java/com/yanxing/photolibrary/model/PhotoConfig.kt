package com.yanxing.photolibrary.model

import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast

/**
 * @author 李双祥 on 2020/11/9.
 */

/**
 * 是否显示相机,默认不显示false，暂只能拍照片
 */
const val SHOW_CAMERA="show_camera"
/**
 * 是否多选，默认false
 */
const val SELECT_MODE="select_mode"
/**
 * 多选最大个数
 */
const val MAX_NUM="max_num"
/**
 * 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
 */
const val LOAD_MEDIA_TYPE="media_type"
/**
 * 是否限制选择视频时长，单位秒，默认限制,视频时有效
 */
const val LIMIT_VIDEO_DURATION="video_duration"

const val PHOTO_KEY="photo_data"
const val REQUEST_PHOTO_CODE=1000


/**
 * 格式化时间，单位为秒
 */
fun formatDuration(duration: Int):String{
    if (duration<10){
        return "0:0$duration"
    }
    if (duration<60){
        return "0:$duration"
    }
    val m=duration/60
    if (m<60){
        return if (m<10){
            "0"+m.toString()+":"+duration%60
        }else{
            m.toString()+":"+duration%60
        }
    }
    val h=duration/3600
    if (h<24){
        return h.toString()+":"+m+":"+m%60
    }
    return duration.toString()
}

fun showToast(context: Context, msg: String){
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

/**
 * 获取屏幕参数
 */
fun getScreenMetrics(context: Context): DisplayMetrics{
    return context.resources.displayMetrics
}

/**
 * 转换dp为px
 */
fun dp2px(context: Context, dp: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

