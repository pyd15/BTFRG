package com.example.btf;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btf.Util.ActivityCollector;
import com.example.btf.Util.HttpUtil;
import com.example.btf.db.InfoDetail;
import com.example.btf.fragment.DIYDialog;
import com.example.btf.view.clip.ClipImageLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Dr.P on 2017/10/10.
 */

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    Button cancelbtn;
    Button use_photo;
    ClipImageLayout butterflyImageLayout;
    Toolbar toolbar;
    TextView butterflyContentText;

    String image_camera = null;
    String image_album = null;
    //        String uploadUrl = "http://120.78.72.153:8080/btf/identify.do";
    String uploadUrl = "http://40.125.207.182:8080/identify.do";
    static long end_time;

    Bitmap clipBitmap;
    File clipImage;
    DIYDialog resultDialog = null;
    private ProgressDialog progressDialog;

    private IntentFilter intentFilter;
    private NetworkChangeReciver networkChangeReciver;

    boolean camera_flag = false;
    boolean album_flag = false;
    boolean net_flag = true;
    boolean is_Front_Flag = false;
    Gson gson = new Gson();

    public static final String IMAGE_URI_CAMERA = "imagePath_camera";
    public static final String IMAGE_URI_ALBUM = "imagePath_Album";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.activity_image);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
        ActivityCollector.AddActivity(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_image);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        butterflyImageLayout = (ClipImageLayout) findViewById(R.id.butterfly_image_view);
        butterflyContentText = (TextView) findViewById(R.id.butterfly_content_text);
        cancelbtn = (Button) findViewById(R.id.cancel);
        use_photo = (Button) findViewById(R.id.use_photo);
        cancelbtn.setOnClickListener(this);
        use_photo.setOnClickListener(this);

        Intent intent = getIntent();
        Uri imageUri1 = null;
        if (intent.getStringExtra(IMAGE_URI_CAMERA) != null) {
            image_camera = intent.getStringExtra(IMAGE_URI_CAMERA);
            imageUri1 = Uri.parse(image_camera);
        }
        if (intent.getStringExtra(IMAGE_URI_ALBUM) != null) {
            image_album = intent.getStringExtra(IMAGE_URI_ALBUM);
        }
        if (image_camera != null) {
            try {
                butterflyImageLayout.setImagePath(image_camera, 1);//Uri.parse(image_camera).getPath()
                camera_flag = true;
            } catch (FileNotFoundException e) {
            }
        } else if (image_album != null) {
            try {
                butterflyImageLayout.setImagePath(image_album, 2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            album_flag = true;
        } else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }

        String butterflyContent = "双指移动并缩放一只蝴蝶至方框内";
        butterflyContentText.setText(butterflyContent);
        butterflyContentText.setGravity(Gravity.CENTER_VERTICAL);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReciver = new NetworkChangeReciver();
        registerReceiver(networkChangeReciver, intentFilter);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                onBackPressed();
                break;
            case R.id.use_photo:
                if (camera_flag && net_flag) {
                    showProgressDialog();
                    try {
                        clipImage = new File(getExternalCacheDir().getPath() + "/clip.jpg");
                        if (clipImage.exists()) {
                            clipImage.delete();
                        }
                        clipImage.createNewFile();
                        FileOutputStream fos = new FileOutputStream(clipImage);
                        clipBitmap = butterflyImageLayout.clip();
                        if (clipBitmap != null) {
                            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);//将clipBitmap写入clipImage中
                            fos.flush();
                            fos.close();
                        } else {
                        }
                        if (clipImage.getPath() != null) {
                        }
                        HttpUtil.sendOkHttpPicture(uploadUrl, clipImage.getPath(), new Callback() {
                            @Override
                            public void
                            onResponse(Call call, Response response) throws IOException {
                                final String responeData = response.body().string();
                                final InfoDetail butterflyInfo1 = gson.fromJson(responeData, new TypeToken<InfoDetail>() {
                                }.getType());
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeProgressDialog();
                                            end_time = System.currentTimeMillis();
                                            if (is_Front_Flag) {
                                                resultDialog = new DIYDialog(ImageActivity.this, clipImage.getPath(), butterflyInfo1.getId());//butterflyInfo1.getId()
                                                resultDialog.show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                ImageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ImageActivity.this, "请再试一次！", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (album_flag && net_flag) {
                    showProgressDialog();
                    try {
                        clipImage = new File(getExternalCacheDir().getPath() + "/clip.jpg");//设置文件名称
                        if (clipImage.exists()) {
                            clipImage.delete();
                        }
                        clipImage.createNewFile();
                        FileOutputStream fos = new FileOutputStream(clipImage);
                        clipBitmap = butterflyImageLayout.clip();
                        if (clipBitmap != null) {
                            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            fos.flush();
                            fos.close();
                        } else {
//                            Toast.makeText
//                                    (this, "asdfsaf", Toast.LENGTH_SHORT).show();
                        }
                        if (clipImage.getPath() != null) {
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpUtil.sendOkHttpPicture(uploadUrl, clipImage.getPath(), new Callback() {
                            @Override
                            public void
                            onResponse(Call call, Response response) throws IOException {
                                final String responeData = response.body().string();
                                final InfoDetail butterflyInfo1 = gson.fromJson(responeData, new TypeToken<InfoDetail>() {
                                }.getType());
                                if (responeData != null) {
                                    ImageActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeProgressDialog();
                                            end_time = System.currentTimeMillis();
                                            if (is_Front_Flag) {
                                                resultDialog = new DIYDialog(ImageActivity.this, clipImage.getPath(), butterflyInfo1.getId());//
                                                resultDialog.show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void
                            onFailure(Call call, IOException e) {
                                ImageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ImageActivity.this, "请再试一次！", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ImageActivity.this, "网络无连接!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ImageActivity.this);
            progressDialog.setMessage("识别中，请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        File file=null;
        if (camera_flag) {
            file = new File(Uri.parse(image_camera).getPath());
//            Log.e("path_image", file.getAbsolutePath());
        }
        if (file.exists()) {
            file.delete();
        }
        resultDialog = null;
        finish();
        overridePendingTransition
                (android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        is_Front_Flag = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        is_Front_Flag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReciver);
    }

    class NetworkChangeReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                net_flag = true;
            } else {
                net_flag = false;
                Toast.makeText(context, "无法连接至服务器，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

