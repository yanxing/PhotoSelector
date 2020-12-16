package com.yanxing.photoselector.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by 李双祥 on 2017/7/13.
 */
public class TakePhotoUtil {

    /**
     * 和清单文件中provider一致
     */
    private static String AUTHORITY = "";
    public static final int TAKE_PHOTO = 100;

    /**
     * 拍照
     */
    public static Uri takePhoto(Activity context,int requestCode) {
        AUTHORITY = context.getPackageName() + ".provider";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //这里使用AndroidQ沙盒
        File path = context.getExternalFilesDir("");
        File file = new File(path, System.currentTimeMillis() + ".png");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, AUTHORITY, file));
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        context.startActivityForResult(intent, requestCode);
        return FileUriUtil.getFileToUri(context,file,1);
    }
}
