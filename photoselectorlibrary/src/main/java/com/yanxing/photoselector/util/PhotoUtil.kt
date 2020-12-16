package com.yanxing.photoselector.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.ArrayMap
import androidx.annotation.RequiresApi
import com.yanxing.photoselector.model.Photo
import com.yanxing.photoselector.model.PhotoFolder
import java.io.File

/**
 * 查询图片/视频
 * @author 李双祥 on 2020/11/10.
 */


/**
 * 兼容方式获取图片/视频
 * @param type 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
 */
fun getPhotos(context: Context, type: Int = 1): ArrayList<PhotoFolder> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getNewPhotos(context, type,null)
    } else {
        getOldPhotos(context, type,null)
    }
}

/**
 * 兼容方式获取图片/视频
 * @param type 加载媒体类型（只支持图片视频），0全部，1图片，2视频，默认1只加载图片
 * @param videoDuration 对视频时长过滤，秒
 */
fun getPhotos(context: Context, type: Int = 1,videoDuration:Int?): ArrayList<PhotoFolder> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getNewPhotos(context, type,videoDuration)
    } else {
        getOldPhotos(context, type,videoDuration)
    }
}

/**
 * AndroidQ方式获取图片/视频
 */
@RequiresApi(Build.VERSION_CODES.Q)
private fun getNewPhotos(context: Context, type: Int,videoDuration:Int?): ArrayList<PhotoFolder> {
    val allPhotoKey = when (type) {
        0 -> {
            "图片和视频"
        }
        1 -> {
            "所有图片"
        }
        else -> {
            "所有视频"
        }
    }
    //总的图片和视频
    val photoFolderMap = ArrayMap<String, PhotoFolder>()
    val photoFolder = PhotoFolder(allPhotoKey, ArrayList(), true)
    photoFolderMap[allPhotoKey] = photoFolder

    //先查询图片
    if (type == 0 || type == 1) {
        val photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = context.contentResolver
        try {
            val cursor = contentResolver.query(
                photoUri,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.RELATIVE_PATH,
                    MediaStore.Images.Media.DISPLAY_NAME
                ),
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png"),//只加载这几种图片格式
                MediaStore.Images.Media.DATE_MODIFIED + " desc"
            )
            while (cursor?.moveToNext()!!) {
                //兼容AndroidQ不再使用MediaStore的"_data"字段
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                val updateTime =cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                //图片路径
                val imageUri =Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id)
                val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH))
                //这个文件夹下有图片了
                if (photoFolderMap.containsKey(imagePath)) {
                    val photo = Photo(imageUri, 1, 0,updateTime)
                    photoFolderMap[imagePath]?.photos?.add(photo)
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                    continue
                } else {
                    //还没有这个文件夹
                    val photoFolder = PhotoFolder("", ArrayList(), false)
                    val photo = Photo(imageUri, 1, 0,updateTime)
                    photoFolder.photos.add(photo)
                    photoFolder.name = imagePath
                    photoFolderMap[imagePath] = photoFolder
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                }
            }
            cursor.close()
        } catch (e: Exception) {
        }
    }
    if (type == 0 || type == 2) {
        //查询视频
        val photoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val contentResolver = context.contentResolver
        try {
            val cursor = contentResolver.query(
                photoUri, arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATE_MODIFIED,
                    MediaStore.Video.Media.RELATIVE_PATH,
                    MediaStore.Video.Media.DISPLAY_NAME
                ), MediaStore.Video.Media.MIME_TYPE + "=?",
                arrayOf("video/mp4")//这里只加载mp4格式视频
                , MediaStore.Video.Media.DATE_MODIFIED + " desc"
            )
            while (cursor?.moveToNext()!!) {
                //兼容AndroidQ不再使用MediaStore的"_data"字段
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val updateTime =cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))
                //视频路径
                val videoUri =Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id)
                val videoPath =cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH))
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, videoUri)
                val duration =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //时长(毫秒)
                val durationInt=(duration.toLong()/1000).toInt()
                //时长大于限制
                if (videoDuration!=null&&durationInt>videoDuration){
                    continue
                }
                //这个文件夹下有视频了
                if (photoFolderMap.containsKey(videoPath)) {
                    val photo = Photo(videoUri, 2, durationInt, updateTime)
                    photoFolderMap[videoPath]?.photos?.add(photo)
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                    continue
                } else {
                    //还没有这个文件夹
                    val photoFolder = PhotoFolder("", ArrayList(), false)
                    val photo = Photo(videoUri, 2, durationInt, updateTime)
                    photoFolder.photos.add(photo)
                    photoFolder.name = videoPath
                    photoFolderMap[videoPath] = photoFolder
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                }
            }
            cursor.close()
        } catch (e: Exception) {
        }
    }
    val photoFolders=ArrayList<PhotoFolder>()
    var allPhotoFolder:PhotoFolder?=null
    photoFolderMap.forEach { (_, u) ->
        if (u.selected){
            allPhotoFolder=u
        }else{
            photoFolders.add(u)
        }
    }
    //把总的照片或者视频文件夹移到最前面
    allPhotoFolder?.let { photoFolders.add(0,it) }
    return photoFolders
}

