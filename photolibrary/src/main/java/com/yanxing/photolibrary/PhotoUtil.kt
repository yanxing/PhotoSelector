package com.yanxing.photolibrary

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.ArrayMap
import androidx.annotation.RequiresApi
import java.io.File

/**
 * 查询图片/视频
 * @author 李双祥 on 2020/11/10.
 */


/**
 * 兼容方式获取图片/视频
 * @type 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
 */
fun getPhotos(context: Context, type: Int = 1){
    return if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
        getAndroidQPhotos(context, type)
    }else{
        getOldPhotos(context, type)
    }
}

/**
 * AndroidQ方式获取图片/视频
 */
@RequiresApi(Build.VERSION_CODES.Q)
private fun getAndroidQPhotos(context: Context, type: Int){
     val allPhotoKey= when (type) {
         0 -> {
             "图片和视频"
         }
         1 -> {
             "图片"
         }
         else -> {"视频"}
     }
    //总的图片和视频
    val photoFolderMap=ArrayMap<String, PhotoFolder>()
    val photoFolder=PhotoFolder(allPhotoKey, ArrayList(), true)
    photoFolderMap[allPhotoKey] = photoFolder

    //先查询图片
    if (type==0||type==1) {
        val imageUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver=context.contentResolver
        try {
            val cursor = contentResolver.query(
                imageUri,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.RELATIVE_PATH,
                    MediaStore.Images.Media.DISPLAY_NAME
                ),
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png"),
                MediaStore.Images.Media.DATE_MODIFIED + " desc"
            )
            while (cursor?.moveToNext()!!){
                //兼容AndroidQ不再使用MediaStore的"_data"字段
                val id=cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                //图片路径
                val photoUri=Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()+File.separator+id)
            }


        }catch (e:Exception){}
    }
    if (type==0||type==2){
        //查询视频

    }





}

private fun getOldPhotos(context: Context, type: Int){

}