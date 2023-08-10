package com.gpd.addbin.bin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cocosw.favor.FavorAdapter;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gpd.addbin.R;
import com.gpd.addbin.activity.UHFMainActivity;
import com.gpd.addbin.bin.data.remote.ApiUtils;
import com.gpd.addbin.bin.data.remote.SOService;
import com.gpd.addbin.bin.db.BinDetails;
import com.gpd.addbin.bin.db.BinDetailsDao;
import com.gpd.addbin.bin.db.DaoSession;
import com.gpd.addbin.bin.db.DataBaseHelper;
import com.gpd.addbin.bin.info.Account;
import com.gpd.addbin.bin.info.AppController;
import com.gpd.addbin.bin.info.ConnectivityReceiver;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewBinDetails extends AppCompatActivity {

    Boolean uploadSuccess = false;
    Location locationForComparing;
    private GoogleApiClient googleApiClient;
    Account accountCurrentBinDetails;
    TextView tvSelectedGovernorate, tvSelectedWillayat, tvSelectedCapacity, tvSelectedManufacturer, tvBeahCode, tvRfidCode, tvRfidCodeType, tvCurrentLatitudeLongitude;
    RelativeLayout rlUploadToServer;
    private SOService mService;
    // private String UPLOAD_URL = "http://www.gpduae.com/Addbin/get_oges_api.php?p=5";
    private String UPLOAD_URL = "";
    //    private String UPLOAD_URL = "http://ogesinfotech.com/Add_bin/get_oges_api.php?p=5";
//    private String UPLOAD_URL = "http://192.168.10.111:8081/Anjitha/Gpd_technology/get_oges_api.php?p=5";
    View layoutError, layoutSuccesUpload, layoutSavetoDevice;
    String statusCreation, joinedBeahCode;

    ProgressDialog loading, loadingCheckingForLocation;
    private Query<BinDetails> binDetailsQuery;
    private BinDetailsDao binDetailsDao;
    DataBaseHelper myDb;

    //    private static LocationTrack locationTrack;
//    private static LocationProvider locationProvider;
    Button btUploadToServer;
    private LocationManager locationManager;
    LinearLayout linearExit, linearChangeBinCapacity, linearChangeFourDigitBeah, linearOverlayDisable;
    ImageView iv_logo; //Added 19.09.2020


    //..........GPS Location settings start...............................
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    // fastest updates interval - 3 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    // bunch of location related apis
//    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private double latitude = 0.0, longitude = 0.0;
    private String access_token;
    FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog progressDoalogLoc;

    //..........GPS Location settings end...............................
    TextView tx_willayath, tx_governarate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_bin_details);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //.................Starts here........................
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initLocationSettings();
        startLocationServices();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2 * 1000);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        System.out.println("@@@@LATon create" + latitude);
//                        txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
//                        Toast.makeText(Read_RFIDActivity.this, " "+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        //.......................................................


        UPLOAD_URL = ApiUtils.BASE_URL + "get_oges_api.php?p=5"; //newly added 22.09.2020


        accountCurrentBinDetails = new FavorAdapter.Builder(PreviewBinDetails.this).build().create(Account.class);
//        Toast.makeText(this, accountCurrentBinDetails.getBeahCode(),Toast.LENGTH_SHORT).show();

        joinedBeahCode = accountCurrentBinDetails.getGovernorateValue() + "-" + accountCurrentBinDetails.getWillayatValue() + "-"
                + accountCurrentBinDetails.getCapacityValue() + "-" + accountCurrentBinDetails.getBeahCode();


//        locationProvider = new FusedLocationProvider(this);

