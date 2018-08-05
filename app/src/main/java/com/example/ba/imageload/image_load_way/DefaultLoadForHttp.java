package com.example.ba.imageload.image_load_way;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.ba.imageload.image_compress.ImageCompressInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BA on 2018/7/28 0028. 使用http下载，用
 */

public class DefaultLoadForHttp implements LoadFromAnywayInf {
    ImageCompressInterface compressInterface;
    public DefaultLoadForHttp(ImageCompressInterface compressInterface){
        this.compressInterface=compressInterface;
    }
    private static final String TAG = "DefaultLoadForHttp";
    @Override
    public Bitmap load(String uri,int reW,int reH) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap=null;

        try {
            final URL url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            bitmap= BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                Log.e(TAG, "释放资源失败- " + e);
            }
        }
        return bitmap;
    }
}
