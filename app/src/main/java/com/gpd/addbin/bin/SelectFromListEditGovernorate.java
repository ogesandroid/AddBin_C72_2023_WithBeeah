package com.gpd.addbin.bin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cocosw.favor.FavorAdapter;
import com.gpd.addbin.R;
import com.gpd.addbin.bin.adapter.CapacityListAdapter;
import com.gpd.addbin.bin.adapter.GovernorateListAdapter;
import com.gpd.addbin.bin.adapter.ManufacturerListAdapter;
import com.gpd.addbin.bin.adapter.WillayatListAdapter;
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


import org.greenrobot.greendao.query.Query;

import java.util.List;


public class SelectFromListEditGovernorate extends AppCompatActivity {

    public class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_WILLAYAT")) {
                finish();
            }
        }
    }


    private LogoutReceiver logoutReceiver;

    TextView tvPageTitle;

    private Query<Governorate> governorateQuery;
    private Query<Willayat> willayatQuery;
    private Query<Capacity> capacityQuery;
    private Query<Manufacturer> manufacturerQuery;

    private GovernorateDao governorateDao;
    private WillayatDao willayatDao;
    private CapacityDao capacityDao;
    private ManufacturerDao manufacturerDao;


    private static GovernorateListAdapter adapter;
    private static WillayatListAdapter willayatAdapter;
    private static CapacityListAdapter capacityAdapter;
    private static ManufacturerListAdapter manufacturerAdapter;

    ListView listView;

    Account accountCurrentBinDetails;

    @Override
    protected void onDestroy() {

        // Unregister the logout receiver
        unregisterReceiver(logoutReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_from_list_edit_governorate);


        logoutReceiver = new LogoutReceiver();
        // Register the logout receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_WILLAYAT");
        registerReceiver(logoutReceiver, intentFilter);

        tvPageTitle = (TextView) findViewById(R.id.tv_page_title_SelectFromListEditGovernorate);

        accountCurrentBinDetails = new FavorAdapter.Builder(SelectFromListEditGovernorate.this).build().create(Account.class);

        // get the governorate DAO
        DaoSession daoSession = ((AppController) getApplication()).getDaoSession();

        governorateDao = daoSession.getGovernorateDao();
        willayatDao = daoSession.getWillayatDao();
        capacityDao = daoSession.getCapacityDao();
        manufacturerDao = daoSession.getManufacturerDao();

        governorateQuery = governorateDao.queryBuilder().build();
        willayatQuery = willayatDao.queryBuilder().where(WillayatDao.Properties.Governorate_id.eq( accountCurrentBinDetails.getGovernorateId() )).build();
        capacityQuery = capacityDao.queryBuilder().build();
        manufacturerQuery = manufacturerDao.queryBuilder().build();

        listView=(ListView)findViewById(R.id.lv_SelectFromListEditGovernorate);

        setTitleText("Select Governorate");
        getGovernorateFromDb();

    }

    public void setTitleText(String pageTitle){
        tvPageTitle.setText(pageTitle);
    }

    public void getGovernorateFromDb(){

        final List<Governorate> governorateNames = governorateQuery.list();

        if(governorateNames.isEmpty()){

        }else{

            adapter= new GovernorateListAdapter(governorateNames,getApplicationContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Governorate dataModel= governorateNames.get(position);

                    Intent intent = new Intent(SelectFromListEditGovernorate.this,SelectFromListEditWillayat.class);

                    intent.putExtra("edit_from_governorate", dataModel.getGovernorate_name());
                    intent.putExtra("edit_from_governorate_id", dataModel.getGovernorate_id());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });
        }

    } // getGovernorateFromDb


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }
}
