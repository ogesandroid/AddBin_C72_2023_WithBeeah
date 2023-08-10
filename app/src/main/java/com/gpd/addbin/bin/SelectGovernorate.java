package com.gpd.addbin.bin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cocosw.favor.FavorAdapter;

import com.google.android.material.snackbar.Snackbar;
import com.gpd.addbin.R;
import com.gpd.addbin.bin.adapter.GovernorateAdapter;
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

import org.greenrobot.greendao.query.Query;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectGovernorate extends AppCompatActivity {

    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_LOGOUT")) {
                finish();
            }
        }
    }


    ProgressDialog loading;

    int closeDialogCountSucces = 0, closeDialogCountFailure = 0;


    private LogoutReceiver logoutReceiver;
    private SOService mService;
    Spinner governorateSpinner;
    GovernorateAdapter adapter;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    int languagespinnerPos;

    RelativeLayout rl_enter_SelectGovernoratel;

    ImageView imageServer;


    private Query<Governorate> governorateQuery;
    private GovernorateDao governorateDao;
    private WillayatDao willayatDao;
    private CapacityDao capacityDao;
    private ManufacturerDao manufacturerDao;

    private Query<Willayat> willayatQuery;
    private Query<Capacity> capacityQuery;
    private Query<Manufacturer> manufacturerQuery;
    RelativeLayout rlNext, rlSelectGovernorate;

    Account accountCurrentBinDetails;
    //    private static LocationProvider locationProvider;
//    private static LocationTrack locationTrack;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 100;
    TextView tvSelectedGovernorate;
    private LocationManager locationManager;

    private ArrayList<GovernorateResponse> arrayListGovernorate;
    private ArrayList<WillayatResponse> arrayListWillayat;
    private ArrayList<CapacityResponse> arrayListCapacity;
    private ArrayList<ManufacturerResponse> arrayListManufacturer;
    DaoSession daoSession;
    AlertDialog deleteDialog;
    ImageView img_icon;
    private static final String TAG = "AddBin";

    ImageView iv_exit;

    @Override
    protected void onDestroy() {

        // Unregister the logout receiver
        unregisterReceiver(logoutReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_governorate);

        mService = ApiUtils.getSOService();
        Log.e("mService", "= " + mService);
        accountCurrentBinDetails = new FavorAdapter.Builder(SelectGovernorate.this).build().create(Account.class);

        // Remove current saved values  bcoz after exiting or going home then no need of the values
        // again start from scratch
        if (accountCurrentBinDetails.getGovernarateStatus().equals("1")) {
            accountCurrentBinDetails.setGovernorate("No "+accountCurrentBinDetails.getGovernarateTitle());
        } else {
            accountCurrentBinDetails.setGovernorate("No Governorate");
        }

        accountCurrentBinDetails.setBeahCode("");
        accountCurrentBinDetails.setCurrentScanTypeInPreview("");

        // get the governorate DAO
        daoSession = ((AppController) getApplicationContext()).getDaoSession();

        governorateDao = daoSession.getGovernorateDao();
        willayatDao = daoSession.getWillayatDao();
        capacityDao = daoSession.getCapacityDao();
        manufacturerDao = daoSession.getManufacturerDao();

        governorateQuery = governorateDao.queryBuilder().build();
        willayatQuery = willayatDao.queryBuilder().build();
        capacityQuery = capacityDao.queryBuilder().build();
        manufacturerQuery = manufacturerDao.queryBuilder().build();

        checkIfLocalDataBaseEmpty();

        checkLocation();

        logoutReceiver = new LogoutReceiver();
        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(logoutReceiver, intentFilter);

        governorateSpinner = (Spinner) findViewById(R.id.sp_SelectGovernorate);

        tvSelectedGovernorate = (TextView) findViewById(R.id.tv_selected_governorate_id_SelectGovernorate);
        img_icon = (ImageView) findViewById(R.id.img_logo);
        iv_exit = (ImageView) findViewById(R.id.iv_exit);
        if(accountCurrentBinDetails.getGovernarateStatus().equals("1")){
            tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernarateTitle());
        }else{
            tvSelectedGovernorate.setText("Select Governorate");
        }

        rlSelectGovernorate = (RelativeLayout) findViewById(R.id.container_show_value_SelectGovernorate);
        rlSelectGovernorate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getGovernorateFromDb();

            }
        });
        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            Log.e(TAG, "onCreate: @@@@@ ");
            img_icon.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID() + "_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
