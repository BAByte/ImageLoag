package com.example.ba.imageload.image_compress;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;

/**
 * Created by BA on 2018/8/1 0001.
 */

public abstract class ImageCompressInterface {
    private static final String TAG = "ImageCompressInterface";
    public abstract Bitmap compress(Resources res, int id, int reqW, int reqH);
    public abstract Bitmap compress(FileDescriptor fd, int reqW, int reqH);
    public abstract Bitmap compress(Bitmap bitmap,int reqW, int reqH);
    public abstract Bitmap compress(String path, int reqW, int reqH);

    /**
     * bitmap转换成byte数组
     *
     * @param bitmap
     * @param needRecycle
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap, boolean needRecycle) {
        if (null == bitmap) {
            return null;
        }
        if (bitmap.isRecycled()) {
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, output);
        if (needRecycle) {
            bitmap.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }
}
