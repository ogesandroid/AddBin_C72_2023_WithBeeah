
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WillayatResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("willayat_id")
    @Expose
    private String willayatId;
    @SerializedName("willayat_name")
    @Expose
    private String willayatName;
    @SerializedName("willayat_value")
    @Expose
    private String willayatValue;
    @SerializedName("governorate_id")
    @Expose
    private String governorateId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWillayatId() {
        return willayatId;
    }

    public void setWillayatId(String willayatId) {
        this.willayatId = willayatId;
    }

    public String getWillayatName() {
        return willayatName;
    }

    public void setWillayatName(String willayatName) {
        this.willayatName = willayatName;
    }

    public String getWillayatValue() {
        return willayatValue;
    }

    public void setWillayatValue(String willayatValue) {
        this.willayatValue = willayatValue;
    }

    public String getGovernorateId() {
        return governorateId;
    }

    public void setGovernorateId(String governorateId) {
        this.governorateId = governorateId;
    }

}
