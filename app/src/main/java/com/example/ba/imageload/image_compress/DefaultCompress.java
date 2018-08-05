package com.example.ba.imageload.image_compress;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by BA on 2018/7/26 0026.提供压缩图片的工具类，防止OOM，单例
 */
public class DefaultCompress {
    private static final String TAG = "DefaultCompress";

    /**
     * Instantiates a new Image compress.
     */
    private DefaultCompress() {
    }

    /**
     * Get instance compress.
     *
     * @return the compress
     */
    public static CompressImpl getInstance() {
        return CompressImpl.impl;
    }

    /**
     * The type Compress.
     */
    private static class CompressImpl extends ImageCompressInterface {
        private static final CompressImpl impl = new CompressImpl();

        /**
         * Compress with from resource bitmap.
         * 加载资源文件夹的图片
         *
         * @param res  the res
         * @param id   the id
         * @param reqW the req w
         * @param reqH the req h
         * @return the bitmap
         */
        public Bitmap compress(Resources res, int id, int reqW, int reqH) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            //设置只获取边界，不真正加载图片
            options.inJustDecodeBounds = true;

            //加载图片，注意：这里是不真正加载图片
            BitmapFactory.decodeResource(res, id, options);

            options.inSampleSize = calculateInSampleSize(options, reqW, reqH);

            options.inJustDecodeBounds = false;

            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeResource(res, id, options);
        }

        /**
         * Compress with from resource bitmap.
         * 加载内存的图片
         *
         * @param fd   the res
         * @param reqW the req w
         * @param reqH the req h
         * @return the bitmap
         */
        public Bitmap compress(FileDescriptor fd, int reqW, int reqH) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            //设置只获取边界，不真正加载图片
            options.inJustDecodeBounds = true;

            //加载图片，注意：这里是不真正加载图片
            BitmapFactory.decodeFileDescriptor(fd, null, options);

            options.inSampleSize = calculateInSampleSize(options, reqW, reqH);

            options.inJustDecodeBounds = false;

            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        }

        @Override
        public Bitmap compress(Bitmap bitmap, int reqW, int reqH) {
            byte[] bytes = ImageCompressInterface.bitmapToByteArray(bitmap, true);

            BitmapFactory.Options options = new BitmapFactory.Options();

            //设置只获取边界，不真正加载图片
            options.inJustDecodeBounds = true;

            //加载图片，注意：这里是不真正加载图片
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            options.inSampleSize = calculateInSampleSize(options, reqW, reqH);

            options.inJustDecodeBounds = false;

            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }

        @Override
        public Bitmap compress(String path, int reqW, int reqH) {
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPurgeable = true;
            options.inTempStorage = new byte[12 * 1024];
            options.inJustDecodeBounds = true;
            File file = new File(path);
            FileInputStream fs;

            try {
                fs = new FileInputStream(file);
                BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
                options.inSampleSize = calculateInSampleSize(options, reqW, reqH);

                options.inJustDecodeBounds = false;

                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        /**
         * Calculate in sample size int.
         * 算出采样率
         *
         * @param options the options
         * @param rW      the r w
         * @param rH      the r h
         * @return the int
         */
        private int calculateInSampleSize(BitmapFactory.Options options, int rW, int rH) {
            if (rW == 0 || rH == 0) {
                return 1;
            }
            //拿出图片宽高
            final int h = options.outHeight;
            final int w = options.outWidth;
            int inSampleSize = 1;

            //当大小超过View的大小才去压缩
            if (h > rH || w > rW) {

                final int halfH = h / 2;
                final int halfW = w / 2;

                while (halfH / inSampleSize >= rH && halfW / inSampleSize >= rW) {
                    inSampleSize *= 2;
                }
            }
            Log.d(TAG, "calculateInSampleSize: " + inSampleSize + "h=" + h + ";;;w=" + w + ";;rW=" + rW + ";;rH=" + rH);
            return inSampleSize;
        }
    }
}
