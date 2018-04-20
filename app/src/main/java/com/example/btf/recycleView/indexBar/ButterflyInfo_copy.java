package com.example.btf.recycleView.indexBar;

import com.google.gson.annotations.SerializedName;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import org.litepal.annotation.Column;

/**
 * Created by Dr.P on 2017/11/12.
 * runas /user:Dr.P "cmd /k"
 */

public class ButterflyInfo_copy extends BaseIndexPinyinBean {
    @SerializedName("id")
    private int id;

    @SerializedName("image")
    private String imageUrl;

    @Column(unique = true)
    @SerializedName("name")
    private String name;

    @SerializedName("latinName")
    private String latinName;

    @SerializedName("type")
    private String type;

    @SerializedName("feature")
    private String feature;

    @SerializedName("area")
    private String area;

    @SerializedName("protect")
    private int protect;

    @SerializedName("rare")
    private int rare;

    @SerializedName("uniqueToChina")
    private int uniqueToChina;

    private String imagePath;

    private boolean isTop;//是否是最上面的 不需要被转化成拼音的

    public ButterflyInfo_copy() {
    }

    public ButterflyInfo_copy(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getLatinName() {
        return latinName;
    }

    public String getType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ButterflyInfo_copy setName(String name) {
        this.name = name;
        return this;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setProtect(int protect) {
        this.protect = protect;
    }

    public void setRare(int rare) {
        this.rare = rare;
    }

    public void setUniqueToChina(int uniqueToChina) {
        this.uniqueToChina = uniqueToChina;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String getTarget() {
        return name;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }

    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }
}