//        checkLocation();
        updateLocationUI();

        mService = ApiUtils.getSOService();

        myDb = new DataBaseHelper(this);

        DaoSession daoSession = ((AppController) getApplication()).getDaoSession();
        binDetailsDao = daoSession.getBinDetailsDao();

        binDetailsQuery = binDetailsDao.queryBuilder().build();

        tvSelectedGovernorate = (TextView) findViewById(R.id.tv_governorate_value_PreviewBinDetails);
        tvSelectedWillayat = (TextView) findViewById(R.id.tv_willayat_value_PreviewBinDetails);
        tvSelectedCapacity = (TextView) findViewById(R.id.tv_bin_capacity_value_PreviewBinDetails);
        tvSelectedManufacturer = (TextView) findViewById(R.id.tv_manfacturer_value_PreviewBinDetails);
        tvBeahCode = (TextView) findViewById(R.id.tv_beah_value_PreviewBinDetails);
        tvRfidCodeType = (TextView) findViewById(R.id.tv_current_code_type_preview_details);
        tvRfidCode = (TextView) findViewById(R.id.tv_rfid_value_PreviewBinDetails);
        tvCurrentLatitudeLongitude = (TextView) findViewById(R.id.tv_current_latittude_longitude_value_PreviewBinDetails);

        linearExit = (LinearLayout) findViewById(R.id.container_exit_PreviewBinDetails);

        linearExit.setEnabled(false);

        linearChangeBinCapacity = (LinearLayout) findViewById(R.id.container_change_capacity_PreviewBinDetails);
        linearChangeFourDigitBeah = (LinearLayout) findViewById(R.id.container_change_beah_PreviewBinDetails);

        linearOverlayDisable = (LinearLayout) findViewById(R.id.container_exit_overlay_disabled_PreviewBinDetails);

        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
        tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());
        tvSelectedManufacturer.setText(accountCurrentBinDetails.getManufacturer());
        tvBeahCode.setText(joinedBeahCode);
        tvRfidCodeType.setText(accountCurrentBinDetails.getCurrentTypeInPreview());
        tvRfidCode.setText(accountCurrentBinDetails.getCurrentTypeInPreviewValue());

        btUploadToServer = (Button) findViewById(R.id.bt_upload_PreviewBinDetails);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);  //Added 19.09.2020
        tx_governarate = (TextView) findViewById(R.id.tx_governarate);  //Added 25.5.21
        tx_willayath = (TextView) findViewById(R.id.tx_willayath);  //Added 25.5.21
        if (accountCurrentBinDetails.getGovernarateStatus().equals("1")) {
            tx_governarate.setText("Your "+accountCurrentBinDetails.getGovernarateTitle()+" is ");
        } else {
            tx_governarate.setText("Your Governorate is ");
        }
        if (accountCurrentBinDetails.getWillayathStatus().equals("1")) {
            tx_willayath.setText("Your "+accountCurrentBinDetails.getWillayathTitle()+" is ");
        } else {
            tx_willayath.setText("Your Willayat is ");
        }
        btUploadToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationTest();

                Log.e("AddBin", "inside uploadToServer click");

            }
        });


        LayoutInflater liSuccessUpload = getLayoutInflater();
        layoutSuccesUpload = liSuccessUpload.inflate(R.layout.layout_succes_upload_broadcast_receiver, (ViewGroup) findViewById(R.id.custom_toast_layout_uploaded_to_server_broadcast));

        LayoutInflater liError = getLayoutInflater();
        layoutError = liError.inflate(R.layout.custom_toast_error, (ViewGroup) findViewById(R.id.custom_toast_layout_error));

        LayoutInflater liSavetoDevice = getLayoutInflater();
        layoutSavetoDevice = liSavetoDevice.inflate(R.layout.layout_save_bin_details_to_device, (ViewGroup) findViewById(R.id.saved_data_to_device));

        linearExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToFirstScreen();
            }
        });
        linearChangeBinCapacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backToCapacityScreen();
            }
        });

        linearChangeFourDigitBeah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            iv_logo.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID() + "_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
            Picasso.with(this).load(file).into(iv_logo);
        }

    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

            // if internet is present upload , no problem

            loading = ProgressDialog.show(PreviewBinDetails.this, "Uploading to server", "Please wait...", false, false);

            testUpload();

            message = "Connected to Internet";
            color = Color.WHITE;


        } else {

            // if no net is present , saveToLocalDb , then go to first screen

            loading = ProgressDialog.show(PreviewBinDetails.this, "No Internet,  Saving to device", "Please wait...", false, false);

            // now only after checking if code is present in db

            checkIfScanCodeExistsInDb();

            message = "Not connected to internet";
            color = Color.RED;
        }


        // Snackbar not necessary because, if net then on button click "uploading" is shown
        // if no net then "saving is shown
        // when page is openend and checking net then showing snackbar wiill interfere with location snackbar
        // when page is openend and checking net then showing snackbar wiill interfere with location snackbar

