package com.example.btf.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.btf.R;
import com.example.btf.db.InfoDetail;
import com.example.btf.viewpage.GuardViewPager;
import com.example.btf.viewpage.DFAdapter;
import com.example.btf.viewpage.ViewPagerIndicator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_PATH = "PHOTO_FROM_INFO";

    private ImageView imageView;
    private TextView nameText;
    private TextView latinNameText;
    private GuardViewPager vp;
    private DFAdapter dfAdapter;
    private LinearLayout ll;

    private static ArrayList<String> images;

    private static Context context;
    private static InfoDetail infoDetail;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(Context co, String imagePath, ArrayList<String> imgs, InfoDetail info) {
        infoDetail = info;
        context = co;
        images = imgs;
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setArguments(bundle);
        imageFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return imageFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //        imageView = new ImageView(getActivity());

        View view = inflater.inflate(R.layout.fragment_image, container, false);

        vp = (GuardViewPager) view.findViewById(R.id.vp_frag);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        dfAdapter = new DFAdapter(context, fm, images, infoDetail);
        vp.setAdapter(dfAdapter);
        ll = (LinearLayout) view.findViewById(R.id.ll_frag);
        if (images.size() > 1) {
            vp.setOnPageChangeListener(new ViewPagerIndicator(context, vp, ll, images.size()));
        } else {
            vp.toggleSlide(false);//若该类蝴蝶只有一张图片则不进行切换显示
        }

        //        imageView = (ImageView) view.findViewById(R.id.image_frag);
        nameText = (TextView) view.findViewById(R.id.name_frag);
        latinNameText = (TextView) view.findViewById(R.id.latinName_frag);
        String imagePath = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        nameText.setText(infoDetail.getName());
        latinNameText.setText(infoDetail.getType());
        //        Glide.with(getActivity()).load(imagePath).into(imageView);//.override(1500,800)
        //        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        //        imageView.setImageBitmap(bitmap);

        return view;
    }


    //    @Override
    //    public void onStart()
    //    {
    //        super.onStart();
    //        DisplayMetrics dm = new DisplayMetrics();
    //        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
    //        getDialog().getWindow().setLayout( dm.widthPixels, getDialog().getWindow().getAttributes().height );
    //    }
}
