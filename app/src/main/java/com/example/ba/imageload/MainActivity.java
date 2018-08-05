package com.example.ba.imageload;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.ba.imageload.image_cache.CacheHandler;
import com.example.ba.imageload.image_compress.DefaultCompress;
import com.example.ba.imageload.image_compress.ImageCompressInterface;
import com.example.ba.imageload.image_load_way.DefaultLoadForHttp;
import com.example.ba.imageload.image_load_way.LocalForMedia;
import com.example.ba.imageload.image_load.ImageLoad;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    List<String> uris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uris = new ArrayList<>();

        //检查权限
        boolean havePermission = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (havePermission)
            get();

//        for (int i=0;i<100;i++) {
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406665&di=b5eded575637e4e20d711f2e2038721e&imgtype=0&src=http%3A%2F%2Fa3.topitme.com%2F2%2F55%2F15%2F11764490137d115552o.jpg");
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406665&di=638c68bea5309dba13a48bf8755a1714&imgtype=0&src=http%3A%2F%2Fww1.sinaimg.cn%2Flarge%2Fd75e3906jw1ec1pqijblaj20sg0lcmzu.jpg");
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406667&di=94a888b7651b698422428aa440ce3714&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F09fa513d269759ee42bee54fb6fb43166c22df4c.jpg");
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406667&di=a7680ad6182635fd1c6226e7a875c7ab&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201312%2F05%2F20131205172253_fHTEn.jpeg");
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406669&di=c9113d3f555c3d49806f41ac5d127772&imgtype=0&src=http%3A%2F%2Fimg0.ph.126.net%2FA3O9DAYRf766UnvF1i9ngg%3D%3D%2F6630479526979054158.jpg");
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406669&di=f793bdc852752c32e7777dcbe0d4665c&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201312%2F05%2F20131205172039_wyWvZ.jpeg");
//            uris.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533190406668&di=afad9275de8f17fce8889d4febc01a98&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201312%2F05%2F20131205171903_ZXHSx.thumb.700_0.jpeg");
//        }
        Log.d(TAG, "onCreate: "+uris.size());
        ImageCompressInterface compressInterface = DefaultCompress.getInstance();
        CacheHandler handler = new CacheHandler.CacheUtilBuilder(this).setImageCompress(compressInterface).build();
        ImageLoad imageLoad = new ImageLoad(handler, new LocalForMedia(compressInterface));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setAdapter(new RVAadapter(uris, imageLoad));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void get() {
        //只查询固定格式的图片，比如png和jpge
        Cursor mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
        if (mCursor.moveToFirst()) {
            do {
                uris.add(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
            } while (mCursor.moveToNext());
            mCursor.close();
        }
    }

    /**
     * 检查是否有权限
     *
     * @param permissions 需要申请的权限
     * @return true：有权限，false：无权限
     * @throws
     * @author BA on 2017/12/7 0007.
     */
    private boolean checkPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> mPermissions = new ArrayList<>();
            for (String p : permissions) {
                int result = ContextCompat.checkSelfPermission(this, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    mPermissions.add(p);
                }
            }

            if (mPermissions.size() != 0) {
                ActivityCompat.requestPermissions(this
                        , mPermissions.toArray(new String[mPermissions.size()])
                        , 101);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length != 0) {
            for (int g : grantResults) {
                if (g != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "没有权限，程序无法正常运行", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        } else {
            Toast.makeText(this, "没有权限，程序无法正常运行", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
