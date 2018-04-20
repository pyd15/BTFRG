package com.example.btf.recycleView.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.btf.R;
import com.example.btf.recycleView.indexBar.ButterflyInfo_copy;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dr.P on 2017/11/10.
 */
public class SearchButterflyInfoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.butterfly_image)
    ImageView butterflyImage;

    @BindView(R.id.butterfly_name)
    TextView butterflyName;

    @BindView(R.id.butterfly_latinName)
    TextView butterflyLatinName;

    View butterflyView;
    Context context;

    public SearchButterflyInfoHolder(Context context, View itemView) {
        super(itemView);
        butterflyView = itemView;
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void bind(ButterflyInfo_copy butterflyInfo) throws IOException {

        String imagePath = butterflyInfo.getImagePath();
        butterflyName.setText(butterflyInfo.getName());

        String[] images = imagePath.split(",");
        String[] img_urls = butterflyInfo.getImageUrl().split(",");
        RequestOptions options = new RequestOptions().
                placeholder(R.drawable.loading);
        if (images.length > 1)
//            Glide.with(butterflyView.getContext()).load(images[1]).into(butterflyImage);
            Glide.with(butterflyView.getContext()).load("http://40.125.207.182:8080"+img_urls[0]).apply(options).into(butterflyImage);
        else
            Glide.with(butterflyView.getContext()).load(butterflyView.getContext().getFilesDir().getAbsolutePath() + "/btf/zanwu.jpg").into(butterflyImage);
        butterflyLatinName.setText(butterflyInfo.getLatinName());
    }
}
