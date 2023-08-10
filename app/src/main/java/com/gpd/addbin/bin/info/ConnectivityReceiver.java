package com.gpd.addbin.bin.info;

/**
 * Created by Lincoln on 18/03/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cocosw.favor.FavorAdapter;
import com.gpd.addbin.R;
import com.gpd.addbin.bin.data.model.UplodaLocalBinDetailsModel;
import com.gpd.addbin.bin.data.remote.ApiUtils;
import com.gpd.addbin.bin.db.BinDetails;
import com.gpd.addbin.bin.db.BinDetailsDao;
import com.gpd.addbin.bin.db.DaoSession;
import com.gpd.addbin.bin.db.DataBaseHelper;


import org.greenrobot.greendao.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectivityReceiver
        extends BroadcastReceiver {


    Context mContext;
    public static ConnectivityReceiverListener connectivityReceiverListener;
    private BinDetailsDao binDetailsDao;
    private Query<BinDetails> binDetailsQuery;
    DaoSession daoSession;
    DataBaseHelper myDb;
    // private String UPLOAD_URL = "http://www.gpduae.com/Addbin/get_oges_api.php?p=5";
//    private String UPLOAD_URL = "http://ogesinfotech.com/Add_bin/get_oges_api.php?p=5";
    private String UPLOAD_URL = "";
    //    private String UPLOAD_URL = "http://192.168.10.111:8081/Anjitha/Gpd_technology/get_oges_api.php?p=5";
    String statusCreation;

    View layoutSuccesUpload, layoutNoDataToUpload, layoutCheckingForData;
    Account accountCurrentBinDetails;


    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        this.mContext = context;

        UPLOAD_URL = ApiUtils.BASE_URL + "get_oges_api.php?p=5"; //newly added 22.09.2020

        Log.e("AddBin", "inside onReceive");

        accountCurrentBinDetails = new FavorAdapter.Builder(mContext).build().create(Account.class);

        myDb = new DataBaseHelper(context);

        getAllBinData();

        LayoutInflater liSuccessUpload = LayoutInflater.from(mContext);
        layoutSuccesUpload = liSuccessUpload.inflate(R.layout.layout_succes_upload_broadcast_receiver, null);

        layoutNoDataToUpload = liSuccessUpload.inflate(R.layout.no_data_to_upload_broadcast_receiver, null);

        layoutCheckingForData = liSuccessUpload.inflate(R.layout.layout_checking_for_data, null);


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {


            if ((accountCurrentBinDetails.getIsDatabaseSetForFirstTime()).equals("true")) {
                Log.e("AddBin", "network present");

                Toast successUpload = new Toast(mContext);
                successUpload.setDuration(Toast.LENGTH_SHORT);
                successUpload.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                successUpload.setView(layoutCheckingForData);//setting the view of custom toast layout
                successUpload.show();

                testUpload();

            }


        } else {

            Log.e("AddBin", "no network present");
        }

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        } else {
            Log.e("AddBin", "connectivity listener null");
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) AppController.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public void getAllBinData() {


        Log.e("AddBin", "inside getAllBinData of broadcastReceiver");

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            Log.e("AddBin", "No data found zero count");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("GovernorateId :" + res.getString(1) + "\n");
            buffer.append("Willayatd :" + res.getString(2) + "\n");
            buffer.append("capacityId :" + res.getString(3) + "\n");
            buffer.append("manufacturer_id :" + res.getString(4) + "\n");
            buffer.append("scan_type :" + res.getString(5) + "\n");
            buffer.append("bin_rfid :" + res.getString(6) + "\n");
            buffer.append("bin_lat :" + res.getString(7) + "\n");
            buffer.append("bin_longi :" + res.getString(8) + "\n");
            buffer.append("manual_entry :" + res.getString(9) + "\n");
            buffer.append("bin_status :" + res.getString(10) + "\n");
            buffer.append("bin_time :" + res.getString(11) + "\n");
            buffer.append("company_id :" + res.getString(12) + "\n");
        }

        // Show all data
        Log.e("AddBin", " Data from local db is " + buffer.toString());

    }


    public void testUpload() {

        Log.e("AddBin", "inside testUpload");

        JSONObject jsonObject = new JSONObject();

        ArrayList<UplodaLocalBinDetailsModel> contactList = new ArrayList<UplodaLocalBinDetailsModel>();

        Cursor c1 = myDb.getAllData();

        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    UplodaLocalBinDetailsModel contactListItems = new UplodaLocalBinDetailsModel();

                    contactListItems.setgovernorate_id(c1.getString(1));
                    contactListItems.setwillayat_id(c1.getString(2));
                    contactListItems.setcapacity_id(c1.getString(3));
                    contactListItems.setmanufacturer_id(c1.getString(4));
                    contactListItems.set_scan_type(c1.getString(5));
                    contactListItems.setbin_rfid(c1.getString(6));
                    contactListItems.setbin_lat(c1.getString(7));
                    contactListItems.setbin_longi(c1.getString(8));
                    contactListItems.setmanual_entry(c1.getString(9));
                    contactListItems.setbin_status(c1.getString(10));
                    contactListItems.setbin_time(c1.getString(11));
                    contactListItems.setCompany_id(c1.getString(12));

                    contactList.add(contactListItems);

                } while (c1.moveToNext());
            }
        }
        c1.close();

        if (contactList != null && contactList.size() > 0) {
            JSONArray array = new JSONArray();
            for (UplodaLocalBinDetailsModel selectedDrugDetails : contactList) {
                try {

                    JSONObject json = new JSONObject();

                    json.put("governorate_id", selectedDrugDetails.getgovernorate_id());
                    json.put("willayat_id", selectedDrugDetails.getwillayat_id());
                    json.put("capacity_id", selectedDrugDetails.getcapacity_id());
                    json.put("manufacturer_id", selectedDrugDetails.getmanufacturer_id());
                    json.put("scan_type", selectedDrugDetails.get_scan_type());
                    json.put("bin_rfid", selectedDrugDetails.getbin_rfid());
                    json.put("bin_lat", selectedDrugDetails.getbin_lat());
                    json.put("bin_longi", selectedDrugDetails.getbin_longi());
                    json.put("manual_entry", selectedDrugDetails.getmanual_entry());
                    json.put("bin_status", "1");
                    json.put("bin_time", selectedDrugDetails.getbin_time());
                    json.put("company_id", selectedDrugDetails.getCompany_id());

                    array.put(json);
                } catch (JSONException e) {

                    Log.e("AddBin", "error in json list making  ");
                }

            }
            try {
                jsonObject.put("bin_details", array);
                Log.e("AddBin", "array created  " + array);

            } catch (JSONException e) {

                Log.e("AddBin", "error in array making ");
            }


            JsonObjectRequest myRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        statusCreation = response.getString("status");
                        if (statusCreation.equals("exist")) {
                            Toast.makeText(mContext, "Add Bin - details uploaded to server. RFID or Beah Code already exist ", Toast.LENGTH_LONG).show();
                        }
                        if (statusCreation.equals("true")) {

                            Log.e("AddBin", "Success from volley response");


                            Toast.makeText(mContext, "Add Bin - details uploaded to server ", Toast.LENGTH_LONG).show();

                            // Toast.makeText(Registration.this, statusCreation, Toast.LENGTH_SHORT).show();


                            Toast successUpload = new Toast(mContext);
                            successUpload.setDuration(Toast.LENGTH_SHORT);
                            successUpload.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            successUpload.setView(layoutSuccesUpload);//setting the view of custom toast layout
                            successUpload.show();

                            myDb.updateUploadStatus();

                        }

                    } catch (Exception e) {

                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("AddBin", "Error from volley response");

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", "My useragent");
                    return headers;
                }
            };

            int socketTimeout = 30000; // 30 seconds. You can change it
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            myRequest.setRetryPolicy(policy);

            AppController.getInstance().addToRequestQueue(myRequest, "tag_home_user");

        } else {
            Log.e("AddBin", "No data to upload");

            Toast successUpload = new Toast(mContext);
            successUpload.setDuration(Toast.LENGTH_SHORT);
            successUpload.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            successUpload.setView(layoutNoDataToUpload);//setting the view of custom toast layout
            successUpload.show();

        }


    }
    // test upload ends


}