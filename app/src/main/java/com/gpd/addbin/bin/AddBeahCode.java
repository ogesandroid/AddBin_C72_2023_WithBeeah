package com.gpd.addbin.bin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cocosw.favor.FavorAdapter;

import com.gpd.addbin.R;
import com.gpd.addbin.activity.UHFMainActivity;
import com.gpd.addbin.bin.info.Account;
import com.gpd.addbin.fragment.UHFReadTagFragment;
import com.gpd.addbin.tools.UIHelper;
import com.rscja.deviceapi.Barcode1D;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddBeahCode extends AppCompatActivity {


    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_LOGOUT") || intent.getAction().equals("clicked.Change.Bin.Capacity")) {
                Log.e("AddBin", "inside onReceive  ");
                finish();
            }
        }
    }

    Handler handler;
    Runnable runnable;
    AlertDialog deleteDialog;

    //Declare timer
    CountDownTimer cTimer;
    //ProgressDialog mypDialogTimer;

    ProgressDialog continuousProgressDialog;

    private LogoutReceiver logoutReceiver;

    private Handler handlerBarCode;

    Account accountCurrentBinDetails;
    TextView tvSelectedGovernorate, tvSelectedWillayat, tvSelectedCapacity, tvManufacturer;
    EditText etBeahCode;

