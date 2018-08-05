package com.example.ba.imageload.image_load_way;

import android.graphics.Bitmap;

/**
 * Created by BA on 2018/7/28 0028.图片加载时可能是从网上下载
 */

public interface LoadFromAnywayInf {
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    public Bitmap load(String uri,int reW,int reH);
}
