package com.gpd.addbin.bin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cocosw.favor.FavorAdapter;

import com.gpd.addbin.R;
import com.gpd.addbin.bin.adapter.WillayatAdapter;
import com.gpd.addbin.bin.db.DataBaseHelper;
import com.gpd.addbin.bin.db.GovernorateDao;
import com.gpd.addbin.bin.db.Willayat;
import com.gpd.addbin.bin.db.WillayatDao;
import com.gpd.addbin.bin.info.Account;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.Query;

import java.io.File;

public class SelectWillayat extends AppCompatActivity {


    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_LOGOUT")) {
                finish();
            }
        }
    }

    private LogoutReceiver logoutReceiver;


    DataBaseHelper myDb;
    Query<Willayat> willayatQuery;
    private WillayatDao willayatDao;
    private GovernorateDao governorateDao;

    WillayatAdapter adapter;
    Spinner willayatSpinner;
    RelativeLayout rlNext;
    TextView tvSelectedGovernorate, tvSelectedWillayat;
    Account accountCurrentBinDetails;

    RelativeLayout rlSelectWillayat;
    ImageView img_icon;

    @Override
    protected void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(logoutReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_willayat);

        accountCurrentBinDetails = new FavorAdapter.Builder(SelectWillayat.this).build().create(Account.class);

        if (accountCurrentBinDetails.getWillayathStatus().equals("1")) {
            accountCurrentBinDetails.setWillayat("No "+accountCurrentBinDetails.getWillayathTitle());
        } else {
            accountCurrentBinDetails.setWillayat("No Willayat");
        }
        myDb = new DataBaseHelper(this);

        logoutReceiver = new LogoutReceiver();

        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(logoutReceiver, intentFilter);


        rlSelectWillayat = (RelativeLayout) findViewById(R.id.container_show_value_SelectWillayat);
        rlSelectWillayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWillayatFromDb();

            }
        });

        tvSelectedGovernorate = (TextView) findViewById(R.id.tv_selected_governorate_value_SelectWillayat);

        tvSelectedWillayat = (TextView) findViewById(R.id.tv_selected_willayat_from_list_SelectWillayat);
        if(accountCurrentBinDetails.getWillayathStatus().equals("1")){
            tvSelectedWillayat.setText("Select "+accountCurrentBinDetails.getWillayathTitle());
        }else{
            tvSelectedWillayat.setText("Select Willayat");
        }
        img_icon = (ImageView) findViewById(R.id.img_logo);

        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());

        willayatSpinner = (Spinner) findViewById(R.id.sp_SelectWillayat);

        rlNext = (RelativeLayout) findViewById(R.id.rl_enter_SelectWillayat);
        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accountCurrentBinDetails.getWillayathStatus().equals("1")) {
                    if (tvSelectedWillayat.getText().toString().equals("Select "+accountCurrentBinDetails.getWillayathTitle()) || accountCurrentBinDetails.getWillayat().equals("No "+accountCurrentBinDetails.getWillayathTitle())) {
                        Toast.makeText(SelectWillayat.this, "Please enter "+accountCurrentBinDetails.getWillayathTitle(), Toast.LENGTH_LONG).show();


                        final MediaPlayer mp = MediaPlayer.create(SelectWillayat.this, R.raw.beep);
                        mp.start();

                    } else {

                        Intent nextIntet = new Intent(SelectWillayat.this, SelectCapacity.class);
                        startActivityForResult(nextIntet, 113);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                } else {
                    if (tvSelectedWillayat.getText().toString().equals("Select Willayat") || accountCurrentBinDetails.getWillayat().equals("No Willayat")) {
                        Toast.makeText(SelectWillayat.this, "Please enter Willayat", Toast.LENGTH_LONG).show();


                        final MediaPlayer mp = MediaPlayer.create(SelectWillayat.this, R.raw.beep);
                        mp.start();

                    } else {

                        Intent nextIntet = new Intent(SelectWillayat.this, SelectCapacity.class);
                        startActivityForResult(nextIntet, 113);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }


            }
        });
        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            img_icon.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID() + "_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
            Picasso.with(this).load(file).into(img_icon);
        }

        willayatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {

                Willayat willayatObj = (Willayat) (parent.getItemAtPosition(pos));
                accountCurrentBinDetails.setWillayat(willayatObj.getWillayat_name());
                accountCurrentBinDetails.setWillayatId(willayatObj.getWillayat_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    public void getWillayatFromDb() {

        Intent listIntet = new Intent(SelectWillayat.this, SelectFromList.class);
        listIntet.putExtra("page_id", "WILLAYAT");
        listIntet.putExtra("page_title", "Select Willayat");

        startActivityForResult(listIntet, 3);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    } // getGovernorateFromDb

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        super.onActivityResult(requestCode, resultCode, data);
        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());

        if (requestCode == 3) {

            tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());

        } else {
            if (requestCode == 113) {


                tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());

                accountCurrentBinDetails.setCapacity("No Bin Capacity");
                accountCurrentBinDetails.setManufacturer("No Manufacturer");
            }
        }

    }//onActivityResult

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
