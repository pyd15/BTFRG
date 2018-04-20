package com.example.btf;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.btf.Util.ActivityCollector;
import com.example.btf.Util.ImageUtil;
import com.example.btf.Util.SQLUtil;
import com.example.btf.Util.download.DownloadService;
import com.example.btf.recycleView.ButterflyActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Dr.P on 2017/10/10.
 * 2017/12/22
 * 待优化问题：
 * 1.识别图片界面：
 * 加上标题栏较好（完成）
 * 图片最好填充整个屏幕，被遮挡部分通过手指拖动查看
 * 识别结果对话框的美化，且受到的识别数据应存储起来以方便查看详细结果后返回识别界面再次查看（已解决）
 * 底部按钮的美化（已解决）
 * 拍照或选图的图片的加载速度（已解决）
 * 增加一个分享识别结果的功能（保存屏幕截图分享即可）
 * 2.整个应用的图标设计和美化
 * 3.主界面三个按钮的触摸反馈
 * 4.图片相关匹配率前五的列表（可能要做）
 * 5.搜索界面：
 * 搜索栏过滤的优化，目前问题：添加对拉丁名的过滤以实现对拉丁名的匹配搜索（已解决）
 * 下拉刷新：获取数据后先和数据库中内容比较，没有的才添加，多余的删除（部分解决，存在更新后界面未正确加载问题）
 * 更新时某种蝴蝶无图片的问题、须更新data.txt文件
 * 6.不同分辨率屏幕间适配
 * 7.第一次使用时的引导界面
 * 8.滑动菜单中各项子菜单的具体内容
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO_FORCAMERA = 5;
    public static final int CROP_PHOTO_FORALBUM = 4;
    public static final String TAKE_PHOTO_PATH="TAKE_PHOTO";
    private Uri imageUri = null;
    Uri imageUri1 = null;
    private String imagePath;
    private String takePath;
    private File outputImage;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button takePhoto;
    private Button choosePhoto;
    private Button search;

    private ProgressDialog progressDialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    String address = "http://120.78.72.153:8080/btf/getInfo.do";
    private boolean readImgFlag = false;
    private boolean readDBFlag = false;
    private int statusBarColor= 0xff000000;
    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadBinder=(DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(TAKE_PHOTO_PATH, ""));
//            Log.e(TAKE_PHOTO_PATH + "-save", takePath);
        }
//        else
//            Toast.makeText(this, "Take photo fail!", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.AddActivity(this);
//        setWindowStatusBarColor(this,R.color.toolbar_edge);
//        StatusBarUtil.setColor(this,getResources().getColor(R.color.toolbar_edge));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(20);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu2);
        }
