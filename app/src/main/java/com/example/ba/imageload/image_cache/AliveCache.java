package com.example.ba.imageload.image_cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BA on 2018/8/3 0003.正在使用的缓存
 */

public class AliveCache implements CacheInterface<Map>{
    private static final String TAG = "AliveCache";
    private Map<String, WeakReference<Bitmap>> aliveCache;

    @Override
    public Bitmap get(String uri, int reqW, int reqH) {
        WeakReference<Bitmap> weakReference=aliveCache.get(uri);
        if (weakReference!=null){
            Bitmap bitmap=weakReference.get();
            Log.d(TAG, "get: "+bitmap);
            return bitmap;
        }
        return null;
    }

    @Override
    public void put(String uri, Bitmap bitmap) {
        if (get(uri,0,0)==null){
            Log.d(TAG, "put: "+bitmap);
            WeakReference<Bitmap> weakReference=new WeakReference<>(bitmap);
            aliveCache.put(uri,weakReference);
        }
    }

    @Override
    public void init(Context context, int cacheSize) {
        aliveCache=new HashMap<>();
    }

    @Override
    public Object getSize() {
        return aliveCache.size();
    }

    @Override
    public Map getImpl() {
        return aliveCache;
    }

    @Override
    public void release() {
        aliveCache.clear();
        aliveCache=null;
    }

    @Override
    public void clear() {
        aliveCache.clear();
    }

    @Override
    public void remove(String uri) {
        aliveCache.remove(uri);
    }
}
