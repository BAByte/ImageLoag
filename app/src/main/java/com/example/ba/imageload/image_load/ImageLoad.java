package com.example.ba.imageload.image_load;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.ba.imageload.image_cache.CacheHandler;
import com.example.ba.imageload.image_compress.ImageCompressInterface;
import com.example.ba.imageload.image_load_way.LoadFromAnywayInf;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by BA on 2018/7/30 0030.
 */

public class ImageLoad {
    private static final String TAG = "ImageLoad";
    //获取CUP核心数
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程
    public static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    //线程池最大线程数
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    //非核心线程闲置时间
    private static final long KEEP_ALIVE = 10L;

    //这个线程工厂只是去定义一个创建线程的地方，用来在里面设置线程一些属性
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        //代表创建的线程数
        private final AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ImageLoader" + atomicInteger.getAndIncrement());
        }
    };

    //线程池，默认有核心线程，超过核心线程时会创建非核心线程，但是有限制，当超过限制就入队，非核心线程10秒闲置回收
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor
            (CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), THREAD_FACTORY);

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoadResult result = (LoadResult) msg.obj;
            String uri = result.getUri();
            cacheHandler.putToMemory(uri, result.getBitmap());
            //判断当前View的要显示的Uri和当前获取到图片的Uri对不对的上，对不上说明当前ImageView已经不需要显示这个图片
            ImageView imageView = result.getImageView();
            if (uri.equals(imageView.getTag())) {
                imageView.setImageBitmap(result.getBitmap());
                cacheHandler.putToAlive(uri,result.getBitmap());
                cacheHandler.removeFromMemory(uri);
            }

        }
    };
    private CacheHandler cacheHandler;
    private LoadFromAnywayInf loadFromHttpInf;

    public ImageLoad(CacheHandler cacheHandler, LoadFromAnywayInf loadFromHttpInf) {
        this.cacheHandler = cacheHandler;
        this.loadFromHttpInf = loadFromHttpInf;
    }

    public void load(String uri, ImageView imageView, int reqW, int reqH) {
        imageView.setTag(uri);
        Bitmap bitmap = null;
        if (cacheHandler != null) {
            bitmap = cacheHandler.getFromAlive(uri, reqW, reqH);
        }

        if (bitmap != null) {
            cacheHandler.putToMemory(uri, bitmap);
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "load: 从使用中取成功");
            cacheHandler.removeFromAlive(uri);

        } else {
            bitmap = cacheHandler.getFromMemory(uri, reqW, reqH);
            if (bitmap != null) {
                Log.d(TAG, "load: 从内存取成功");
                imageView.setImageBitmap(bitmap);
                cacheHandler.putToAlive(uri,bitmap);
                cacheHandler.removeFromMemory(uri);
            } else {
                makeATask(uri, imageView, reqW, reqH);
            }
        }
    }

    private void makeATask(final String uri, final ImageView imageView, final int reqW, final int reqH) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (cacheHandler != null) {
                    bitmap = cacheHandler.getFromDisk(uri, reqW, reqH);
                }

                if (bitmap == null) {
                    bitmap = downFromHttp(uri, reqW, reqH);
                    if (bitmap != null) {
                        //从网络下载的，需要去压缩再加载，这一步非常耗时，暂时就不要了
                        //bitmap=imageCompressInterface.compress(bitmap,reqW,reqH);
                        //Log.d(TAG, "makeATask: 从网络下载的图片压缩成功"+uri);
                        //下载到的图片加载到缓存中
                        if (uri.equals(imageView.getTag())) {
                            if (cacheHandler != null) {
                                cacheHandler.putToDisk(uri, bitmap);
                            }
                            loadCompleted(uri, imageView, bitmap);
                        }
                    }
                } else {
                    if (uri.equals(imageView.getTag())) {
                        loadCompleted(uri, imageView, bitmap);
                    }
                }
            }
        };

        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    private void loadCompleted(String uri, ImageView imageView, Bitmap bitmap) {
        LoadResult loadResult = new LoadResult(uri, imageView, bitmap);
        mHandler.obtainMessage(101, loadResult).sendToTarget();
    }

    private Bitmap downFromHttp(String uri, int reW, int reH) {
        return loadFromHttpInf.load(uri, reW, reH);
    }
}
