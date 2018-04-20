package com.example.btf.Util;

import android.content.SharedPreferences;

import com.example.btf.db.ButterflyInfo;
import com.example.btf.db.InfoDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dr.P on 2017/11/10.
 * runas /user:Dr.P "cmd /k"
 */

public class HttpAction {

    private SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public static boolean parseJSONWithGSON(String jsonData) throws ExecutionException, InterruptedException {
        try {
            Boolean flag = false;
            String msg = jsonData.substring(jsonData.length() - 1, jsonData.length());
            Gson gson = new Gson();
            ButterflyInfo butterflyInfo1 = gson.fromJson(jsonData.substring(0, jsonData.length() - 2), new TypeToken<ButterflyInfo>() {
            }.getType());
            List<InfoDetail> butterflyInfoList = butterflyInfo1.infoDetailList;
//            Log.d("name_size", String.valueOf(butterflyInfoList.size()));
            if (msg.equals("+")) {
                flag = InsertSQL(butterflyInfoList);
            } else if (msg.equals("-")) {
                flag = DeleteSQL(butterflyInfoList);
            } else if (msg.equals("=")) {
                flag = UpdateSQL(butterflyInfoList);
            } else {
//                Log.d("sqlerror", "No match!");
            }
//            Log.e("parse", String.valueOf(flag));
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.e("default", String.valueOf(false));
        return false;
    }

    private static boolean InsertSQL(List<InfoDetail> butterflyInfoList) throws ExecutionException, InterruptedException {
        boolean flag = false;
        Connector.getDatabase();
        for (InfoDetail butterflyInfo : butterflyInfoList) {
            butterflyInfo.setImageUrl(butterflyInfo.getImageUrl());
            butterflyInfo.setImagePath((String) new DownImage(butterflyInfo).execute(butterflyInfo.getImageUrl()).get());
            flag = butterflyInfo.saveOrUpdate("b_id=?", String.valueOf(butterflyInfo.getId()));
//         flag=butterflyInfo.save();
        }
//        Log.e("Insert-flag", String.valueOf(flag));
        return flag;
    }

    private static boolean DeleteSQL(List<InfoDetail> butterflyInfoList) {
        boolean flag = false;
        Connector.getDatabase();
        String images[];
        File file;
        for (InfoDetail butterflyInfo : butterflyInfoList) {
            List<InfoDetail> oneInfo = DataSupport.where("name=?", butterflyInfo.getName()).find(InfoDetail.class);
            images = oneInfo.get(0).getImagePath().split(",");
            for (String imagePath : images) {
                file = new File(imagePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            oneInfo.get(0).delete();
            flag = true;
        }
//        Log.e("Delete-flag", String.valueOf(flag));
        return flag;
    }

    private static boolean UpdateSQL(List<InfoDetail> butterflyInfoList) throws ExecutionException, InterruptedException {
        boolean flag = false;
        Connector.getDatabase();
        List<InfoDetail> nameList = DataSupport.findAll(InfoDetail.class);
        int count = 0;
        for (int i = 0, j = 0; i < nameList.size(); i++) {
//            Log.e("i+j+size", String.valueOf(i) + "-" + String.valueOf(j) + "-" + String.valueOf(butterflyInfoList.size()));
//            if (j<butterflyInfoList.size())
            if (nameList.get(i).getId() == butterflyInfoList.get(j).getId()) {
//                Log.e("name_name", nameList.get(i).getName() + "-" + butterflyInfoList.get(j).getName());
//                Log.e("latin_latin", nameList.get(i).getLatinName() + "-" + butterflyInfoList.get(j).getLatinName());
//                Log.e("url_url", nameList.get(i).getImageUrl()+ "-" + butterflyInfoList.get(j).getImageUrl());
//                Log.e("path_path", nameList.get(i).getImagePath() + "-" + butterflyInfoList.get(j).getImagePath());
//                Log.e("type_type", nameList.get(i).getType() + "-" + butterflyInfoList.get(j).getType());
//                Log.e("area_area", nameList.get(i).getArea() + "-" + butterflyInfoList.get(j).getArea());
//                Log.e("protect_protect", nameList.get(i).getProtect() + "-" + butterflyInfoList.get(j).getProtect());
//                Log.e("unique_unique", nameList.get(i).getUniqueToChina() + "-" + butterflyInfoList.get(j).getUniqueToChina());
//                Log.e("feature_feature", nameList.get(i).getFeature() + "-" + butterflyInfoList.get(j).getFeature());
                nameList.get(i).setName(butterflyInfoList.get(j).getName());
                nameList.get(i).setImageUrl(butterflyInfoList.get(j).getImageUrl());
                nameList.get(i).setArea(butterflyInfoList.get(j).getArea());
                nameList.get(i).setFeature(butterflyInfoList.get(j).getFeature());
                nameList.get(i).setId(butterflyInfoList.get(j).getId());
                nameList.get(i).setLatinName(butterflyInfoList.get(j).getLatinName());
                nameList.get(i).setRare(butterflyInfoList.get(j).getRare());
                nameList.get(i).setType(butterflyInfoList.get(j).getType());
                nameList.get(i).setUniqueToChina(butterflyInfoList.get(j).getUniqueToChina());
                nameList.get(i).setImagePath((String) new DownImage(butterflyInfoList.get(j)).execute(butterflyInfoList.get(j).getImageUrl()).get());
                 j++;
                count += nameList.get(i).updateAll("b_id=?", String.valueOf(nameList.get(i).getId()));
            }
//            else break;
        }
//        Log.e("count", String.valueOf(count));
        if (count == butterflyInfoList.size()) {
            flag = true;
        }
//        Log.e("Update-flag", String.valueOf(flag));
        return flag;
    }
}