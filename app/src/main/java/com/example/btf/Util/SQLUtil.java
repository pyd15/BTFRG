package com.example.btf.Util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dr.P on 2018/1/29.
 * runas /user:Dr.P "cmd /k"
 */

public class SQLUtil {
    /**
     * 这个类就是实现从assets目录读取数据库文件然后写入SDcard中,如果在SDcard中存在，就打开数据库，不存在就从assets目录下复制过去
     * @author Big_Adamapple
     *
     */
        //数据库存储路径
        String filePath = "data/data/com.example.btf/databases/ButterflyStore.db";
        //数据库存放的文件夹 data/data/com.example.butterflyrecognition/databases 下面
        String pathStr = "data/data/com.example.btf/databases";

    public static boolean createDatabase(Context context) {
        final int BUFFER_SIZE = 200000;
        final String DB_NAME = "ButterflyStore.db"; //保存的数据库文件名
        final String PACKAGE_NAME = "com.example.btf";
        final String DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + PACKAGE_NAME;  //在手机里存放数据库的位置
        final String dbPath = DB_PATH + "/databases/";
        final String dbfile = dbPath + DB_NAME;
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入
                File filepath = new File(dbPath);
                if (!filepath.exists()) {//若路径不存在则先创建文件夹
                    if (filepath.mkdir()) {
                    } else {
                    }
                }
                if (filepath.exists()) {
                    //得到资源
                    AssetManager am = context.getAssets();
                    //得到数据库的输入流
                    InputStream is = am.open("ButterflyStore.db");
                    FileOutputStream fos = new FileOutputStream(dbfile);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int count = 0;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    is.close();
                }
                return true;
            } else {
                return true;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return false;
    }
}
