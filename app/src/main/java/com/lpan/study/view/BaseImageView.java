package com.lpan.study.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lpan.R;


/**
 * Created by lpan on 2017/11/23.
 */

public class BaseImageView extends ImageView {


    public BaseImageView(Context context) {
        super(context);
    }

    public BaseImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setUrl(Fragment fragment, String url) {
        Glide.with(fragment)
                .load(url)
                .placeholder(R.drawable.image_loading)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(this);
    }

    public void setUrl(Activity activity, String url) {
        Glide.with(activity)
                .load(url)
                .placeholder(R.drawable.image_loading)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(this);
    }

    public void setUrl(Context context, String url) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.image_loading)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(this);
    }
}
