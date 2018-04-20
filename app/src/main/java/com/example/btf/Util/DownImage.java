package com.example.btf.Util;

import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.btf.db.InfoDetail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Dr.P on 2017/11/6.
 */

public class DownImage extends AsyncTask {

    private ImageView imageView;
    private InfoDetail infoDetail;
    //    private static final String _URL="http://120.78.72.153:8080";
    private static final String _URL = "http://40.125.207.182:8080";

    private InputStream is;
    private FileOutputStream fileOutputStream;

    public DownImage(InfoDetail infoDetail) {
        this.infoDetail = infoDetail;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String imagePaths = (String) params[0];
        String pathlist = "";
        String imagepath = "";
        if (imagePaths!=null) {
            if (!imagePaths.contains(".jpg")) {
                return "";
            }

            String[] strings = imagePaths.split(",");
            for (int i = 0; i < strings.length; i++) {
            }
            for (int i = 0; i < strings.length; i++) {
                imagepath = _URL + strings[i];
                String fileName = infoDetail.getName() + i + ".jpg";
                String directory = "/data/data/com.example.btf/files";
                File dir = new File(directory + "/btf/");
//                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/btf/");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                if (dir.exists() && dir.canWrite()) {
                    File imageFile = new File(dir.getAbsolutePath() + "/" + fileName);//directory
                    imageFile.setReadable(true);
                    imageFile.setWritable(true);
                    if (imageFile.exists()) {
                        pathlist = pathlist + "," + imageFile.getPath();
                        continue;
                    }
                    try {
                        //加载一个网络图片
                        is = new URL(imagepath).openStream();
                        byte[] data = readInputStream(is);
                        fileOutputStream = new FileOutputStream(imageFile);
                        fileOutputStream.write(data);
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pathlist = pathlist + "," + imageFile.getPath();
                }
            }
            return pathlist;
        } else
            return "";
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[4096];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