//        Snackbar snackbar = Snackbar.make(findViewById(R.id.container_root_PreviewBinDetails), message, Snackbar.LENGTH_LONG);
//
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(color);
//        snackbar.show();

    }

    // testUpload
    public void testUpload() {

        Log.e("AddBin", "inside testUpload");

        JSONObject jsonObject = new JSONObject();

        JSONArray array = new JSONArray();

        try {
            JSONObject json = new JSONObject();

            json.put("governorate_id", accountCurrentBinDetails.getGovernorateId());
            json.put("willayat_id", accountCurrentBinDetails.getWillayatId());
            json.put("capacity_id", accountCurrentBinDetails.getCapacityId());
            json.put("manufacturer_id", accountCurrentBinDetails.getManufacturerId());


            if (accountCurrentBinDetails.getCurrentScanTypeInPreview().equals("rfid")) {
                json.put("scan_type", "1");
            } else if (accountCurrentBinDetails.getCurrentScanTypeInPreview().equals("barcode")) {
                json.put("scan_type", "2");
            }
            json.put("bin_rfid", accountCurrentBinDetails.getCurrentTypeInPreviewValue());
            //json.put("bin_location", "null_location");
            json.put("bin_lat", accountCurrentBinDetails.getBinLatitude());
            json.put("bin_longi", accountCurrentBinDetails.getBinLongitude());
            json.put("manual_entry", accountCurrentBinDetails.getBeahCode());
            json.put("bin_status", "1");
            json.put("bin_time", accountCurrentBinDetails.getBinTime());
            json.put("company_id", accountCurrentBinDetails.getCompanyID());

            array.put(json);

        } catch (JSONException e) {

            Log.e("AddBin", "error in json list making  ");
        }


        try {
            jsonObject.put("bin_details", array);
            Log.e("AddBin", "json object to pass   " + jsonObject.toString());

        } catch (JSONException e) {

            Log.e("AddBin", "error in array making ");
        }


        JsonObjectRequest myRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    statusCreation = response.getString("status");

                    Log.e("AddBin", "real value of response" + response);

                    if (statusCreation.equals("true")) {

                        loading.dismiss();
                        // Toast.makeText(Registration.this, statusCreation, Toast.LENGTH_SHORT).show();
                        Toast successUpload = new Toast(getApplicationContext());
                        successUpload.setDuration(Toast.LENGTH_SHORT);
                        successUpload.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        successUpload.setView(layoutSuccesUpload);//setting the view of custom toast layout
                        successUpload.show();


                        uploadSuccess = true;

                        linearOverlayDisable.setVisibility(View.GONE);
                        linearExit.setEnabled(true);

                        if (uploadSuccess) {
                            Log.e("AddBin", "inside testupload onUserInteraction uploadSuccess true");

                            // clearig lat and long to get fresh values everytime on reaching Preview page

                            accountCurrentBinDetails.setBinLatitude("0.0");
                            accountCurrentBinDetails.setBinLongitude("0.0");

                            delayedIdle(100);
                        }

                        // commented below 2 lines because to make next entry easier, if the person is in same governorate and willayat

                    } else if (statusCreation.equals("false")) {
                        Log.e("AddBin", "status other than true ");

                        // storeCurrentValuesToDb(); // earlier directly saving to localDb
                        // now only after checking if code is present in db

                        checkIfScanCodeExistsInDb();

                        // commented below line because to make next entry easier, if the person is in same governorate and willayat

                    } else if (statusCreation.equals("exist")) {
                        Toast.makeText(PreviewBinDetails.this, "Add Bin - RFID or Beah Code already exist ", Toast.LENGTH_LONG).show();
                    }

                    Log.e("AddBin", "already 10 seconds gone so close progress bar ");
                    loading.dismiss();

                } catch (Exception e) {

                    loading.dismiss();

                    // now only after checking if code is present in db

                    checkIfScanCodeExistsInDb();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // now only after checking if code is present in db

                        checkIfScanCodeExistsInDb();

                        loading.dismiss();

                        Log.e("AddBin", "object creation error is " + error);
                        statusCreation = "try_later";
                        Toast toastFailureTwo = new Toast(getApplicationContext());
                        toastFailureTwo.setDuration(Toast.LENGTH_SHORT);
                        toastFailureTwo.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toastFailureTwo.setView(layoutError);//setting the view of custom toast layout
                        toastFailureTwo.show();

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

        int socketTimeout = 10000; // 10 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        myRequest.setRetryPolicy(policy);

        AppController.getInstance().addToRequestQueue(myRequest, "tag_home_user");


    }
    // test upload ends

    public void backToFirstScreen() {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);

        finish();
    }

    public void backToCapacityScreen() {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("clicked.Change.Bin.Capacity");
        sendBroadcast(broadcastIntent);

        finish();
    }


    public void checkIfScanCodeExistsInDb() {
        boolean isScanCodeAlreadyInDb = myDb.getAllScanCodeData(accountCurrentBinDetails.getCurrentTypeInPreviewValue());
        if (isScanCodeAlreadyInDb) {
            Log.e("AddBin", " scancode already present");
            loading.dismiss();

            uploadSuccess = true;

            linearOverlayDisable.setVisibility(View.GONE);
            linearExit.setEnabled(true);

            Toast.makeText(this, "Details already saved in device", Toast.LENGTH_SHORT).show();

            if (uploadSuccess) {
                Log.e("AddBin", "inside Details already saved in device ");

                // clearig lat and long to get fresh values everytime on reaching Preview page

                accountCurrentBinDetails.setBinLatitude("0.0");
                accountCurrentBinDetails.setBinLongitude("0.0");
                delayedIdle(100);
            }


        } else {
            Log.e("AddBin", " new scancode ");

            storeCurrentValuesToDb();
        }
    }

    public void storeCurrentValuesToDb() {
        // Since status is not 1 in the first place details are saved to db of device
        BinDetails binDetailsObj = new BinDetails();
        binDetailsObj.setGovernorate_id(accountCurrentBinDetails.getGovernorateId());
        binDetailsObj.setWillayat_id(accountCurrentBinDetails.getWillayatId());
        binDetailsObj.setCapacity_id(accountCurrentBinDetails.getCapacityId());
        binDetailsObj.setManufacturer_id(accountCurrentBinDetails.getManufacturerId());

        // new additional column , to check whether the send code is barcode or rfid
        // 1 for rfid 2 for barcode


        if (accountCurrentBinDetails.getCurrentScanTypeInPreview().equals("rfid")) {
            binDetailsObj.setScan_type("1");
        } else if (accountCurrentBinDetails.getCurrentScanTypeInPreview().equals("barcode")) {
            binDetailsObj.setScan_type("2");
        }


        binDetailsObj.setBin_rfid(accountCurrentBinDetails.getCurrentTypeInPreviewValue());
        binDetailsObj.setBin_lat(accountCurrentBinDetails.getBinLatitude());
        binDetailsObj.setBin_longi(accountCurrentBinDetails.getBinLongitude());
        binDetailsObj.setManual_entry(accountCurrentBinDetails.getBeahCode());
        binDetailsObj.setBin_status(accountCurrentBinDetails.getDataBinStatus());
        binDetailsObj.setBin_time(accountCurrentBinDetails.getBinTime());
        binDetailsObj.setCompany_id(accountCurrentBinDetails.getCompanyID());
        binDetailsDao.insert(binDetailsObj);

        uploadSuccess = true;

        linearOverlayDisable.setVisibility(View.GONE);
        linearExit.setEnabled(true);


        loading.dismiss();

        Toast successUpload = new Toast(getApplicationContext());
        successUpload.setDuration(Toast.LENGTH_SHORT);
        successUpload.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        successUpload.setView(layoutSavetoDevice);//setting the view of custom toast layout
        successUpload.show();

        if (uploadSuccess) {
            Log.e("AddBin", "inside testupload onUserInteraction uploadSuccess true");

            // clearig lat and long to get fresh values everytime on reaching Preview page

            accountCurrentBinDetails.setBinLatitude("0.0");
            accountCurrentBinDetails.setBinLongitude("0.0");

            // earlier there was a 5 sec delay if user dont do anyhting after upload success
            // now client asked to  go to "Enter 5 digit of BEAH code" page.
            // now a nominal delay,
            delayedIdle(100);
        }


        // display current saved data from greendao db , just to make sure entered to db
        List<BinDetails> binDetailsName = binDetailsQuery.list();

        for (int i = 0; i < binDetailsName.size(); i++) {
            Log.e("AddBin", "Current values in localDb " + binDetailsName.get(i).getId() + " " + binDetailsName.get(i).getGovernorate_id() + " beah code" + binDetailsName.get(i).getManual_entry()
                    + " scan type" + binDetailsName.get(i).getScan_type() + " scan value" + binDetailsName.get(i).getBin_rfid() + " current time" + binDetailsName.get(i).getBin_time());
        }

        // get the curren data directly from sqlite , so we can use this in broadcast receiver ,
        //  because greendao session in receiver not working
        getAllBinData();

    }

    public void getAllBinData() {

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            Log.e("AddBin", "Nothing found");
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
        }

        // Show all data
        Log.e("AddBin", buffer.toString());
    }


    public void showErrorMessage() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    public void clearCurrentBinDetails() {

        accountCurrentBinDetails.setGovernorate("");
        accountCurrentBinDetails.setWillayat("");
        accountCurrentBinDetails.setCapacity("");
        accountCurrentBinDetails.setManufacturer("");
        accountCurrentBinDetails.setRfid("");
        accountCurrentBinDetails.setBeahCode("");
        accountCurrentBinDetails.setBinTime("");
        accountCurrentBinDetails.setDataBinStatus("");
        accountCurrentBinDetails.setBinLatitude("0.0");
        accountCurrentBinDetails.setBinLongitude("0.0");


        Log.e("AddBin", "Current bin details cleared");
    }

    public void locationTest() {

        Log.e("AddBin", "inside locationTest");

        if (accountCurrentBinDetails.getBinLatitude().equals("0.0") || accountCurrentBinDetails.getBinLatitude().equals("0")) {
            // Toast.makeText(PreviewBinDetails.this,"Not available latitude is " + accountCurrentBinDetails.getBinLatitude()  , Toast.LENGTH_LONG).show();

            Log.e("AddBin", "accountCurrentBinDetails.getBinLatitude().equals(\"0.0\") ");
//            checkLocationUpload();
            updateLocationUI();
        } else {
            Log.e("AddBin", "accountCurrentBinDetails.getBinLatitude() not zero value is  ");
            // Toast.makeText(PreviewBinDetails.this,"Available latitude is " + accountCurrentBinDetails.getBinLatitude()  , Toast.LENGTH_LONG).show();
            checkConnection();
        }

    }

