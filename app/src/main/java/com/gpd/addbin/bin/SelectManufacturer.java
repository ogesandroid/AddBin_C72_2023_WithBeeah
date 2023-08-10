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
import com.gpd.addbin.bin.adapter.ManufacturerAdapter;
import com.gpd.addbin.bin.db.Manufacturer;
import com.gpd.addbin.bin.db.ManufacturerDao;
import com.gpd.addbin.bin.info.Account;
import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.Query;

import java.io.File;

public class SelectManufacturer extends AppCompatActivity {


    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent.getAction().equals("com.package.ACTION_LOGOUT") || intent.getAction().equals("clicked.Change.Bin.Capacity") ) {
                finish();
            }
        }
    }

    private LogoutReceiver logoutReceiver;

    private Query<Manufacturer> manufacturrQuery;
    private ManufacturerDao manufacturertDao;
    ManufacturerAdapter adapter;
    Spinner manufacturerSpinner;
    RelativeLayout rlNext;
    Account accountCurrentBinDetails;

    TextView tvSelectedGovernorate,tvSelectedWillayat,tvSelectedCapacity,tvSelectedManufacturer;
    RelativeLayout rlSelectManufacturer;
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
        setContentView(R.layout.activity_select_manufacturer);

        accountCurrentBinDetails = new FavorAdapter.Builder(SelectManufacturer.this).build().create(Account.class);

        accountCurrentBinDetails.setManufacturer("No Manufacturer");

        // if gone back to Capacity and come to this page, we have to take code freshly
        accountCurrentBinDetails.setCurrentTypeInPreview("");
        accountCurrentBinDetails.setCurrentTypeInPreviewValue("");

        // To prevent automatic reading of rfid on reaching AddBeah screen
        // because if some setCallRfid's true condition is not removed
        // below line make sures , on reaching AddBeah screen rfid wont work automatically
        accountCurrentBinDetails.setCallRfid("false");

        logoutReceiver = new LogoutReceiver();


        tvSelectedManufacturer = (TextView) findViewById(R.id.tv_selected_manufacturer_SelectManufacturer);

        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        intentFilter.addAction("clicked.Change.Bin.Capacity");
        registerReceiver(logoutReceiver, intentFilter);


        tvSelectedGovernorate = (TextView) findViewById(R.id.tv_selected_governorate_value_SelectManufacturer);
        tvSelectedWillayat = (TextView) findViewById(R.id.tv_selected_willayat_value_SelectManufacturer);
        tvSelectedCapacity = (TextView) findViewById(R.id.tv_selected_capacity_value_SelectManufacturer);

        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
        tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());

        manufacturerSpinner = (Spinner) findViewById(R.id.sp_SelectManufacturer);
        img_icon = (ImageView) findViewById(R.id.img_logo);

        rlSelectManufacturer = (RelativeLayout) findViewById(R.id.container_show_value_SelectManufacturer);
        rlSelectManufacturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getManufacturerFromDb();

            }
        });
        if (accountCurrentBinDetails.getCompanyFlag().equals("1")) {
            img_icon.setVisibility(View.VISIBLE);
            String imagePath = accountCurrentBinDetails.getCompanyID()+"_icon.png";
            File downloadsFolder = new File(Environment.getExternalStorageDirectory() + "/AddBin/");
            Uri file = Uri.fromFile(new File(downloadsFolder, imagePath));
            //Picasso.with(this).load(file).placeholder(R.drawable.user).error(R.drawable.user).into(img_icon);
            Picasso.with(this).load(file).into(img_icon);
        }
        rlNext = (RelativeLayout) findViewById(R.id.rl_enter_SelectManufacturer);
        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tvSelectedManufacturer.getText().toString().equals("Select Manufacturer")  || accountCurrentBinDetails.getManufacturer().equals("No Manufacturer")){
                    Toast.makeText(SelectManufacturer.this,"Please enter Manufacturer", Toast.LENGTH_LONG).show();


                    final MediaPlayer mp = MediaPlayer.create(SelectManufacturer.this, R.raw.beep);
                    mp.start();

                }else {

                    Intent nextIntet = new Intent(SelectManufacturer.this,AddBeahCode.class);
                    startActivityForResult(nextIntet,115);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }
        });

        manufacturerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {

                Manufacturer manufacturerObj = (Manufacturer) (parent.getItemAtPosition(pos));
                accountCurrentBinDetails.setManufacturer(manufacturerObj.getManufacturer_name());
                accountCurrentBinDetails.setManufacturerId(manufacturerObj.getManufacturer_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }


    public void getManufacturerFromDb(){

        Intent listIntet = new Intent(SelectManufacturer.this,SelectFromList.class);
        listIntet.putExtra("page_id", "MANUFACTURER");
        listIntet.putExtra("page_title", "Select Manufacturer");

        startActivityForResult(listIntet, 5);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    } // getGovernorateFromDb

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        super.onActivityResult(requestCode, resultCode, data);
        tvSelectedGovernorate.setText(accountCurrentBinDetails.getGovernorate());
        tvSelectedWillayat.setText(accountCurrentBinDetails.getWillayat());
        tvSelectedCapacity.setText(accountCurrentBinDetails.getCapacity());

        if (requestCode == 5) {

            tvSelectedManufacturer.setText(accountCurrentBinDetails.getManufacturer());

        } else {
            if (requestCode == 115) {

                tvSelectedManufacturer.setText("Select Manufacturer");
                tvSelectedManufacturer.setText(accountCurrentBinDetails.getManufacturer());

                accountCurrentBinDetails.setBeahCode("");


                // if cmae back from AddBeah code, we have to take code freshly
                accountCurrentBinDetails.setCurrentTypeInPreview("");
                accountCurrentBinDetails.setCurrentTypeInPreviewValue("");

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
