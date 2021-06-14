package com.codepath.apps.jotwitter.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.databinding.ItemEmbeddedImageBinding;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedImageAdapter extends BaseAdapter {
    private static final String TAG = "EmbeddedImageAdapter";

    private ItemEmbeddedImageBinding binding;
    private Context context;
    private ArrayList<String> imageUrls;

    public EmbeddedImageAdapter(Context context, ArrayList<String> images){
        Log.d(TAG, "created an instance!");
        this.context = context;
        imageUrls = images;
    }
    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");
        //1.) Inflate a new view only if we need to! (convertView == null when this is a new row)
        if(convertView == null){
            binding = ItemEmbeddedImageBinding.inflate(LayoutInflater.from(context), parent, false);
        }

        //2.) Get the image, get the ImageView, load it!
        String currentImageUrl = (String) getItem(position);
        Log.d(TAG, "currentImageUrl = " + currentImageUrl);
        ImageView ivImage = binding.ivImage;
        Glide.with(context).load(currentImageUrl).into(ivImage);

        return convertView;
    }
}
