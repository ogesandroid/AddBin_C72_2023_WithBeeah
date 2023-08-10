package com.gpd.addbin.bin.data.remote;

public class ApiUtils {

    /* public static final String BASE_URL = "http://www.gpduae.com/Addbin/";
     public static String ICON_URL       = "http://gpduae.com/Addbin/data_folder/company/logo/";*/
//    public static final String BASE_URL = "http://192.168.10.111:8081/Anjitha/Gpd_technology/";
//    public static String ICON_URL       = "http://192.168.10.111:8081/Anjitha/Gpd_technology/data_folder/company/logo/";
    public static String BASE_URL = "";
    public static String ICON_URL = "";

    //    public static String BASE_URL = "http://ogesinfotech.com/Add_bin/";
//    public static String ICON_URL       = "http://ogesinfotech.com/Add_bin/data_folder/company/logo/";
    public static SOService getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(SOService.class);
    }
}
