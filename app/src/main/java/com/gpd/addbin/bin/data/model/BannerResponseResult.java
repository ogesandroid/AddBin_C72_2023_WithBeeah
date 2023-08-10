
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerResponseResult {

    @SerializedName("banner_id")
    @Expose
    private String bannerId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("banner_image")
    @Expose
    private String bannerImage;
    @SerializedName("banner_url")
    @Expose
    private String bannerUrl;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

}
