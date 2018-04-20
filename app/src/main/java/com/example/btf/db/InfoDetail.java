package com.example.btf.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Dr.P on 2017/11/22.
 * runas /user:Dr.P "cmd /k"
 */

public class InfoDetail extends DataSupport {
    @SerializedName("id")
    private int b_id;

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

    public int getId() {
        return b_id;
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

    public String getFeature() {
        return feature;
    }

    public String getArea() {
        return area;
    }

    public int getProtect() {
        return protect;
    }

    public int getRare() {
        return rare;
    }

    public int getUniqueToChina() {
        return uniqueToChina;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(int id) {
        this.b_id = id;
    }

    public void setName(String name) {
        this.name = name;
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
}