/**
 * AndroidQ以前版本获取图片/视频
 */
private fun getOldPhotos(context: Context, type: Int,videoDuration:Int?): ArrayList<PhotoFolder> {
    val allPhotoKey = when (type) {
        0 -> {
            "图片和视频"
        }
        1 -> {
            "所有图片"
        }
        else -> {
            "所有视频"
        }
    }
    //总的图片和视频
    val photoFolderMap = ArrayMap<String, PhotoFolder>()
    val photoFolder = PhotoFolder(allPhotoKey, ArrayList(), true)
    photoFolderMap[allPhotoKey] = photoFolder

    //先查询图片
    if (type == 0 || type == 1) {
        val photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = context.contentResolver
        try {
            val cursor = contentResolver.query(
                photoUri,
                arrayOf(
                    MediaStore.Images.Media.DATE_MODIFIED,
                    MediaStore.Images.Media.DATA,
                ),
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png"),
                MediaStore.Images.Media.DATE_MODIFIED + " desc"
            )
            while (cursor?.moveToNext()!!) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val updateTime =cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                val pathFile = File(path)
                if (!pathFile.exists()) {
                    //去掉冗余数据，用于校验这个路径下有没有图片
                    continue
                }
                //图片路径
                val imageUri = FileUriUtil.getFileToUri(context, pathFile, 1)
                val imagePath =if (pathFile.parentFile == null) "" else pathFile.parentFile?.absolutePath
                //这个文件夹下有图片了
                if (photoFolderMap.containsKey(imagePath)) {
                    val photo = Photo(imageUri, 1, 0,updateTime)
                    photoFolderMap[imagePath]?.photos?.add(photo)
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                    continue
                } else {
                    //还没有这个文件夹
                    val photoFolder = PhotoFolder("", ArrayList(), false)
                    val photo = Photo(imageUri, 1, 0,updateTime)
                    photoFolder.photos.add(photo)
                    photoFolder.name = imagePath!!
                    photoFolderMap[imagePath] = photoFolder
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                }
            }
            cursor.close()
        } catch (e: Exception) {
        }
    }
    if (type == 0 || type == 2) {
        //查询视频
        val photoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val contentResolver = context.contentResolver
        try {
            val cursor = contentResolver.query(
                photoUri, arrayOf(
                    MediaStore.Video.Media.DATE_MODIFIED,
                    MediaStore.Video.Media.DISPLAY_NAME
                ), MediaStore.Video.Media.MIME_TYPE + "=?",
                arrayOf("video/mp4"), MediaStore.Video.Media.DATE_MODIFIED + " desc"
            )
            while (cursor?.moveToNext()!!) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val updateTime =cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))
                val pathFile = File(path)
                if (!pathFile.exists()) {
                    //去掉冗余数据，用于校验这个路径下有没有视频
                    continue
                }
                //视频路径
                val videoUri = FileUriUtil.getFileToUri(context, pathFile, 2)
                val videoPath =if (pathFile.parentFile == null) "" else pathFile.parentFile?.absolutePath
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, videoUri)
                val duration =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //时长(毫秒)
                val durationInt=(duration.toLong()/1000).toInt()
                //时长大于限制
                if (videoDuration!=null&&durationInt>videoDuration){
                    continue
                }
                //这个文件夹下有视频了
                if (photoFolderMap.containsKey(videoPath)) {
                    val photo = Photo(videoUri, 2, durationInt,updateTime)
                    photoFolderMap[videoPath]?.photos?.add(photo)
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                    continue
                } else {
                    //还没有这个文件夹
                    val photoFolder = PhotoFolder("", ArrayList(), false)
                    val photo = Photo(videoUri, 2, durationInt,updateTime)
                    photoFolder.photos.add(photo)
                    photoFolder.name = videoPath!!
                    photoFolderMap[videoPath] = photoFolder
                    photoFolderMap[allPhotoKey]?.photos?.add(photo)
                }
            }
            cursor.close()
        } catch (e: Exception) {
        }
    }
    val photoFolders=ArrayList<PhotoFolder>()
    var allPhotoFolder:PhotoFolder?=null
    photoFolderMap.forEach { (_, u) ->
        if (u.selected){
            allPhotoFolder=u
        }else{
            photoFolders.add(u)
        }
    }
    //把总的照片或者视频文件夹移到最前面
    allPhotoFolder?.let { photoFolders.add(0,it) }
    return photoFolders
}