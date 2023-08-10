
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManufacturerResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("manufacturer_id")
    @Expose
    private String manufacturerId;
    @SerializedName("manufacturer_name")
    @Expose
    private String manufacturerName;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

}
