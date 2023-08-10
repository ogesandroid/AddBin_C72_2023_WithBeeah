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
import com.gpd.addbin.bin.adapter.CapacityAdapter;
import com.gpd.addbin.bin.db.Capacity;
import com.gpd.addbin.bin.db.CapacityDao;
import com.gpd.addbin.bin.info.Account;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.Query;

import java.io.File;

public class SelectCapacity extends AppCompatActivity {


    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_LOGOUT")) {
                finish();
            }
        }
    }

    private LogoutReceiver logoutReceiver;

    private Query<Capacity> capacityQuery;
    private CapacityDao capacityDao;
    CapacityAdapter adapter;
    Spinner capacitySpinner;
    RelativeLayout rlNext;
    Account accountCurrentBinDetails;
    TextView tvSelectedGovernorate,tvSelectedWillayat,tvSelectedCapacity;
    RelativeLayout rlSelectCapacity;
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
        setContentView(R.layout.activity_select_capacity);


        accountCurrentBinDetails = new FavorAdapter.Builder(SelectCapacity.this).build().create(Account.class);
        accountCurrentBinDetails.setCapacity("No Bin Capacity");

        logoutReceiver = new LogoutReceiver();

        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(logoutReceiver, intentFilter);


        tvSelectedCapacity = (TextView) findViewById(R.id.tv_selected_capacity_SelectCapacity);

        tvSelectedGovernorate = (TextView) findViewById(R.id.tv_selected_governorate_value_SelectCapacity);
        tvSelectedWillayat = (TextView) findViewById(R.id.tv_selected_willayat_value_SelectCapacity);
        img_icon = (ImageView) findViewById(R.id.img_logo);

        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());

        capacitySpinner = (Spinner) findViewById(R.id.sp_SelectCapacity);

        rlSelectCapacity = (RelativeLayout) findViewById(R.id.container_show_value_SelectCapacity);
        rlSelectCapacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCapacityFromDb();

            }
        });
        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            img_icon.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID()+"_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
            Picasso.with(this).load(file).into(img_icon);
        }

        rlNext = (RelativeLayout) findViewById(R.id.rl_enter_SelectCapacity);
        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tvSelectedCapacity.getText().toString().equals("Select Bin Capacity")  || accountCurrentBinDetails.getCapacity().equals("No Bin Capacity")){
                    Toast.makeText(SelectCapacity.this,"Please enter Capacity", Toast.LENGTH_LONG).show();


                    final MediaPlayer mp = MediaPlayer.create(SelectCapacity.this, R.raw.beep);
                    mp.start();

                }else {

                    Intent nextIntet = new Intent(SelectCapacity.this,SelectManufacturer.class);
                    startActivityForResult(nextIntet , 114);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });


        capacitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {

                Capacity capacityObj = (Capacity) (parent.getItemAtPosition(pos));
                accountCurrentBinDetails.setCapacity(capacityObj.getCapacity_name());
                accountCurrentBinDetails.setCapacityId(capacityObj.getCapacity_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void getCapacityFromDb(){

        Intent listIntet = new Intent(SelectCapacity.this,SelectFromList.class);
        listIntet.putExtra("page_id", "CAPACITY");
        listIntet.putExtra("page_title", "Select Bin Capacity");

        startActivityForResult(listIntet, 4);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    } // getGovernorateFromDb

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        super.onActivityResult(requestCode, resultCode, data);
        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());


        if (requestCode == 4) {

            tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());

        } else {
            if (requestCode == 114) {
                accountCurrentBinDetails.setManufacturer("No Manufacturer");
                tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());

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
