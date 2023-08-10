
package com.gpd.addbin.bin.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuperSaversResponseResult {

    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("item_image")
    @Expose
    private String itemImage;
    @SerializedName("item_price")
    @Expose
    private String itemPrice;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

}
