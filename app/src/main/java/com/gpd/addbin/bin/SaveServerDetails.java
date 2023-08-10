package com.gpd.addbin.bin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cocosw.favor.FavorAdapter;

import com.google.android.material.snackbar.Snackbar;
import com.gpd.addbin.R;
import com.gpd.addbin.bin.data.model.CapacityResponse;
import com.gpd.addbin.bin.data.model.GovernorateResponse;
import com.gpd.addbin.bin.data.model.ManufacturerResponse;
import com.gpd.addbin.bin.data.model.WillayatResponse;
import com.gpd.addbin.bin.data.remote.ApiUtils;
import com.gpd.addbin.bin.data.remote.SOService;
import com.gpd.addbin.bin.db.Capacity;
import com.gpd.addbin.bin.db.CapacityDao;
import com.gpd.addbin.bin.db.DaoSession;
import com.gpd.addbin.bin.db.Governorate;
import com.gpd.addbin.bin.db.GovernorateDao;
import com.gpd.addbin.bin.db.Manufacturer;
import com.gpd.addbin.bin.db.ManufacturerDao;
import com.gpd.addbin.bin.db.Willayat;
import com.gpd.addbin.bin.db.WillayatDao;
import com.gpd.addbin.bin.info.Account;
import com.gpd.addbin.bin.info.AppController;
import com.gpd.addbin.bin.info.ConnectivityReceiver;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveServerDetails extends AppCompatActivity {

    Governorate governorateObj;
    Willayat willayatObj;
    Capacity capacityObj;
    Manufacturer manufacturerObj;

    Button btUpdateDetails;
   DaoSession daoSession;
    ProgressDialog loading;
    private SOService mService;

    private ArrayList<GovernorateResponse> arrayListGovernorate;
    private ArrayList<WillayatResponse> arrayListWillayat;
    private ArrayList<CapacityResponse> arrayListCapacity;
    private ArrayList<ManufacturerResponse> arrayListManufacturer;

    private GovernorateDao governorateDao;
    private WillayatDao willayatDao;
    private CapacityDao capacityDao;
    private ManufacturerDao manufacturerDao;

    int closeDialogCountSucces = 0,closeDialogCountFailure = 0;

    Account accountCurrentBinDetails;
    TinyDB tinyDB;
//    private String mService;
    ImageView iv_img_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_server_details);

        mService = ApiUtils.getSOService();
        tinyDB=new TinyDB(this);