//    public void showNoLocationSnack(){
//        Snackbar snackbar = Snackbar
//                .make(findViewById(R.id.container_root_PreviewBinDetails), "Location not found", Snackbar.LENGTH_INDEFINITE)
//                .setAction("RETRY", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        loadingCheckingForLocation = ProgressDialog.show(PreviewBinDetails.this, "Getting location", "Please wait ...", false, false);
//
//                        loadingCheckingForLocation.setCanceledOnTouchOutside(true);
//
//                        requestLocationUpdates(locationProvider);
//                    }
//                });
//
//        snackbar.setActionTextColor(Color.YELLOW);
//
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(Color.WHITE);
//        snackbar.show();
//    }

//    private void requestLocationUpdates(LocationProvider locationProvider){
//
//        Log.e("AddBin", "inside requestLocationUpdates");
//        locationTrack = new LocationTrack.Builder(this).withProvider(locationProvider).build().getLocationUpdates(PreviewBinDetails.this);
//
//    }

//    @Override
//    public void onLocationUpdate(Location location) {
//
//
//        Log.e("AddBin", "updated location: " + location.getLongitude() + " " + location.getLatitude());
//
//        accountCurrentBinDetails.setBinLatitude(String.valueOf(location.getLatitude()));
//        accountCurrentBinDetails.setBinLongitude(String.valueOf(location.getLongitude()));
//
//        tvCurrentLatitudeLongitude.setText( String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()) );
//
//        // dismiss progress dialog , which started in onCreate and  pressing retry
//        if(loadingCheckingForLocation!=null){
//            loadingCheckingForLocation.dismiss();
//        }
//
//
//        // Toast.makeText(SelectGovernorate.this,"Last location: " + location.getLongitude() + " " + location.getLatitude(),Toast.LENGTH_LONG).show();
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

