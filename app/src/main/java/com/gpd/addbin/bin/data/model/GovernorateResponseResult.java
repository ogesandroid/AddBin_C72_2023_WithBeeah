package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GovernorateResponseResult {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("governorate_name")
    @Expose
    private String governorateName;
    @SerializedName("governorate_value")
    @Expose
    private String governorateValue;
    @SerializedName("governorate_id")
    @Expose
    private String governorateId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGovernorateName() {
        return governorateName;
    }

    public void setGovernorateName(String governorateName) {
        this.governorateName = governorateName;
    }

    public String getGovernorateValue() {
        return governorateValue;
    }

    public void setGovernorateValue(String governorateValue) {
        this.governorateValue = governorateValue;
    }

    public String getGovernorateId() {
        return governorateId;
    }

    public void setGovernorateId(String governorateId) {
        this.governorateId = governorateId;
    }

}