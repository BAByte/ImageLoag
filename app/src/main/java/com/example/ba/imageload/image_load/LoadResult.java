package com.example.ba.imageload.image_load;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by BA on 2018/8/1 0001.
 */

public class LoadResult {
    private String uri;
    private ImageView imageView;
    private Bitmap bitmap;

    public LoadResult(String uri,ImageView imageView, Bitmap bitmap) {
        this.imageView = imageView;
        this.bitmap = bitmap;
        this.uri=uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
