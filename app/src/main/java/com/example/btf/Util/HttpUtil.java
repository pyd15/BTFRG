package com.example.btf.Util;


import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static long start_time;

    public static void sendOkHttpRequest(final String address, final okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpPicture(final String uploadUrl, final String localPath, final okhttp3.Callback callback) throws IOException {
        //修改各种 Timeout
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(6000, TimeUnit.SECONDS)
                .readTimeout(2000, TimeUnit.SECONDS)
                .writeTimeout(6000, TimeUnit.SECONDS)
                .build();
        Log.d("image_file", localPath);
        Log.d("image_file", Uri.parse(localPath).getPath());
        File file = new File(Uri.parse(localPath).getPath());
        MediaType imageType = MediaType.parse("image/jpg; charset=utf-8");
        RequestBody fileBody = RequestBody.create(imageType, file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "head_image", fileBody)
                .build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);

        start_time=System.currentTimeMillis();
//        Log.e("FATAL", String.valueOf(start_time));
    }
}
