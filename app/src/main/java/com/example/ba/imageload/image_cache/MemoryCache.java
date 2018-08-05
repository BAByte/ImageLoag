package com.example.ba.imageload.image_cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by BA on 2018/7/28 0028.
 * 内存缓存器具体实现
 */
public class MemoryCache implements CacheInterface {
    private static final String TAG = "MemoryCache";
    /**
     * The Cache.
     * 内存缓存
     */
    private LruCache<String, Bitmap> cache;
    /**
     * The Cache size.
     */
    private int cacheSize;

    /**
     * Get bitmap.
     *
     * @param uri  the uri
     * @param reqW the req w
     * @param reqH the req h
     * @return the bitmap
     */
    @Override
    public Bitmap get(String uri, int reqW, int reqH) {
        return cache.get(uri);
    }

    /**
     * Put.
     *
     * @param uri    the uri
     * @param bitmap the bitmap
     */
    @Override
    public void put(String uri,Bitmap bitmap) {
        if (get(uri,0,0)==null){
            Log.d(TAG, "put: 放入内存成功");
            cache.put(uri,bitmap);
        }
    }

    /**
     * Init.
     *
     * @param context   the context
     * @param cacheSize the cache size
     */
    @Override
    public void init(Context context,int cacheSize) {
        this.cacheSize=cacheSize;
        cache=getCache();
    }

    /**
     * Gets cache.
     *
     * @return the cache
     */
    private LruCache<String, Bitmap> getCache() {
        return new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //最后要除1024换算成kb
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * Get lru cache.
     *
     * @return the lru cache
     */
    @Override
    public LruCache<String,Bitmap> getImpl(){
        return cache;
    }

    @Override
    public void release() {
       cache=null;
    }

    @Override
    public void clear() {
        cache.evictAll();
    }

    @Override
    public Object getSize() {
        return cache.size();
    }

    @Override
    public void remove(String uri){
        cache.remove(uri);
    }
}
