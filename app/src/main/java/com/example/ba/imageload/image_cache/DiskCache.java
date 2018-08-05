package com.example.ba.imageload.image_cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.ba.imageload.image_compress.ImageCompressInterface;
import com.example.ba.imageload.util.MyUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BA on 2018/7/28 0028.
 * 磁盘缓存器具体实现
 */
public class DiskCache implements CacheInterface {
    /**
     * The constant TAG.
     */
    private static final String TAG = "DiskCache";
    /**
     * The Disk cache size.
     */
    private int diskCacheSize;
    /**
     * The Cache.
     */
    private DiskLruCache cache;
    private ImageCompressInterface imageCompress;
    /**
     * The constant DISK_CACHE_INDEX.
     */
    public static final int DISK_CACHE_INDEX = 0;

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
        Bitmap bitmap;
        try {
            Log.d(TAG, "get: 从磁盘获取");
            String key = hashKeyForDisk(uri);
            DiskLruCache.Snapshot snapshot = cache.get(key);
            if (snapshot != null) {
                FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                bitmap = imageCompress.compress(fileDescriptor, reqW, reqH);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Put.
     *
     * @param uri    the uri
     * @param bitmap the bitmap
     */
    @Override
    public void put(String uri, Bitmap bitmap) {
        if (get(uri,0,0)==null) {
            Log.d(TAG, "put: 放入磁盘成功");
            try {
                String key=hashKeyForDisk(uri);
                DiskLruCache.Editor editor = cache.edit(key);
                if (editor!=null) {
                    OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Init.
     *
     * @param context   the context
     * @param cacheSize the cache size
     */
    @Override
    public void init(Context context, int cacheSize) {
        diskCacheSize = cacheSize;
        cache = getMemoryCache(context);
    }

    public void setImageCompress(ImageCompressInterface compress){
        imageCompress=compress;
    }

    /**
     * Gets memory cache.
     *
     * @param context the context
     * @return the memory cache
     */
    private DiskLruCache getMemoryCache(Context context) {
        File diskCacheDir = context.getCacheDir();
        DiskLruCache diskLruCache = null;
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }

        try {
            //打开磁盘缓存
            diskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diskLruCache;
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     *
     * @param key the key
     * @return the string
     */
    private static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = MyUtil.bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * Get disk lru cache.
     *
     * @return the disk lru cache
     */
    @Override
    public DiskLruCache getImpl(){
        return cache;
    }

    @Override
    public void release() {
        try {
            cache.close();
            cache=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(String uri) {

    }

    @Override
    public Object getSize() {
        return cache.size();
    }
}
