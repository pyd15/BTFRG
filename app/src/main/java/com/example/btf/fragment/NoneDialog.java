package com.example.btf.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.btf.MainActivity;
import com.example.btf.R;
import com.example.btf.db.InfoDetail;

import java.util.ArrayList;

/**
 * Created by Dr.P on 2017/12/31.
 * runas /user:Dr.P "cmd /k"
 */

public class NoneDialog extends BaseDialog {

    private String clipImagePath;
    private int id = 1;

    private OnClickListener mListener;

    public NoneDialog(Context context, String clipImagePath, int id) {
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
        setLayout();
        MyOnClickListener listener = new MyOnClickListener(0);
        TextView homepage = (TextView) findViewById(R.id.homepage_none);
        homepage.setOnClickListener(listener);
    }

    private View initView() {
        View view = getLayoutInflater().inflate(R.layout.none_dialog, null);
        return view;
    }

    private ArrayList<String> getImagePaths(InfoDetail infoDetail) {
        String[] images = infoDetail.getImagePath().split(",");
        ArrayList<String> imageList = new ArrayList<String>();
        for (int i = 1; i < images.length; i++) {
            imageList.add(images[i]);
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
            switch (v.getId()) {
                case R.id.homepage_none:
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
        ;//水平居中、底部
        this.getWindow().setAttributes(params);
    }

}
