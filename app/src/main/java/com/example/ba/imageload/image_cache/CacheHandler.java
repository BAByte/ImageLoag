package com.example.ba.imageload.image_cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.example.ba.imageload.image_compress.ImageCompressInterface;
import com.jakewharton.disklrucache.DiskLruCache;


/**
 * Created by BA on 2018/7/26 0026.有内存缓存，磁盘缓存可选，
 */
public class CacheHandler {
    /**
     * The constant TAG.
     */
    private static final String TAG = "CacheHandler";
    /**
     * The constant DEFAULT_DISK_CACHE_SIZE.
     * 默认的磁盘缓存大小
     */
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50;//50MB

    /**
     * The constant MAX_MEMORY.
     * 进程可用内存大小
     */
    private static final int MAX_MEMORY = (int) Runtime.getRuntime().maxMemory() / 1024;
    /**
     * The constant DEFAULT_MEMORY_CACHE_SIZE.
     * 默认的内存缓存大小
     */
    private static final int DEFAULT_MEMORY_CACHE_SIZE = MAX_MEMORY / 10;

    /**
     * The constant FLAG_DISK_CACHE.
     * 磁盘缓存模式
     */
    public static final int FLAG_DISK_CACHE = 1;
    /**
     * The constant FLAG_MEMORY_CACHE
     * 内存缓存模式.
     */
    public static final int FLAG_MEMORY_CACHE = 2;

    public static final int FLAG_ALIVE_CACHE = 3;
    /**
     * The Mode.
     * 模式
     */
    private int mode;

    /**
     * The Memory cache.
     * 内存缓存
     */
    private CacheInterface<LruCache<String, Bitmap>> memoryCache;
    /**
     * The Disk cache.
     * 磁盘缓存
     */
    private CacheInterface<DiskLruCache> diskCache;

    /**
     * The Alive cache.内存缓存，但是没有任何算法，用来保存正在使用的图片
     */
    private CacheInterface<AliveCache> aliveCache;

    /**
     * Sets mode.
     *
     * @param mode the mode
     */
    private void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Sets memory cache.
     *
     * @param cache the cache
     */
    private void setMemoryCache(CacheInterface cache) {
        this.memoryCache = cache;
    }

    /**
     * Sets disk cache.
     *
     * @param cache the cache
     */
    private void setDiskCache(CacheInterface cache) {
        this.diskCache = cache;
    }

    private void setAliveCache(CacheInterface cache){this.aliveCache=cache;}

    public Bitmap getFromAlive(String uri, int reqW, int reqH){
        return aliveCache.get(uri,reqW,reqH);
    }

    /**
     * Get bitmap.获取图片，根据所选模式，用不同方法获取
     *
     * @param uri  the uri
     * @param reqW the req w
     * @param reqH the req h
     * @return the bitmap
     */
    public Bitmap getFromMemory(String uri, int reqW, int reqH) {
       // Log.d(TAG, "put: 内存缓存占用大小:" + memoryCache.getSize() + "磁盘缓存占用大小:" + diskCache.getSize());
        Bitmap bitmap = null;
        //从内存取
        if ((mode & FLAG_MEMORY_CACHE) != 0) {
            bitmap = memoryCache.get(uri, reqW, reqH);
        }
        return bitmap;
    }

    /**
     * Get from disk bitmap.
     *
     * @param uri  the uri
     * @param reqW the req w
     * @param reqH the req h
     * @return the bitmap
     */
    public Bitmap getFromDisk(String uri, int reqW, int reqH){
     //   Log.d(TAG, "put: 内存缓存占用大小:" + memoryCache.getSize() + "磁盘缓存占用大小:" + diskCache.getSize());
        Bitmap bitmap = null;
        //从磁盘取
        if ((mode & FLAG_DISK_CACHE) != 0) {
            bitmap = diskCache.get(uri, reqW, reqH);
          //  Log.d(TAG, "get:从磁盘获取");
        }
        return bitmap;
    }

    /**
     * Put.放入缓存，如果内存有了就不放磁盘，如果只选了磁盘模式，就直接放磁盘
     * 如果开启了二级缓存模式，当内存缓存到了一定大小，就放磁盘中
     *
     * @param uri    the uri
     * @param bitmap the bitmap
     */
    public void putToDisk(String uri, Bitmap bitmap) {
        if ((mode & FLAG_DISK_CACHE) != 0) {
            diskCache.put(uri, bitmap);
          //  Log.d(TAG, "put: 放入磁盘");
        }
    }

    /**
     * Put.放入缓存，如果内存有了就不放磁盘，如果只选了磁盘模式，就直接放磁盘
     * 如果开启了二级缓存模式，当内存缓存到了一定大小，就放磁盘中
     *
     * @param uri    the uri
     * @param bitmap the bitmap
     */
    public void putToMemory(String uri, Bitmap bitmap) {
        if ((mode & FLAG_MEMORY_CACHE) != 0) {
            memoryCache.put(uri, bitmap);
          //  Log.d(TAG, "put: 放入内存");
        }
     //   Log.d(TAG, "put: 内存缓存占用大小:" + memoryCache.getSize() + "磁盘缓存占用大小:" + diskCache.getSize());
    }

