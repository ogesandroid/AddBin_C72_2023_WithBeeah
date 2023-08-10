
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BannerResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private ArrayList<BannerResponseResult> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<BannerResponseResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<BannerResponseResult> result) {
        this.result = result;
    }

}
