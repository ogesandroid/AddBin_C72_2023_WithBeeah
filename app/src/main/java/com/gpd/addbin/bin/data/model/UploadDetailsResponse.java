
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadDetailsResponse {

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
