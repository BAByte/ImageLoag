package com.example.ba.imageload.image_cache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by BA on 2018/7/28 0028.
 * 缓存公用接口
 */

public interface CacheInterface <T>{
    public Bitmap get(String uri,int reqW,int reqH);
    public void put(String uri,Bitmap bitmap);
    public  void init(Context context, int cacheSize);
    public Object getSize();
    public T getImpl();
    public void release();
    public void clear();
    public void remove(String uri);
}
