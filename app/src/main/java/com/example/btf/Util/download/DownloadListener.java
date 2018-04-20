package com.example.btf.Util.download;

/**
 * Created by Dr.P on 2017/10/24.
 */

public interface DownloadListener {
    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

    void onProgress(int progress);
}