//        mService = tinyDB.preference.getString("base_url", "") + "/get_oges_api.php?";
        accountCurrentBinDetails = new FavorAdapter.Builder(SaveServerDetails.this).build().create(Account.class);

        daoSession = ((AppController) getApplication()).getDaoSession();

        governorateDao = daoSession.getGovernorateDao();
        willayatDao = daoSession.getWillayatDao();
        capacityDao = daoSession.getCapacityDao();
        manufacturerDao = daoSession.getManufacturerDao();


        btUpdateDetails = (Button) findViewById(R.id.bt_update_SaveServerDetails);
        iv_img_icon = (ImageView) findViewById(R.id.iv_img_icon);

        btUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateDatabaseFromHere();

            }
        });

        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            iv_img_icon.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID()+"_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
            Picasso.with(this).load(file).into(iv_img_icon);
        }
    } // onCreate ends


    public void updateDatabaseFromHere(){


        // earlier done like this , but if we clear this in first place and no data got from net ,
        // user wont have old data also , so moving it to after getting data from server

        checkConnection();

    } // updateDatabaseFromHere

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

            // if internet is present upload , no problem

            getServerData();

            message = "Connected to Internet";
            color = Color.WHITE;

            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_root_save_server_details), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();

        } else {

            message = "Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_root_save_server_details), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    } // showSnack

    public void getServerData() {


        loading = ProgressDialog.show(SaveServerDetails.this,"Updating Database","Please wait...",false,false);
        Log.d("BinApp", "inside getServerData");


        mService.getGovernorate1("1",accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<GovernorateResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GovernorateResponse>> call, Response<ArrayList<GovernorateResponse>> response) {

                if(response.isSuccessful()) {

                    Log.e("BinApp", "Response Governorate success"+response.body());

                    arrayListGovernorate = response.body();

                    gotResponseFromAllTables(1,0);

                }else {

                    gotResponseFromAllTables(0,1);

                    int statusCode  = response.code();

                    String errorDetails = response.message();

                    Log.d("BinApp", "Response Governorate failure code " + statusCode + "error is " + errorDetails);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GovernorateResponse>> call, Throwable t) {
                showErrorMessage("Governorate");


                Log.d("BinApp", "error loading from API Governorate " + t.toString());
                gotResponseFromAllTables(0,1);

            }
        }); // getGovernorateDetails


        mService.getWillayat1("2",accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<WillayatResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<WillayatResponse>> call, Response<ArrayList<WillayatResponse>> response) {

                if(response.isSuccessful()) {
                    Log.d("BinApp", "Response Willayat success");

                    arrayListWillayat = response.body();

                    gotResponseFromAllTables(1,0);

                }else {

                    int statusCode  = response.code();
                    Log.d("BinApp", "Response failure code Willayat " + statusCode);
                    gotResponseFromAllTables(0,1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WillayatResponse>> call, Throwable t) {
                showErrorMessage("Willayat");
                Log.d("BinApp", "error loading from API Willayat " + t.toString());
                gotResponseFromAllTables(0,1);
            }
        }); //getWillayat

        mService.getCapacity1("3",accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<CapacityResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CapacityResponse>> call, Response<ArrayList<CapacityResponse>> response) {

                if(response.isSuccessful()) {
                    Log.d("BinApp", "Response success Capacity");

                    arrayListCapacity = response.body();

                    gotResponseFromAllTables(1,0);

                }else {
                    int statusCode  = response.code();
                    Log.d("BinApp", "Response failure code Capacity " + statusCode);
                    gotResponseFromAllTables(0,1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CapacityResponse>> call, Throwable t) {
                showErrorMessage("Capacity");
                Log.d("BinApp", "error loading from API Capacity " + t.toString());
                gotResponseFromAllTables(0,1);

            }
        }); //getCapacity ends

        mService.getManufacturer1("4",accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<ManufacturerResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ManufacturerResponse>> call, Response<ArrayList<ManufacturerResponse>> response) {

                if(response.isSuccessful()) {
                    Log.d("BinApp", "Response success Manufacturer");


                    arrayListManufacturer = response.body();

                    gotResponseFromAllTables(1,0);

                }else {

                    int statusCode  = response.code();
                    Log.d("BinApp", "Response failure code  Manufacturer" + statusCode);
                    gotResponseFromAllTables(0,1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ManufacturerResponse>> call, Throwable t) {
                showErrorMessage("Maufacturer");
                Log.d("BinApp", "error loading from API Manufacturer" + t.toString());
                gotResponseFromAllTables(0,1);


            }
        }); //getManufacturer

    } // getServerDetails

    public void addGovernorateToDb(){

        for(int i = 0; i < arrayListGovernorate.size(); i++){

            governorateObj = new Governorate();
            governorateObj.setStatus(arrayListGovernorate.get(i).getStatus());
            governorateObj.setGovernorate_name(arrayListGovernorate.get(i).getGovernorateName());
            governorateObj.setGovernorate_value(arrayListGovernorate.get(i).getGovernorateValue());
            governorateObj.setGovernorate_id(arrayListGovernorate.get(i).getGovernorateId());

            // insert only after , success from all url
            governorateDao.insert(governorateObj);
        }
    }

    public void  addWillayatToDb(){

        for(int i = 0; i < arrayListWillayat.size(); i++){

            Log.d("BinApp", "Response Willayat data " +arrayListWillayat.get(i).getWillayatName());

            willayatObj = new Willayat();
            willayatObj.setWillayat_name(arrayListWillayat.get(i).getWillayatName());
            willayatObj.setWillayat_value(arrayListWillayat.get(i).getWillayatValue());
            willayatObj.setWillayat_id(arrayListWillayat.get(i).getWillayatId());
            willayatObj.setGovernorate_id(arrayListWillayat.get(i).getGovernorateId());
            willayatDao.insert(willayatObj);

        }
    }

    public void addCapacityToDb(){
        for(int i = 0; i < arrayListCapacity.size(); i++){

            capacityObj = new Capacity();
            capacityObj.setStatus(arrayListCapacity.get(i).getStatus());
            capacityObj.setCapacity_id(arrayListCapacity.get(i).getCapacityId());
            capacityObj.setCapacity_name(arrayListCapacity.get(i).getCapacityName());
            capacityObj.setCapacity_value(arrayListCapacity.get(i).getCapacityValue());
            capacityDao.insert(capacityObj);

        }
    }

    public void addManufacturerToDb(){

        for(int i = 0; i < arrayListManufacturer.size(); i++){

            manufacturerObj = new Manufacturer();
            manufacturerObj.setStatus(arrayListManufacturer.get(i).getStatus());
            manufacturerObj.setManufacturer_id(arrayListManufacturer.get(i).getManufacturerId());
            manufacturerObj.setManufacturer_name(arrayListManufacturer.get(i).getManufacturerName());
            manufacturerDao.insert(manufacturerObj);
        }
    }

    public void gotResponseFromAllTables(int closeDialogCountSuccesPass, int closeDialogCountFailurePass){

        if(closeDialogCountSuccesPass == 1){

            closeDialogCountSucces = closeDialogCountSucces + 1;

        }if(closeDialogCountFailurePass == 1){

            closeDialogCountFailure = closeDialogCountFailure + 1;
        }


        if((closeDialogCountSucces + closeDialogCountFailure) == 4){

            if(closeDialogCountSucces == 4){
                loading.dismiss();

                 // first clear db
                clearDatabase();

                // then call 4 methods of entering arraylist to db
                addGovernorateToDb();
                addWillayatToDb();
                addCapacityToDb();
                addManufacturerToDb();

                String trueStatus = "true";
                accountCurrentBinDetails.setIsDatabaseSetForFirstTime(trueStatus);

                Toast.makeText(this, " Database updated successfully " , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }else{
                Toast.makeText(this, " Database not updated " , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }
        }
    }

    public void showErrorMessage(String errorApiNAme) {
        Toast.makeText(this, errorApiNAme + " Data not updated " , Toast.LENGTH_SHORT).show();
    }

    public  void clearDatabase(){
        daoSession.getGovernorateDao().deleteAll();
        daoSession.getWillayatDao().deleteAll();
        daoSession.getCapacityDao().deleteAll();
        daoSession.getManufacturerDao().deleteAll();
    }

}