//    private boolean checkLocation() {
//
//        if(!isLocationEnabled()){
//            showAlert();
//        }else{
////            loadingCheckingForLocationOnCreate = ProgressDialog.show(PreviewBinDetails.this, "Getting location", "Please wait ...", false, false);
////            loadingCheckingForLocationOnCreate.setCanceledOnTouchOutside(false);
//
//            requestLocationUpdates(locationProvider);
//
//        }
//        return isLocationEnabled();
//    }

//    private boolean checkLocationUpload() {
//
//        if(!isLocationEnabled()){
//            showAlert();
//        }else{
//
//            // retry to get
//            // gps is on request location updates
//             showNoLocationSnack();
//
//            //requestLocationUpdates(locationProvider);
//
//        }
//        return isLocationEnabled();
//    }

//    private boolean isLocationEnabled() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }

//    private void showAlert() {
//        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
//        dialog.setTitle("Enable Location")
//                .setMessage("Please Enable Location to use this app")
//                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//
//                        // showSosPrompt();
//
//                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(myIntent);
//
//                        paramDialogInterface.dismiss();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        // showSosPrompt();
//                    }
//                });
//        dialog.show();
//    }

    public void showGovernorateList() {

        Intent listIntet = new Intent(PreviewBinDetails.this, SelectFromListEditGovernorate.class);
        listIntet.putExtra("page_id", "GOVERNORATE");
        listIntet.putExtra("page_title", "Select Governorate");

        startActivityForResult(listIntet, 6);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    } // getGovernorateFromDb

    public void showCapacityList() {

        Intent listIntet = new Intent(PreviewBinDetails.this, SelectFromListEditCapacity.class);

        startActivityForResult(listIntet, 61);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    } // getGovernorateFromDb

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//                tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
//                tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
//                tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());
//
//    }//onActivityResult

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent intent = new Intent();
//        setResult(RESULT_OK, intent);
//        finish();