//    public RFIDWithUHF mReader;
    public RFIDWithUHFUART mReader;

    public Barcode1D mInstance; // earlier private

    private boolean isBarcodeOpened = false;
    private ExecutorService executor;
    String rfidValue, barcodeValue;
    View layoutRfidReadingSuccess;
    LinearLayout linearExit, linearChangeBinCapacity;
    ImageView img_logout, img_logo;
    private static final String TAG = "AddBin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beah_code);

        logoutReceiver = new LogoutReceiver();

        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        intentFilter.addAction("clicked.Change.Bin.Capacity");
        registerReceiver(logoutReceiver, intentFilter);

        accountCurrentBinDetails = new FavorAdapter.Builder(AddBeahCode.this).build().create(Account.class);

        LayoutInflater liRfidUpload = getLayoutInflater();
        layoutRfidReadingSuccess = liRfidUpload.inflate(R.layout.layout_rfid_reading_success, (ViewGroup) findViewById(R.id.layout_checking_rfid_success));

        linearExit = (LinearLayout) findViewById(R.id.container_exit_AddBeahCode);
        linearChangeBinCapacity = (LinearLayout) findViewById(R.id.container_change_capacity_AddBeahCode);

        tvSelectedGovernorate = (TextView) findViewById(R.id.tv_selected_governorate_value_AddBeahCode);
        tvSelectedWillayat = (TextView) findViewById(R.id.tv_selected_willayat_value_AddBeahCode);
        tvSelectedCapacity = (TextView) findViewById(R.id.tv_selected_capacity_value_AddBeahCode);
        tvManufacturer = (TextView) findViewById(R.id.tv_selected_manufacturer_value_AddBeahCode);
        img_logout = (ImageView) findViewById(R.id.img_logout);
        img_logo = (ImageView) findViewById(R.id.img_logo);

        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
        tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());
        tvManufacturer.setText(accountCurrentBinDetails.getManufacturer());

        etBeahCode = (EditText) findViewById(R.id.et_AddBeahCode);

        accountCurrentBinDetails.setBeahCode("");
        etBeahCode.setText("");

        // to automatically popup the screen on reaching the screen
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);  //commented for no beaah code

        etBeahCode.setOnEditorActionListener(new DoneOnEditorActionListener());

        barcodeHandler();

        initializeBarcode();

        initializeRfid();
        // if only barcode no problem
        // next just initializing rfid objects
        // when initiazed here barcode value is empty
        // initializeRfid();

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            img_logo.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID() + "_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
            Picasso.with(this).load(file).into(img_logo);
        }

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
//        submitFunctionForNoBeahCode();  // for no beahcode
    } // onCreate

    public void logout() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        Log.d(TAG, "logout: ");

        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        accountCurrentBinDetails.setCompanyID("");
                        accountCurrentBinDetails.setIsDatabaseSetForFirstTime("false");
                        Intent intent = new Intent(AddBeahCode.this, LoginActivity.class);
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

    public void barcodeHandler() {

        handlerBarCode = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if (msg != null) {
                    String strData = "";
                    switch (msg.arg1) {
                        case 0:
                            strData = "";
                            break;
                        case 1:
                            strData = msg.obj.toString();
                            break;
                        default:
                            break;
                    }
                    barcodeValue = strData;
                    if (barcodeValue == null) {
                        Log.e("AddBin", "barcode is null ");

                        continuousProgressDialog.cancel();

                    } else {
                        Log.e("AddBin", "barcode is not null ");
//                    removeBarCodeObjects();
//                    firstRfid();

                        if (barcodeValue.length() == 0) {
                            Log.e("AddBin", "barcode is zero length ");


                            if (mReader != null) {
                                Log.e("AddBin", "handler mReaderobj is " + mReader.toString());
                            }

                            if (mInstance != null) {
                                Log.e("AddBin", "handler mInstance is " + mInstance.toString());
                            }

                            if (executor != null) {
                                Log.e("AddBin", "handler executor is " + executor.toString());
                            }

                            removeBarCodeObjects();

                            initializeRfid();

                            firstRfid();
                        } else {

                            Log.e("AddBin", "barcode has value");

                            continuousProgressDialog.cancel();

                            Toast.makeText(AddBeahCode.this, " BARCODE read successfully", Toast.LENGTH_LONG).show();

                            accountCurrentBinDetails.setCurrentScanTypeInPreview("barcode");
                            accountCurrentBinDetails.setCurrentTypeInPreview("Your BARCODE is");
                            accountCurrentBinDetails.setCurrentTypeInPreviewValue(barcodeValue);
                            // go to Preview Page no need of Submit button
                            submitFunction();
                        }

                    } // else
                }
            }

        };

    } //barCodeHandler

    public void initializeBarcode() {

        Log.e("AddBin", "inside initializeBarcode ");

        try {
            mInstance = Barcode1D.getInstance();
            Log.e("AddBin", "inside initializeBarcode value of minstance object" + mInstance);

        } catch (ConfigurationException e) {

            Log.e("AddBin", "inside initializeBarcode error is  " + e.toString());

            //  Toast.makeText(AddBeahCode.this, "Error is " + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        // Earlier below code was not there , but mInstance.open() is creating error in BarCodeTask ,
        // because BarCodeInitTask is in onResume so when restarting Activity Barcode Task is running but no mInstance object
        try {
            executor = Executors.newFixedThreadPool(6);

            Log.e("AddBin", "inside initializeBarcode executor object is  " + executor);

            new BarcodeInitTask().execute();

        } catch (Exception e) {
            // Toast.makeText(AddBeahCode.this, "Error is " + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void submitFunction() {
        String beahCode = etBeahCode.getText().toString();
        if (beahCode.length() == 5) {

            Date d = new Date();
            CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss aa", d.getTime());

            // output  time is 2018-02-10 08:55:23 AM
            Log.e("AddBin", " time is " + s.toString());
//            Toast.makeText(this, beahCode, Toast.LENGTH_SHORT).show();
            accountCurrentBinDetails.setBeahCode(beahCode);
            accountCurrentBinDetails.setBinTime(s.toString());
            accountCurrentBinDetails.setDataBinStatus("1");

            Intent intentPreview = new Intent(AddBeahCode.this, UHFMainActivity.class);
            startActivityForResult(intentPreview, 116);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else {
            Toast.makeText(AddBeahCode.this, "Please enter a valid BEAH code", Toast.LENGTH_LONG).show();
            final MediaPlayer mp = MediaPlayer.create(AddBeahCode.this, R.raw.beep);
            mp.start();
        }
    }

    public void submitFunctionForNoBeahCode() {
//        String beahCode = etBeahCode.getText().toString();
//        if (beahCode.length() == 5) {

        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss aa", d.getTime());

        // output  time is 2018-02-10 08:55:23 AM
        Log.e("AddBin", " time is " + s.toString());
//            Toast.makeText(this, beahCode, Toast.LENGTH_SHORT).show();
        accountCurrentBinDetails.setBeahCode("");
        accountCurrentBinDetails.setBinTime(s.toString());
        accountCurrentBinDetails.setDataBinStatus("1");

        Intent intentPreview = new Intent(AddBeahCode.this, UHFMainActivity.class);
        startActivityForResult(intentPreview, 116);
        overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
    }

    public void initializeRfid() {

        if (mReader != null) {
            Log.e("AddBin", "initializeRfid mReaderobj is " + mReader.toString());
        }

        if (mInstance != null) {
            Log.e("AddBin", "initializeRfid mInstance is " + mInstance.toString());
        }

        if (executor != null) {
            Log.e("AddBin", "initializeRfid executor is " + executor.toString());
        }

        try {
//            mReader = RFIDWithUHF.getInstance();
            mReader = RFIDWithUHFUART.getInstance();

            Log.e("AddBin", "inside initializeRfid mReader object is  " + mReader.toString());
        } catch (Exception ex) {
            //  Toast.makeText(AddBeahCode.this,"Error is "+ ex.toString(),Toast.LENGTH_LONG).show();
            Log.e("AddBin", "inside mReader exception " + ex.toString());

        }
        try {
            if (mReader != null) {
                Log.e("AddBin", "inside initializeRfid calling InitTask " + mReader.toString());
                new InitTask().execute();
            }
        } catch (Exception e) {
            Log.e("AddBin", "inside InitTask exception " + e.toString());
        }
    } // initializeRfid

    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                accountCurrentBinDetails.setBeahCode(etBeahCode.getText().toString());
                /* Deepa Commented 04/07/2019
                    showRfidReadPopup();
                 */
                submitFunction();
                return true;
            }
            return false;
        }
    }

    public void showRfidReadPopup() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.popup_go_near_rfid, null);
        deleteDialog = new AlertDialog.Builder(this).create();

        deleteDialog.setCancelable(true);

        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.iv_close_popup_rfid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                deleteDialog.dismiss();
            }
        });

        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();

        deleteDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == 139) {
                    if (event.getRepeatCount() == 0) {

                        Log.e("AddBin", "inside DialogInterface keydown  ");

                        closePopup();
                    }
                }
                return true;
            }
        });

        // Hide after some seconds
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Log.e("AddBin", "inside run just before  closePopup ");
                closePopup();
            }
        };


        handler.postDelayed(runnable, 2000);

    } // showRfidPopup

    public void closePopup() {

        Log.e("AddBin", "inside closePopup function  ");

        if (deleteDialog.isShowing()) {
            deleteDialog.dismiss();
            Log.e("AddBin", "inside runnable  deleteDialog.dismiss()  ");

            if (handler != null) {

                handler.removeCallbacksAndMessages(null);
                Log.e("AddBin", "inside closePopup handler not null  ");
            } else {
                Log.e("AddBin", "inside closePopup handler not null  ");
            }

        } else {
            Log.e("AddBin", "showPopup not Showing ");
            if (handler != null) {

                handler.removeCallbacksAndMessages(null);

                Log.e("AddBin", "inside closePopup handler not null  ");
            } else {
                Log.e("AddBin", "inside closePopup handler not null  ");
            }
        }

    }


    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mypDialog.cancel();

            if (!result) {
                //   Toast.makeText(AddBeahCode.this, "initialization failed",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(AddBeahCode.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("initializing ");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
        tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());
        tvManufacturer.setText(accountCurrentBinDetails.getManufacturer());


        // on coming back after clicking in Change beah code
        // or auto exit after 5 seconds
        etBeahCode.setText("");

        /* show keyboard */
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }//onActivityResult


    @Override
    protected void onResume() {
        super.onResume();

        Log.e("AddBin", "inside onResume ");

        initializeBarcode();

    } // onResume

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("AddBin", "inside onPause");

        Log.d("AddBin", "inside onPause callRfid is not true");

        if (executor != null) {

            executor.shutdownNow();
        }

        if (isBarcodeOpened) {
            mInstance.close();
        }
    } // onPause

    @Override
    protected void onDestroy() {

        Log.e("AddBin", "inside  onDestroy method");

        unregisterReceiver(logoutReceiver);

        cancelTimer();
        mReader.stopInventory();
        isInventory=false;
        if (mReader != null) {
            mReader.free();
        }

        super.onDestroy();
    } // onDestroy

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    } // onBackPressed


    public class BarcodeInitTask extends AsyncTask<String, Integer, Boolean> {

        //ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mInstance.open();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            isBarcodeOpened = result;

            //  mypDialog.cancel();

            if (!result) {
                //  Toast.makeText(AddBeahCode.this, "init fail",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

    }

    class GetBarcode implements Runnable {

        private boolean isContinuous = false;
        String barCode = "";
        private long sleepTime = 1000;
        Message msg = null;

        @Override
        public void run() {

            Log.e("AddBin", "inside run of GetBarcode ");

            // getting value of barcode
            barCode = mInstance.scan();

            Log.e("AddBin", "inside run of GetBarcode barCode obj is  " + barCode.toString());
            msg = new Message();
            if (StringUtility.isEmpty(barCode)) {
                msg.arg1 = 0;
                msg.obj = "";
            } else {
                msg.arg1 = 1;
                msg.obj = barCode;
            }
            handlerBarCode.sendMessage(msg);

        } // run
    } // AddBeah code


    // Deepa Commented 04/07/2019
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (event.getRepeatCount() == 0) {

                Log.e("AddBin", "inside keycode == 139 ");

                /*Deepa commented 04/07/2019
//                scanBarCode();

                */
                readTag();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    private void scanBarCode() {

        continuousProgressDialog = new ProgressDialog(AddBeahCode.this);
        continuousProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        continuousProgressDialog.setMessage("please wait... ");
        continuousProgressDialog.setCanceledOnTouchOutside(false);
        continuousProgressDialog.show();

        Log.e("AddBin", "inside scan button clicked");
        executor.execute(new GetBarcode());


        if (mReader != null) {
            Log.e("AddBin", "scanBarCode mReaderobj is " + mReader.toString());
        }

        if (mInstance != null) {
            Log.e("AddBin", "scanBarCode mInstance is " + mInstance.toString());
        }

        if (executor != null) {
            Log.e("AddBin", "scanBarCode executor is " + executor.toString());
        }

    } // scanBarCode

    public void removeRfidObjects() {

        if (mReader != null) {
            Log.e("AddBin", "inside re-initializtion of barcod mReader is not null freeing it");
            mReader.free();
        }
    } // removeRfidObjects

    public void removeBarCodeObjects() {
        if (executor != null) {
            Log.e("AddBin", "executor is shutdown ");
            executor.shutdownNow();
        }

        if (isBarcodeOpened) {
            Log.e("AddBin", "mInstance is Closed - isBarcodeOpened ");

            //earlier it was  mInstance.close();

            mInstance.close();
        }

        if (mInstance != null) {
            Log.e("AddBin", "mInstance is Closed - mInstance not null ");
            mInstance.close();
        }
    } // removeBarCodeObjects

    public void firstRfid() {

        Log.e("AddBin", "inside firstRfid");

        cTimer = null;

        startTimer();

    } // firstRfid

    //start timer function
    void startTimer() {

        cTimer = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

                // getting value of Rfid
//                rfidValue = mReader.inventorySingleTag();

                if (rfidValue == null) {

                    Log.e("AddBin", "value of rfid is null and calling initializeBarcode");

                } else {

                    if (rfidValue.length() == 0) {

                        Log.e("AddBin", "value of rfid is zero and calling initializeBarcode");

                        // commented bzoz below lines will repeat if no rfid is got
                        // if rfid is got after 2 seconds below line will work 2 times
                        // so we will add it in timer finish function so only once

                    } else {

                        // we got rfid , so one theoretical loop complete , next clear all objects of barcode , because if scan button is pressed ,
                        // red light has to come as an indication of barcode is working correctly
                        // in the theoretical loop , on button click check for barcode , if barCodeResult == null , go for rfid


                        Log.e("AddBin", "value of rfid is " + rfidValue + " and setCallRfid to false");

                        Log.e("AddBin", "onTick: " + mReader.readTagFromBuffer());
                        accountCurrentBinDetails.setCurrentScanTypeInPreview("rfid");
                        accountCurrentBinDetails.setCurrentTypeInPreview("Your RFID is");
                        accountCurrentBinDetails.setCurrentTypeInPreviewValue(rfidValue);

                        continuousProgressDialog.cancel();

                        removeRfidObjects();
                        initializeBarcode();

                        Log.e("AddBin", "before cancelTimer line");

                        cancelTimer();

                        Log.e("AddBin", "after cancelTimer line");
                        Toast.makeText(AddBeahCode.this, "RFID read successfully ", Toast.LENGTH_LONG).show();

                        // go to Preview Page no need of Submit button
                        submitFunction();

                    } //else of rfid length == 0

                } // else of  rfidValue == null
            }

            public void onFinish() {

                continuousProgressDialog.cancel();

                Log.e("AddBin", "inside onFinish ");
                removeRfidObjects();
                initializeBarcode();
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

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

//    private void readTag() {
//        String strUII = mReader.inventorySingleTag();
//        if (!TextUtils.isEmpty(strUII)) {
//            String strEPC = mReader.convertUiiToEPC(strUII);
////            addEPCToList(strEPC, "N/A");
////            Toast.makeText(this, "dat@@@"+strEPC, Toast.LENGTH_SHORT).show();
//
//            Date d = new Date();
//            CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss aa", d.getTime());
//
//            // output  time is 2018-02-10 08:55:23 AM
//            Log.e("AddBin", " time is " + s.toString());
////            Toast.makeText(this, beahCode, Toast.LENGTH_SHORT).show();
//            accountCurrentBinDetails.setBeahCode("");
//            accountCurrentBinDetails.setBinTime(s.toString());
//            accountCurrentBinDetails.setDataBinStatus("1");
//
//            accountCurrentBinDetails.setCurrentScanTypeInPreview("rfid");
//            accountCurrentBinDetails.setCurrentTypeInPreview("Your RFID is");
//            accountCurrentBinDetails.setCurrentTypeInPreviewValue(strEPC);
////            Toast.makeText(this, accountCurrentBinDetails.getBeahCode(), Toast.LENGTH_SHORT).show();
//            String rfid = strEPC;
//            String lastSix = "";
//            if (rfid.length() > 6) {
//                lastSix = rfid.substring(rfid.length() - 6);
//            }
//            String beahCode = "A" + lastSix;
//            Log.e("@beahCode", "=" + lastSix);
//            accountCurrentBinDetails.setBeahCode(beahCode);
//
//            Intent intentPreview = new Intent(AddBeahCode.this, PreviewBinDetails.class);
//            startActivityForResult(intentPreview, 116);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//            finish();
//        } else {
////            UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_fail);
////					mContext.playSound(2);
//        }
//    }

    boolean isInventory = false;
    private final Handler handler1 = new Handler(Looper.getMainLooper());
    private void readTag() {
        if (mReader.startInventoryTag()) {
//success
            Log.d("UHF_sample", "inside readTag if");
            isInventory = true;
            new ThreadInventory(handler1).start();
        } else {
            mReader.stopInventory();
            Log.d("UHF_sample", "inside readTag else");
//fail
        }
    }
    private class ThreadInventory extends Thread {
        private Handler handler;

        public ThreadInventory(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void run() {
            while (isInventory) {
                Log.d("UHF_sample", "inside ThreadInventory while");
                UHFTAGInfo uhftagInfo = mReader.readTagFromBuffer();
                if (uhftagInfo == null) {
                    Log.d("UHF_sample", "inside uhftagInfo null");
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                String strEPC = uhftagInfo.getEPC();
                String rssi = uhftagInfo.getRssi();
//                toastMessage("EPC=" + epc + " rssi=" + rssi);
//                tv_data.setText(epc);
                Log.d("UHF_sample", "epc= "+strEPC);
                Log.d("UHF_sample", "got data from thread");

                Date d = new Date();
                CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss aa", d.getTime());

                // output  time is 2018-02-10 08:55:23 AM
                Log.e("AddBin", " time is " + s.toString());
//            Toast.makeText(this, beahCode, Toast.LENGTH_SHORT).show();
                accountCurrentBinDetails.setBeahCode("");
                accountCurrentBinDetails.setBinTime(s.toString());
                accountCurrentBinDetails.setDataBinStatus("1");

                accountCurrentBinDetails.setCurrentScanTypeInPreview("rfid");
                accountCurrentBinDetails.setCurrentTypeInPreview("Your RFID is");
                accountCurrentBinDetails.setCurrentTypeInPreviewValue(strEPC);
//            Toast.makeText(this, accountCurrentBinDetails.getBeahCode(), Toast.LENGTH_SHORT).show();
                String rfid = strEPC;
                String lastSix = "";
                if (rfid.length() > 6) {
                    lastSix = rfid.substring(rfid.length() - 6);
                }
                String beahCode = "A" + lastSix;
                Log.e("@beahCode", "=" + lastSix);

                if(etBeahCode.isShown()){
                    accountCurrentBinDetails.setBeahCode(etBeahCode.getText().toString().trim());
                }else{
                    accountCurrentBinDetails.setBeahCode(beahCode);
                }


                Intent intentPreview = new Intent(AddBeahCode.this, PreviewBinDetails.class);
                startActivityForResult(intentPreview, 116);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Access the TextView and update its text
//                        tv_data.setText("epc= "+epc);
                    }
                });
                break;
//.....
            }
        }
    }
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
