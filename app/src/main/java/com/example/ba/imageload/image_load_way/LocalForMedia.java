package com.example.ba.imageload.image_load_way;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.ba.imageload.image_compress.DefaultCompress;
import com.example.ba.imageload.image_compress.ImageCompressInterface;

/**
 * Created by BA on 2018/8/1 0001.
 */

public class LocalForMedia implements LoadFromAnywayInf {
    ImageCompressInterface imageCompressInterface;

    public LocalForMedia(ImageCompressInterface imageCompressInterface) {
        this.imageCompressInterface = imageCompressInterface;
    }

    private static final String TAG = "LocalForMedia";
    @Override
    public Bitmap load(String uri,int reW,int reH) {
        Log.d(TAG, "load: 媒体拿");
        return imageCompressInterface.compress(uri,reW,reH);
    }
}
