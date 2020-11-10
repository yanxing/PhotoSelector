package com.yanxing.photolibrary

/**
 * @author 李双祥 on 2020/11/9.
 */
data class PhotoFolder(
    //文件夹名称
    var name:String,
    //该文件夹下图片/视频列表
    var photos:List<Photo>,
    //是否选中该文件夹
    var isSelected:Boolean=false)