//            Picasso.with(this).load("http://gpduae.com/Addbin/data_folder/company/logo/"+accountCurrentBinDetails.getCompanyID()+"/"+accountCurrentBinDetails.getCompanyLogo()).into(img_icon);
            Picasso.with(this).load(ApiUtils.ICON_URL + accountCurrentBinDetails.getCompanyID() + "/" + accountCurrentBinDetails.getCompanyLogo()).into(img_icon);
//            Picasso.with(this).load("http://ogesinfotech.com/Add_bin/data_folder/company/logo/"+accountCurrentBinDetails.getCompanyID()+"/"+accountCurrentBinDetails.getCompanyLogo()).into(img_icon);
//            Picasso.with(this).load("http://192.168.10.111:8081/Anjitha/Gpd_technology/data_folder/company/logo/"+accountCurrentBinDetails.getCompanyID()+"/"+accountCurrentBinDetails.getCompanyLogo()).into(img_icon);
//            Picasso.with(this).load(file).into(img_icon);
        }
        rlNext = (RelativeLayout) findViewById(R.id.rl_enter_SelectGovernorate);
        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (accountCurrentBinDetails.getGovernarateStatus().equals("1")) {
                    if (tvSelectedGovernorate.getText().toString().equals(accountCurrentBinDetails.getGovernarateTitle()) || accountCurrentBinDetails.getGovernorate().equals("No City")) {
                        Toast.makeText(SelectGovernorate.this, "Please enter "+accountCurrentBinDetails.getGovernarateTitle(), Toast.LENGTH_LONG).show();

                        final MediaPlayer mp = MediaPlayer.create(SelectGovernorate.this, R.raw.beep);
                        mp.start();

                    } else {
                        Intent nextIntet = new Intent(SelectGovernorate.this, SelectWillayat.class);
                        startActivityForResult(nextIntet, 112);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                } else {
                    if (tvSelectedGovernorate.getText().toString().equals("Select Governorate") || accountCurrentBinDetails.getGovernorate().equals("No Governorate")) {
                        Toast.makeText(SelectGovernorate.this, "Please enter Governorate", Toast.LENGTH_LONG).show();

                        final MediaPlayer mp = MediaPlayer.create(SelectGovernorate.this, R.raw.beep);
                        mp.start();

                    } else {
                        Intent nextIntet = new Intent(SelectGovernorate.this, SelectWillayat.class);
                        startActivityForResult(nextIntet, 112);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }

                }
            }
        });


        imageServer = (ImageView) findViewById(R.id.iv_save_to_local_db_SelectGovernorate);

        governorateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {

                Governorate governorateObj = (Governorate) (parent.getItemAtPosition(pos));
                accountCurrentBinDetails.setGovernorate(governorateObj.getGovernorate_name());

                accountCurrentBinDetails.setGovernorateId(governorateObj.getGovernorate_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        imageServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUpdateData = new Intent(SelectGovernorate.this, SaveServerDetails.class);
                startActivity(intentUpdateData);
            }
        });

        iv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

//        locationProvider=new FusedLocationProvider(this);

//        getCurrentLocation();

    } // onCreate


//    public void getCurrentLocation() {
//
//        Log.d("BinApp", "inside getCurrentLocation");
//
//        locationProvider.setCurrentLocationUpdate(true);
//        locationProvider.setTimeOut(1000 * 10);
//        checkForPermission();
//
//    }

//    private void requestLocationUpdates(LocationProvider locationProvider){
//
//        Log.d("BinApp", "inside requestLocationUpdates");
//        locationTrack = new LocationTrack.Builder(this).withProvider(locationProvider).build().getLocationUpdates(this);
//
//    }

//    private void checkForPermission() {
//
//        Log.d("BinApp", "inside checkForPermission");
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                Log.d("BinApp", "requestLocationUpdates: " + "Show an expanation to the user");
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_READ_LOCATION);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//
//            Log.d("BinApp", "inside else of Marshmllow");
//
//            requestLocationUpdates(locationProvider);
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Log.d("BinApp", "onRequestPermissionsResult: " + "permission granted");
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    requestLocationUpdates(locationProvider);
//
//                } else {
//
//                    Log.d("BinApp", "onRequestPermissionsResult: " + "permission denied");
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }


