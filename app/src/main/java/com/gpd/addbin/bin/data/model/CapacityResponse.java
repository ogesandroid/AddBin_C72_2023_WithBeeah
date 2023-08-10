
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CapacityResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("capacity_id")
    @Expose
    private String capacityId;
    @SerializedName("capacity_name")
    @Expose
    private String capacityName;
    @SerializedName("capacity_value")
    @Expose
    private String capacityValue;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(String capacityId) {
        this.capacityId = capacityId;
    }

    public String getCapacityName() {
        return capacityName;
    }

    public void setCapacityName(String capacityName) {
        this.capacityName = capacityName;
    }

    public String getCapacityValue() {
        return capacityValue;
    }

    public void setCapacityValue(String capacityValue) {
        this.capacityValue = capacityValue;
    }

}
