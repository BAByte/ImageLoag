package com.example.ba.imageload;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ba.imageload.image_load.ImageLoad;

import java.util.List;

/**
 * Created by BA on 2018/8/1 0001.
 */

public class RVAadapter extends RecyclerView.Adapter<RVAadapter.ViewHolder>{
    private List<String> uris;
    private ImageLoad load;


    public RVAadapter(List<String> uris, ImageLoad load) {
        this.uris = uris;
        this.load = load;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(holder.imageView.getResources().getDrawable(R.color.w));
        int w=holder.imageView.getWidth();
        int h=holder.imageView.getHeight();
        load.load(uris.get(position),holder.imageView,w,h);
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }
}