//        actionBar.setElevation(100f);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_call:
                        Toast.makeText(MainActivity.this, "请联系#18927512657#", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_friends:
                        Toast.makeText(MainActivity.this, "#蝴蝶识别项目组#", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_version:
                        String url="http://40.125.207.182:8080/apk/btfrg.apk";
                        //https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe
//                        downloadBinder.startDownload(url);
//                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        Notification notification=getNotification("下载中...",0);
//                        notificationManager.notify(1,notification);
//                        installAPK(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/btfrg.apk");
                        Toast.makeText(MainActivity.this, "#当前版本#-"+getVersionCode(getApplicationContext()), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_mail:
                        Toast.makeText(MainActivity.this, "#拍照或选图上传即可#", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_task:
                        Toast.makeText(MainActivity.this, "#任务完成#", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        takePhoto = (Button) findViewById(R.id.take_photo);
        choosePhoto = (Button) findViewById(R.id.choose_from_album);
        search = (Button) findViewById(R.id.search);

        takePhoto.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        search.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //动态申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        sp = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE);
        editor = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE).edit();
        readImgFlag = sp.getBoolean("IMG_EXIST", false);
        readDBFlag = sp.getBoolean("DB_EXIST", false);
        if (!readDBFlag) {
            readDBFlag = SQLUtil.createDatabase(getApplicationContext());
            editor.putBoolean("DB_EXIST", readDBFlag);
        }
        if (!readImgFlag) {
            ImageUtil.saveImageToLocal(this);
            readImgFlag=true;
            editor.putBoolean("IMG_EXIST", readImgFlag);
        }
        editor.apply();

        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);//启动服务
        bindService(intent, connection, BIND_AUTO_CREATE);

    }

    private Notification getNotification(String title, int progress) {
        Notification notification;
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_btf_launcher);
        builder.setContent(remoteViews);//设置自定义布局
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        if (progress > 0) {
            //当progress>0时才显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        notification = builder.build();
        return notification;
    }

    private void installAPK(String filePath) {
        File file = new File(filePath);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                //创建File对象用于存储拍摄的照片
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date=null;
//                    date = sdf.format(new Date());
//                System.currentTimeMillis()
//                new Date()
                outputImage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),(int)(Math.random()*1000)+".jpg");///storage/emulated/0/tempImage.jpg Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                        getExternalCacheDir()
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Log.e("path", outputImage.getAbsolutePath());
                takePath=outputImage.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= 24) {
                    //别忘了注册FileProvider内容提供器
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.btf.fileProvider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CROP_PHOTO_FORCAMERA);
                break;
            case R.id.choose_from_album:
                openAlbum();
                break;
            case R.id.search:
                    Intent intent1 = new Intent(MainActivity.this, ButterflyActivity.class);
                    intent1.putExtra("activity", MainActivity.class.getSimpleName());
                    startActivity(intent1);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    private void openAlbum() {
        Intent intent1 = new Intent(Intent.ACTION_PICK);//ACTION_PICK Intent.ACTION_GET_CONTENT//"android.intent.action.GET_CONTENT"
        intent1.setType("image/*");
        startActivityForResult(intent1, CHOOSE_PHOTO);//打开相册
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri!=null){
            outState.putString(TAKE_PHOTO_PATH, imageUri.toString());
//            Log.e(TAKE_PHOTO_PATH, imageUri.toString());
        }

//        outState.put
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent2 = new Intent(this, ImageActivity.class);
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent2.putExtra("imagePath_camera", imageUri.toString());
                    startActivity(intent2);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上系统用此方法处理图片
                        imagePath = handleImageOnKitKat(data);
//                        Log.e("MainActivity", imagePath);
                        imageUri1 = data.getData();
                    } else {
                        //其他系统用此方法处理图片
                        imageUri1 = handleImageBeforeKitKat(data);
                        imageUri = imageUri1;
                    }
                    Intent intent = new Intent(this, ImageActivity.class);
                    intent.setDataAndType(imageUri1, "image/*");
//                    intent.putExtra("crop", true);//允许裁剪/
//                    intent.putExtra("scale", true);//允许缩放
                    if (imagePath != null) {
                        intent.putExtra("imagePath_Album", imagePath);
                    } else {
                        intent.putExtra("imagePath_Album", getImagePath(imageUri1,null));
                    }
                    startActivity(intent);
                }
                break;
            case CROP_PHOTO_FORCAMERA:
                if (resultCode == RESULT_OK) {
                    Intent intent2 = new Intent(this, ImageActivity.class);
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent2.putExtra("imagePath_camera", imageUri.toString());
                    startActivity(intent2);
                }

                break;
            case CROP_PHOTO_FORALBUM:
                if (resultCode == RESULT_OK) {
                    Intent intent1 = new Intent(this, ImageActivity.class);
                    if (imagePath != null) {
                        intent1.putExtra("imagePath_Album", imagePath);
                    } else {
                        intent1.putExtra("imagePath_Album", imageUri1.getPath());
                    }
                    startActivity(intent1);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //若为document类型的uri则通过document id处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads"), Long.valueOf(docID));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //若为content类型的url，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //若为file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private Uri handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        return uri;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Failed to get image!", Toast.LENGTH_SHORT).show();
        }
        return path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
            new AlertDialog.Builder(this).setTitle("确认退出吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //退出APP
//                            MainActivity.this.finish();
                            ActivityCollector.finishAll();
                            //异常导致app挂掉,需要发送完数据后，kill掉死掉的APP。
                            //int myPid=android.os.Process.myPid();
                            //android.os.Process.killProcess(myPid);
                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //nothing to do
                        }
                    })
                    .show();
    }

    /**
     * get App versionCode
     * @param context
     * @return
     */
    public String getVersionCode(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionCode=packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * get App versionName
     * @param context
     * @return
     */
    public String getVersionName(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
