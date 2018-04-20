package com.example.btf.viewpage;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.btf.R;
import com.example.btf.db.InfoDetail;

import java.util.ArrayList;

/**
 * Created by Dr.P on 2018/3/18.
 * runas /user:Dr.P "cmd /k"
 */

public class DFAdapter extends PagerAdapter {

    private Context context;
    private Activity activity;
    private FragmentManager fm;
    private ArrayList<String> imgs;
    private InfoDetail infoDetail;
    private int img_position;

    private static final String DIALOG_IMAGE = "image";

    public DFAdapter(Context context, FragmentManager fm, ArrayList<String> imageList, InfoDetail infoDetail) {
        this.context = context;
        this.fm = fm;
        imgs = new ArrayList<>();
        this.infoDetail = infoDetail;
        for (int i = 0; i < imageList.size(); i++) {
            imgs.add(imageList.get(i));
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.vp_layout_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
        RequestOptions options = new RequestOptions().
                placeholder(R.drawable.loading);
        if (imgs.size() > 1) {
            img_position = position;
            Glide.with(context).load("http://40.125.207.182:8080" + imgs.get(position % imgs.size())).apply(options).into(iv);
        } else if (imgs.size() == 1) {
            Glide.with(context).load("http://40.125.207.182:8080" + imgs.get(0)).apply(options).into(iv);
        } else {
            Glide.with(context).load(context.getFilesDir().getAbsolutePath() + "/btf/zanwu.jpg").into(iv);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