    public void putToAlive(String uri, Bitmap bitmap){
        if ((mode&FLAG_ALIVE_CACHE)!=0){
            aliveCache.put(uri,bitmap);
        }
    }

    public void removeFromMemory(String uri){
        if ((mode&FLAG_MEMORY_CACHE)!=0){
           memoryCache.remove(uri);
        }
    }

    public void removeFromAlive(String uri){
        if ((mode&FLAG_ALIVE_CACHE)!=0){
            aliveCache.remove(uri);
        }
    }

    /**
     * Release memory.
     */
    public void releaseMemory() {
        Log.d(TAG, "releaseMemory: 释放内存缓存");
        if (memoryCache != null)
            memoryCache.release();
    }

    /**
     * Release disk.
     */
    public void releaseDisk() {
        Log.d(TAG, "releaseDisk: 释放磁盘缓存");
        if (diskCache != null)
            diskCache.release();
    }

    public void releaseAlive(){
        if (aliveCache!=null){
            aliveCache.release();
        }
    }

    /**
     * Releasr all.
     */
    public void releasrAll() {
        Log.d(TAG, "releasrAll: 释放所有缓存");
        releaseDisk();
        releaseMemory();
        releaseAlive();
    }

    public void clearAlive(){
        if (aliveCache!=null)
            aliveCache.clear();
    }
    /**
     * Clear memory.
     */
    public void clearMemory() {
        Log.d(TAG, "clearMemory: 清空内存缓存");
        if (memoryCache != null)
            memoryCache.clear();
    }

    /**
     * Clear disk.
     */
    public void clearDisk() {
        Log.d(TAG, "clearDisk: 清空磁盘缓存");
        if (diskCache != null)
            diskCache.clear();
    }

    /**
     * Clear all.
     */
    public void clearAll() {
        Log.d(TAG, "clearAll: 清空所有缓存");
        clearMemory();
        clearDisk();
    }

    /**
     * The type Cache util builder.
     * 缓存建造器
     */
    public static class CacheUtilBuilder {
        /**
         * The Mode.
         */
        private int mode;
        /**
         * The Memory cache size.
         */
        private int memoryCacheSize;
        /**
         * The Disk cache size.
         */
        private int diskCacheSize;
        /**
         * The Context.
         */
        private Context context;

        private ImageCompressInterface compressInterface;

        /**
         * Instantiates a new Cache util builder.
         *
         * @param context the context
         */
        public CacheUtilBuilder(Context context) {
            this.context = context;
        }

        /**
         * Add flag cache util builder.
         *
         * @param flag the flag
         * @return the cache util builder
         */
        public CacheUtilBuilder addFlag(int flag) {
            this.mode |= flag;
            return this;
        }

        /**
         * Sets disk cache size.
         *
         * @param size the size
         * @return the disk cache size
         */
        public CacheUtilBuilder setDiskCacheSize(int size) {
            diskCacheSize = size;
            return this;
        }

        /**
         * Sets memory cache size.
         *
         * @param size the size
         * @return the memory cache size
         */
        public CacheUtilBuilder setMemoryCacheSize(int size) {
            memoryCacheSize = size;
            return this;
        }

        public CacheUtilBuilder setImageCompress(ImageCompressInterface compress) {
            compressInterface = compress;
            return this;
        }

        /**
         * Build cache handler.
         *
         * @return the cache handler
         */
        public CacheHandler build() {
            CacheHandler handler = new CacheHandler();
            if ((mode & FLAG_DISK_CACHE) != 0) {
                Log.d(TAG, "build: 开启磁盘缓存模式");
                handler.setDiskCache(initDiskCache());
            }

            if ((mode & FLAG_MEMORY_CACHE) != 0) {
                Log.d(TAG, "build: 开启内存缓存模式");
                handler.setMemoryCache(initMemoryCache());
            }

            if ((mode & FLAG_ALIVE_CACHE) != 0) {
                Log.d(TAG, "build: 开启Alive缓存模式");
                handler.setMemoryCache(initAliveCache());
            }

            if (mode == 0) {
                Log.d(TAG, "build: 开启默认三级缓存模式");
                mode = FLAG_DISK_CACHE | FLAG_MEMORY_CACHE;
                handler.setDiskCache(initDiskCache());
                handler.setAliveCache(initAliveCache());
                handler.setMemoryCache(initMemoryCache());
            }

            handler.setMode(mode);

            Log.d(TAG, "build: 缓存大小：内存="+memoryCacheSize+"；；磁盘="+diskCacheSize);
            return handler;
        }

        private DiskCache initDiskCache() {
            DiskCache diskCache = new DiskCache();
            if (diskCacheSize == 0) {
                diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
            }
            diskCache.init(context, diskCacheSize);
            diskCache.setImageCompress(compressInterface);
            return diskCache;
        }

        private MemoryCache initMemoryCache() {
            MemoryCache memoryCache = new MemoryCache();
            if (memoryCacheSize == 0) {
                memoryCacheSize = DEFAULT_MEMORY_CACHE_SIZE;
            }
            memoryCache.init(context, memoryCacheSize);
            return memoryCache;
        }

        private AliveCache initAliveCache() {
            AliveCache aliveCache = new AliveCache();
            aliveCache.init(context, 0);
            return aliveCache;
        }
    }
}
