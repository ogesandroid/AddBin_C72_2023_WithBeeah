package com.gpd.addbin.bin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.favor.FavorAdapter;
import com.gpd.addbin.R;
import com.gpd.addbin.activity.UHFMainActivity;
import com.gpd.addbin.bin.info.Account;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

public class ReadRFIDActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    Account accountCurrentBinDetails;
    TextView tvSelectedGovernorate, tvSelectedWillayat, tvSelectedCapacity, tvManufacturer;
    LinearLayout linearExit, linearChangeBinCapacity;
    ImageView img_logout, img_logo;
    private LogoutReceiver logoutReceiver;
//    public RFIDWithUHF mReader;
    public RFIDWithUHFUART mReader;
    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_LOGOUT") || intent.getAction().equals("clicked.Change.Bin.Capacity")) {
                Log.e("AddBin", "inside onReceive  ");
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_r_f_i_d);

        initializeRfid();
        logoutReceiver = new LogoutReceiver();

        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        intentFilter.addAction("clicked.Change.Bin.Capacity");
        registerReceiver(logoutReceiver, intentFilter);

        accountCurrentBinDetails = new FavorAdapter.Builder(getApplicationContext()).build().create(Account.class);

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
                        Intent intent = new Intent(ReadRFIDActivity.this, LoginActivity.class);
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

    @Override
    protected void onDestroy() {

        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }

    /**
     * �豸�ϵ��첽��
     *
     * @author liuruifeng
     */
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
                Toast.makeText(ReadRFIDActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(ReadRFIDActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }

    public void initializeRfid() {

        if (mReader != null) {
            Log.e("AddBin", "initializeRfid mReaderobj is " + mReader.toString());
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
//            Intent intentPreview = new Intent(ReadRFIDActivity.this, PreviewBinDetails.class);
//            startActivityForResult(intentPreview, 116);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//            finish();
//        } else {
////            UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_fail);
////					mContext.playSound(2);
//        }
//    }

}