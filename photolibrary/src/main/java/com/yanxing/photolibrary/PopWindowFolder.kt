package com.yanxing.photolibrary

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yanxing.photolibrary.model.PhotoFolder
import com.yanxing.photolibrary.model.formatString
import com.yanxing.photolibrary.model.getScreenMetrics
import kotlinx.android.synthetic.main.item_popwindow_folder.view.*

/**
 * 文件夹列表
 * @author 李双祥 on 2020/11/12.
 */
class PopWindowFolder {

    var popupWindow =
        PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)


    fun showFolder(context: Context, targetView: View, data: ArrayList<PhotoFolder>, listener: (Int) -> Unit) {
        var selectPosition = 0
        val view = LayoutInflater.from(context).inflate(R.layout.popwindow_folder, null)
        popupWindow.contentView = view
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        showAsDropDown(targetView)
        val folderRecyclerView = view.findViewById<RecyclerView>(R.id.folderRecyclerView)
        folderRecyclerView.layoutManager = LinearLayoutManager(context)
        folderRecyclerView.adapter =
            object : RecyclerViewAdapter<PhotoFolder>(data, R.layout.item_popwindow_folder) {
                override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                    super.onBindViewHolder(holder, position)
                    mDataList[position].apply {
                        holder.itemView.apply {
                            image.apply {
                                visibility = if (photos.size > 0) {
                                    //第一个可能是相机图标
                                    if (photos[0].path==null){
                                        if (photos.size>1){
                                            Glide.with(context).load(photos[1].path).into(this)
                                        }
                                    }else{
                                        Glide.with(context).load(photos[0].path).into(this)
                                    }
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }
                            }
                        }.state.apply {
                            visibility = if (selected) {
                                selectPosition = position
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                        }
                        holder.setText(R.id.name, formatString(name))
                        holder.setText(R.id.number, "（" + photos.size + "）")
                        holder.itemView.setOnClickListener {
                            if (!selected) {
                                data[selectPosition].selected = false
                                selectPosition = position
                                data[selectPosition].selected = true
                                listener.invoke(selectPosition)
                            }
                            popupWindow.dismiss()
                        }
                    }
                }
            }
        view.findViewById<View>(R.id.shape).setOnClickListener {
            popupWindow.dismiss()
        }
        val inAnimatorSet = AnimatorSet()
        val objectAnimator = ObjectAnimator.ofFloat(folderRecyclerView,
            "translationY",
            -getScreenMetrics(context).heightPixels.toFloat(),
            0f)
        val alphaInAnimator =ObjectAnimator.ofFloat(view.findViewById(R.id.shape), "alpha", 0f, 0.4f)
        inAnimatorSet.interpolator = LinearInterpolator()
        inAnimatorSet.duration = 200
        inAnimatorSet.play(objectAnimator).with(alphaInAnimator)
        inAnimatorSet.start()
    }

    private fun showAsDropDown(anchorView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)
            // 7.1 版本处理
            if (Build.VERSION.SDK_INT >= 25) {
                val screenHeight = getScreenMetrics(popupWindow.contentView.context).heightPixels
                popupWindow.height = screenHeight - location[1] - anchorView.height
            }
            popupWindow.showAtLocation(anchorView,
                Gravity.NO_GRAVITY,
                0,
                location[1] + anchorView.height)
        } else {
            popupWindow.showAsDropDown(anchorView)
        }
    }
}