//        Intent intent = new Intent(PreviewBinDetails.this, SelectManufacturer.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    } // BackPressed


    // below code for checking user inactivity for 5 seconds,
    // if no touch is made in 5 seconds finish this activity and back to add beah code page

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        Log.e("AddBin", "onUserInteraction");

//        if(uploadSuccess){
//            Log.e("AddBin" , "onUserInteraction uploadSuccess true");
//            delayedIdle(5000);
//        }
    }


    Handler _idleHandler = new Handler();
    Runnable _idleRunnable = new Runnable() {
        @Override
        public void run() {
            //handle your IDLE state

            Log.e("AddBin", " inside run , finish screen is idle ");
//            finish();
//            Intent intent = new Intent(PreviewBinDetails.this, SelectManufacturer.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    };

    private void delayedIdle(int delaySeconds) {
        _idleHandler.removeCallbacks(_idleRunnable);
        _idleHandler.postDelayed(_idleRunnable, (delaySeconds));


        Log.e("AddBin", "inside delayedIdle  screen is idle ");
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
////        stopUpdates();
//
//        Log.i("AddBin", "onPause, done");
//    }

//    public void stopUpdates() {
//        if (locationTrack != null) {
//
//            locationTrack.stopLocationUpdates();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("@@@@locationstart");
        if (progressDoalogLoc != null) {

        } else {
            callprogressDialogLoc();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //.................Starts here........................
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initLocationSettings();
        startLocationServices();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2 * 1000);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        System.out.println("@@@@lat onResume" + latitude + " long" + longitude);
                        accountCurrentBinDetails.setBinLatitude(String.valueOf(latitude));
                        accountCurrentBinDetails.setBinLongitude(String.valueOf(longitude));
                        tvCurrentLatitudeLongitude.setText(String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));

//                        tv.setText("location= lat"+latitude+" long"+longitude);
                        progressDoalogLoc.dismiss();
//                        txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
//                        Toast.makeText(Read_RFIDActivity.this, " "+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
//        getLastLocation();
//        tv.setText("location= lat"+latitude+" long"+longitude);
    }
    //....................GPS Location_Tracker functions................................................


    private void startLocationServices() {
        Dexter.withContext(PreviewBinDetails.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                            if (progressDoalogLoc != null) {
                                progressDoalogLoc.dismiss();
                                progressDoalogLoc = null;
                            }

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void initLocationSettings() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void updateLocationUI() {
        System.out.println("@@@@locationend");
        if (mCurrentLocation != null) {
//            txtLocationResult.setText(
//                    "Lat: " + mCurrentLocation.getLatitude() + ", " +
//                            "Lng: " + mCurrentLocation.getLongitude()
//            );
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            System.out.println("@@@@latupdate" + latitude);
            accountCurrentBinDetails.setBinLatitude(String.valueOf(latitude));
            accountCurrentBinDetails.setBinLongitude(String.valueOf(longitude));
            tvCurrentLatitudeLongitude.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));

//            // giving a blink animation on TextView
//            txtLocationResult.setAlpha(0);
//            txtLocationResult.animate().alpha(1).setDuration(300);
//
//            // location last updated timefingerprint
//            txtUpdatedOn.setText("Last updated on: " + mLastUpdateTime);
        }

        //  toggleButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //Log.i(TAG, "All location settings are satisfied.");

//                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        // loading.dismiss();
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                //      "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(PreviewBinDetails.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    // Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                // Log.e(TAG, errorMessage);

                                Toast.makeText(PreviewBinDetails.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        // toggleButtons();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
        tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());


        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        //  Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private boolean checkPermissions() {

//        int permissionState = ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        return permissionState == PackageManager.PERMISSION_GRANTED;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void callprogressDialogLoc() {
        progressDoalogLoc = new ProgressDialog(PreviewBinDetails.this);
        progressDoalogLoc.setMessage("Fetching location...");
//        progressDoalog.setTitle("ProgressDialog bar example");
        progressDoalogLoc.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalogLoc.setCancelable(false);
        progressDoalogLoc.show();
    }
}
