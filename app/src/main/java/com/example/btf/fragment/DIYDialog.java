package com.example.btf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.btf.MainActivity;
import com.example.btf.R;
import com.example.btf.db.InfoDetail;
import com.example.btf.recycleView.InfoActivity;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dr.P on 2017/12/23.
 * runas /user:Dr.P "cmd /k"
 */

public class DIYDialog extends BaseDialog {

    //界面显示的数据

    private String clipImagePath;
    private Context context;
    private int id = 1;

    private OnClickListener mListener;

    public DIYDialog(Context context, String clipImagePath, int id) {
        super(context);
        this.clipImagePath = clipImagePath;
        this.id = id;
        mCreateView = initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置Dialog没有标题。需在setContentView之前设置，在之后设置会报错
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置Dialog背景透明效果，必须设置一个背景，否则会有系统的Dialog样式：外部白框
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(mCreateView);//添加视图布局

        MyOnClickListener listener = new MyOnClickListener(0);

        LitePal.initialize(mContext);
        ImageView imageView = (ImageView) findViewById(R.id.picture);
        TextView name = (TextView) findViewById(R.id.Name);
        TextView latinName = (TextView) findViewById(R.id.latinname);
        TextView kind = (TextView) findViewById(R.id.Kind);
        TextView detail = (TextView) findViewById(R.id.detail);
        Button checkcompare = (Button) findViewById(R.id.checkcompare);
        TextView home = (TextView) findViewById(R.id.homepage);

        List<InfoDetail> infoDetail = DataSupport.where("b_id=?",String.valueOf(id)).find(InfoDetail.class);
        RequestOptions options = new RequestOptions().fitCenter().
                placeholder(R.drawable.loading);
        Glide.with(mContext).load("http://40.125.207.182:8080"+getImagePaths(infoDetail.get(0)).get(0)).apply(options).into(imageView);

        name.setText(infoDetail.get(0).getName());
        latinName.setText(infoDetail.get(0).getLatinName());
        kind.setText(infoDetail.get(0).getType());
        detail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        home.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        detail.setText("查看详情");
        home.setText("返回首页");
        checkcompare.setOnClickListener(listener);
        detail.setOnClickListener(listener);//设置点击监听器
        home.setOnClickListener(listener);
        setLayout();
    }

    private View initView() {
        View view = getLayoutInflater().inflate(R.layout.diy_dialog, null);
        return view;
    }

    private ArrayList<String> getImagePaths(InfoDetail infoDetail) {
        String[] images = infoDetail.getImagePath().split(",");
        String[] img_urls = infoDetail.getImageUrl().split(",");
        ArrayList<String> imageList = new ArrayList<String>();
//        for (int i = 1; i < images.length; i++) {
        for (int i = 0; i < img_urls.length; i++) {
            imageList.add(img_urls[i]);
        }
        return imageList;
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int mPosition;

        public MyOnClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.checkcompare:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",Locale.CHINA);
                    Uri screenShot;
                    String fname = mContext.getExternalCacheDir().getAbsolutePath()+"/"+sdf.format(new Date())+ ".jpg";
                    View view = v.getRootView();
                    view.setDrawingCacheEnabled(true);
                    view.buildDrawingCache();
                    Bitmap bitmap = view.getDrawingCache();
                    File file = new File(fname);
                    if (file.exists())
                        file.delete();
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(bitmap!= null){
                        Log.e("file_screenshot","bitmap got!");
                        try{
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
//                            Log.e("file", fname + "outputdone.");
//                            Log.e("file_path", Uri.fromFile(file).toString());
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Log.e("file_error","bitmap is NULL!");
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        //别忘了注册FileProvider内容提供器
                        screenShot = FileProvider.getUriForFile(mContext, "com.example.btf.fileProvider", file);
//                        Log.e("imageUri_sdk>24", screenShot.toString());
                    } else {
                        screenShot = Uri.fromFile(file);
//                        Log.e("imageUri_sdk<24", screenShot.toString());
                    }
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                    //分享图片
                    intent.putExtra(Intent.EXTRA_STREAM, screenShot);
                    //分享文本
                    intent.putExtra(Intent.EXTRA_TEXT, "识别图片中蝴蝶的结果");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Activity activity = (Activity) mContext;
                    mContext.startActivity(Intent.createChooser(intent, activity.getTitle()));
                    break;
                case R.id.detail:
                    intent = new Intent(getContext(), InfoActivity.class);
                    intent.putExtra("butterflyNo", id);
                    intent.putExtra("activity", InfoActivity.class.getSimpleName());
//                    InfoDetail infoDetail = DataSupport.find(InfoDetail.class, id);
                    List<InfoDetail> infoDetail = DataSupport.where("b_id=?",String.valueOf(id)).find(InfoDetail.class);
                    //                    String[] images = infoDetail.getImagePath().split(",");
                    //                    ArrayList<String> imageList = new ArrayList<String>();
                    //                    for (int i = 0; i < images.length; i++) {
                    //                        imageList.add(images[i]);
                    //                    }
                    intent.putExtra("imageList", getImagePaths(infoDetail.get(0)));
                    mContext.startActivity(intent);

                    break;
                case R.id.homepage:
                    Intent intent1 = new Intent(getContext(), MainActivity.class);
                    mContext.startActivity(intent1);
                    break;
                default:
                    break;
            }
        }
    }

    public interface OnClickListener {
        void OnClick(View v, int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    private void setLayout() {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = mScreenWidth;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        //水平居中、底部
        this.getWindow().setAttributes(params);
    }

}
