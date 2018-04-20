package com.example.btf.Util;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}