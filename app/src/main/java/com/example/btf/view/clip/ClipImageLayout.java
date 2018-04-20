package com.example.btf.view.clip;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 */
public class ClipImageLayout extends RelativeLayout {

    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;
    private Context context;

    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //		setImagePath(image);
        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.ViewGroup.LayoutParams lp1 = new LayoutParams(
                1100,1100);
        android.view.ViewGroup.LayoutParams lp2 = new LayoutParams(
                1100,1100);
        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding +50);
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 设置图片路径
     *
     * @return
     */
    public void setImagePath(String imagePath, int id) throws FileNotFoundException {
        String display = android.os.Build.DISPLAY;
        String model = Build.MODEL;
        Log.e("path", Uri.parse(imagePath).toString());
        RequestOptions options = new RequestOptions().fitCenter().dontTransform();
        if (id == 1) {
//            mZoomImageView.setImageURI(Uri.parse(imagePath));
//            Glide.with(context).load(Uri.parse(imagePath)).override(400,400).into(mZoomImageView);
                        Glide.with(context).load(imagePath).apply(options).into(mZoomImageView);
        } else {
            File file = new File(imagePath);
            if (file != null) {
//                Glide.with(context).load(file).override(400,400).into(mZoomImageView);
                Glide.with(context).load(imagePath).apply(options).into(mZoomImageView);
            } else
                mZoomImageView.setImageURI(Uri.parse(imagePath));
//            Glide.with(context).load(Uri.parse(imagePath)).override(400,400).into(mZoomImageView);
        }
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

}
