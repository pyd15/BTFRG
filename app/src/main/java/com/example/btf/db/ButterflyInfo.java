package com.example.btf.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Dr.P on 2017/11/6.
 */

public class ButterflyInfo extends DataSupport {

    @SerializedName("code")
    int code;

    @SerializedName("message")
    String message;

    @SerializedName("data")
    public List<InfoDetail> infoDetailList;

    public ButterflyInfo() {
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    InfoDetail infoDetail;

}
