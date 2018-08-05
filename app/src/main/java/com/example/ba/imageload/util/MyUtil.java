package com.example.ba.imageload.util;

import android.util.Log;

/**
 * Created by BA on 2018/7/28 0028.
 * 工具类
 */
public class MyUtil {
    private static final String TAG = "MyUtil";

    /**
     * Bytes to hex string string.
     * MD5加密
     * @param bytes the bytes
     * @return the string
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