//    @Override
//    public void onLocationUpdate(Location location) {
//
//        Log.d("BinApp", "updated location: " + location.getLongitude() + " " + location.getLatitude());
//
//
//        accountCurrentBinDetails.setBinLatitude(String.valueOf(location.getLatitude()));
//        accountCurrentBinDetails.setBinLongitude(String.valueOf(location.getLongitude()));
//
//       // Toast.makeText(SelectGovernorate.this,"Last location: " + location.getLongitude() + " " + location.getLatitude(),Toast.LENGTH_LONG).show();
//
//        if (locationTrack.getProvider().isSingleLocationUpdate()) {
//            locationTrack.stopLocationUpdates();
//            locationProvider.setCurrentLocationUpdate(false);
//            locationProvider.addLocationSettings(LocationSettings.DEFAULT_SETTING);
//        }
//    }
//
//
//    @Override
//    public void onTimeout() {
//        //Toast.makeText(SelectGovernorate.this, "Timed out , location request stopped", Toast.LENGTH_SHORT).show();
//        locationTrack.stopLocationUpdates();
//    }

    public void getGovernorateFromDb() {

        Intent listIntet = new Intent(SelectGovernorate.this, SelectFromList.class);
        listIntet.putExtra("page_id", "GOVERNORATE");
        listIntet.putExtra("page_title", "Select Governorate");

        startActivityForResult(listIntet, 2);
        overridePendingTransition(R.anim.enter, R.anim.exit);


    } // getGovernorateFromDb


    public void checkIfLocalDataBaseEmpty() {

        //Toast.makeText(SelectGovernorate.this, "inside check isset value is " + accountCurrentBinDetails.getIsDatabaseSetForFirstTime(),Toast.LENGTH_LONG).show();

        if ((accountCurrentBinDetails.getIsDatabaseSetForFirstTime()).equals("true")) {

        } else {

            LayoutInflater factory = LayoutInflater.from(this);
            final View deleteDialogView = factory.inflate(R.layout.popup_no_data_in_local, null);

            deleteDialog = new AlertDialog.Builder(this).create();

            deleteDialog.setCancelable(false);
            deleteDialog.setCanceledOnTouchOutside(false);

            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            deleteDialog.setView(deleteDialogView);

            deleteDialogView.findViewById(R.id.bt_go_to_update_page_Popup_Nodata).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//                    Intent myIntent = new Intent(SelectGovernorate.this,SaveServerDetails.class);
//                    startActivityForResult(myIntent, 1);

                    updateDatabaseFromHere();

                }
            });

            deleteDialogView.findViewById(R.id.iv_close_popup_no_data).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //your business logic
                    deleteDialog.dismiss();
                }
            });

            deleteDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check that it is the SecondActivity with an OK result
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                finish();
                Intent refresh = new Intent(this, SelectGovernorate.class);
                startActivity(refresh);//Start the same Activity

            }
        } else if (requestCode == 2) {

            tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());

        } else {
            if (requestCode == 112) {

                // tvSelectedGovernorate.setText("Select Governorate");

                if (accountCurrentBinDetails.getWillayathStatus().equals("1")) {
                    accountCurrentBinDetails.setWillayat("No "+accountCurrentBinDetails.getWillayathTitle());
                } else {
                    accountCurrentBinDetails.setWillayat("No Willayat");
                }

                accountCurrentBinDetails.setCapacity("No Bin Capacity");
                accountCurrentBinDetails.setManufacturer("No Manufacturer");
            }
        }


    }//onActivityResult

    private boolean checkLocation() {
        if (!isLocationEnabled()) {
            showAlert();
        } else {

        }
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Please Enable Location to use this app")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        // showSosPrompt();

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);

                        paramDialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // showSosPrompt();
                    }
                });
        dialog.show();
    }

    public void updateDatabaseFromHere() {


        //  Toast.makeText(this, " inside Data not updated updateDatabaseFromHere"  , Toast.LENGTH_SHORT).show();
        daoSession.getGovernorateDao().deleteAll();
        daoSession.getWillayatDao().deleteAll();
        daoSession.getCapacityDao().deleteAll();
        daoSession.getManufacturerDao().deleteAll();

        checkConnection();

    } // updateDatabaseFromHere

    private void checkConnection() {
        // Toast.makeText(this, " inside checkConnection"  , Toast.LENGTH_SHORT).show();
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

            //  Toast.makeText(this, " inside snack connected " , Toast.LENGTH_SHORT).show();

            // if internet is present upload , no problem

            getServerData();

            message = "Connected to Internet";
            color = Color.WHITE;

            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_root_select_governorate), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();

        } else {

            // if no net is present , saveToLocalDb , then go to first screen

            // Toast.makeText(this, " inside snack no net " , Toast.LENGTH_SHORT).show();

            message = "Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.container_root_select_governorate), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();

            Toast.makeText(this, " Not connected to internet ", Toast.LENGTH_LONG).show();
        }


    }


    public void getServerData() {


        loading = ProgressDialog.show(SelectGovernorate.this, "Updating Database", "Please wait...", false, false);
        Log.d("BinApp", "inside getServerData");


        mService.getGovernorate1("1", accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<GovernorateResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GovernorateResponse>> call, Response<ArrayList<GovernorateResponse>> response) {

                if (response.isSuccessful()) {


                    Log.d("BinApp", "Response Governorate success");

                    arrayListGovernorate = response.body();

                    for (int i = 0; i < arrayListGovernorate.size(); i++) {

                        Governorate governorateObj = new Governorate();
                        governorateObj.setStatus(arrayListGovernorate.get(i).getStatus());
                        governorateObj.setGovernorate_name(arrayListGovernorate.get(i).getGovernorateName());
                        governorateObj.setGovernorate_value(arrayListGovernorate.get(i).getGovernorateValue());
                        governorateObj.setGovernorate_id(arrayListGovernorate.get(i).getGovernorateId());

                        governorateDao.insert(governorateObj);
                    }

                    gotResponseFromAllTables(1, 0);


                } else {

                    gotResponseFromAllTables(0, 1);

                    loading.dismiss();
                    int statusCode = response.code();

                    String errorDetails = response.message();

                    Log.d("BinApp", "Response Governorate failure code " + statusCode + "error is " + errorDetails);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GovernorateResponse>> call, Throwable t) {
                if (accountCurrentBinDetails.getGovernarateStatus().equals("1")) {
                    showErrorMessage(accountCurrentBinDetails.getGovernarateTitle());
                } else {
                    showErrorMessage("Governorate");
                }
//                showErrorMessage("Governorate");
                loading.dismiss();
                Log.d("BinApp", "error loading from API Governorate " + t.toString());
                gotResponseFromAllTables(0, 1);

            }
        }); // getGovernorateDetails

        mService.getWillayat1("2", accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<WillayatResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<WillayatResponse>> call, Response<ArrayList<WillayatResponse>> response) {

                if (response.isSuccessful()) {
                    // mAdapter.updateAnswers(response.body().getResult());
                    Log.d("BinApp", "Response Willayat success");

                    arrayListWillayat = response.body();

                    for (int i = 0; i < arrayListWillayat.size(); i++) {

                        Log.d("BinApp", "Response Willayat data " + arrayListWillayat.get(i).getWillayatName());

                        Willayat willayatObj = new Willayat();
                        willayatObj.setWillayat_name(arrayListWillayat.get(i).getWillayatName());
                        willayatObj.setWillayat_value(arrayListWillayat.get(i).getWillayatValue());
                        willayatObj.setWillayat_id(arrayListWillayat.get(i).getWillayatId());
                        willayatObj.setGovernorate_id(arrayListWillayat.get(i).getGovernorateId());

                        willayatDao.insert(willayatObj);
                    }

                    gotResponseFromAllTables(1, 0);

                } else {

                    int statusCode = response.code();
                    Log.d("BinApp", "Response failure code Willayat " + statusCode);
                    gotResponseFromAllTables(0, 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WillayatResponse>> call, Throwable t) {
                showErrorMessage("Willayat");
                Log.d("BinApp", "error loading from API Willayat " + t.toString());
                gotResponseFromAllTables(0, 1);
            }
        }); //getWillayat

        mService.getCapacity1("3", accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<CapacityResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CapacityResponse>> call, Response<ArrayList<CapacityResponse>> response) {

                if (response.isSuccessful()) {
                    // mAdapter.updateAnswers(response.body().getResult());
                    Log.d("BinApp", "Response success Capacity");

                    arrayListCapacity = response.body();

                    for (int i = 0; i < arrayListCapacity.size(); i++) {

                        Capacity capacityObj = new Capacity();
                        capacityObj.setStatus(arrayListCapacity.get(i).getStatus());
                        capacityObj.setCapacity_id(arrayListCapacity.get(i).getCapacityId());
                        capacityObj.setCapacity_name(arrayListCapacity.get(i).getCapacityName());
                        capacityObj.setCapacity_value(arrayListCapacity.get(i).getCapacityValue());

                        capacityDao.insert(capacityObj);
                    }

                    gotResponseFromAllTables(1, 0);

                } else {
                    int statusCode = response.code();
                    Log.d("BinApp", "Response failure code Capacity " + statusCode);
                    gotResponseFromAllTables(0, 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CapacityResponse>> call, Throwable t) {
                showErrorMessage("Capacity");
                Log.d("BinApp", "error loading from API Capacity " + t.toString());
                gotResponseFromAllTables(0, 1);

            }
        }); //getCapacity ends

        mService.getManufacturer1("4", accountCurrentBinDetails.getCompanyID()).enqueue(new Callback<ArrayList<ManufacturerResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ManufacturerResponse>> call, Response<ArrayList<ManufacturerResponse>> response) {

                if (response.isSuccessful()) {
                    Log.d("BinApp", "Response success Manufacturer");


                    arrayListManufacturer = response.body();

                    for (int i = 0; i < arrayListManufacturer.size(); i++) {

                        Manufacturer manufacturerObj = new Manufacturer();
                        manufacturerObj.setStatus(arrayListManufacturer.get(i).getStatus());
                        manufacturerObj.setManufacturer_id(arrayListManufacturer.get(i).getManufacturerId());
                        manufacturerObj.setManufacturer_name(arrayListManufacturer.get(i).getManufacturerName());

                        manufacturerDao.insert(manufacturerObj);
                    }

                    gotResponseFromAllTables(1, 0);

                } else {

                    int statusCode = response.code();
                    Log.d("BinApp", "Response failure code  Manufacturer" + statusCode);
                    gotResponseFromAllTables(0, 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ManufacturerResponse>> call, Throwable t) {
                showErrorMessage("Maufacturer");
                Log.d("BinApp", "error loading from API Manufacturer" + t.toString());
                gotResponseFromAllTables(0, 1);


            }
        }); //getManufacturer


    } // getServerDetails


    public void gotResponseFromAllTables(int closeDialogCountSuccesPass, int closeDialogCountFailurePass) {

        if (closeDialogCountSuccesPass == 1) {

            closeDialogCountSucces = closeDialogCountSucces + 1;

        }
        if (closeDialogCountFailurePass == 1) {

            closeDialogCountFailure = closeDialogCountFailure + 1;
        }

        // Toast.makeText(this, " current count success " + closeDialogCountSucces + " closeDialogCountFailure " +  closeDialogCountFailure, Toast.LENGTH_SHORT).show();

        if ((closeDialogCountSucces + closeDialogCountFailure) == 4) {

            if (closeDialogCountSucces == 4) {
                loading.dismiss();
                Toast.makeText(this, " Database updated successfully ", Toast.LENGTH_SHORT).show();

                String trueStatus = "true";
                accountCurrentBinDetails.setIsDatabaseSetForFirstTime(trueStatus);

                deleteDialog.dismiss();
            } else {
                Toast.makeText(this, " Database not updated ", Toast.LENGTH_SHORT).show();
                deleteDialog.dismiss();
            }
        }
    }


    public void showErrorMessage(String errorApiNAme) {
        Toast.makeText(this, errorApiNAme + " Data not updated ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        if (id == R.id.action_update) {
            Intent i = new Intent(SelectGovernorate.this, SaveServerDetails.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        Log.d(TAG, "logout: ");

        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        accountCurrentBinDetails.setCompanyID("");
                        accountCurrentBinDetails.setIsDatabaseSetForFirstTime("false");
                        Intent intent = new Intent(SelectGovernorate.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
