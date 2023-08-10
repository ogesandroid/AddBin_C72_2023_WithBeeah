
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SuperSaversResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<SuperSaversResponseResult> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SuperSaversResponseResult> getResult() {
        return result;
    }

    public void setResult(List<SuperSaversResponseResult> result) {
        this.result = result;
    }

}
