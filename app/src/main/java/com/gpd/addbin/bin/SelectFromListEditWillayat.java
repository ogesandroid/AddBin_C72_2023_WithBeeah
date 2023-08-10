package com.gpd.addbin.bin;

import android.content.Intent;
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


public class SelectFromListEditWillayat extends AppCompatActivity {

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
    String intentGovernorate,intentGovernorateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_from_list_edit_willayat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            intentGovernorate = extras.getString("edit_from_governorate");
            intentGovernorateId = extras.getString("edit_from_governorate_id");

        }

        tvPageTitle = (TextView) findViewById(R.id.tv_page_title_SelectFromListEditWillayat);

        accountCurrentBinDetails = new FavorAdapter.Builder(SelectFromListEditWillayat.this).build().create(Account.class);

        // get the governorate DAO
        DaoSession daoSession = ((AppController) getApplication()).getDaoSession();

        willayatDao = daoSession.getWillayatDao();

        willayatQuery = willayatDao.queryBuilder().where(WillayatDao.Properties.Governorate_id.eq( intentGovernorateId )).build();

        listView=(ListView)findViewById(R.id.lv_SelectFromListEditWillayat);

        setTitleText("Select Willayat");
        getWillayatFromDb();

    }

    public void setTitleText(String pageTitle){
        tvPageTitle.setText(pageTitle);
    }


    public void getWillayatFromDb(){

        final List<Willayat> governorateNames = willayatQuery.list();

        if(governorateNames.isEmpty()){

        }else{

            willayatAdapter= new WillayatListAdapter(governorateNames,getApplicationContext());
            listView.setAdapter(willayatAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Willayat dataModel= governorateNames.get(position);


                    accountCurrentBinDetails.setGovernorate(intentGovernorate);
                    accountCurrentBinDetails.setGovernorateId(intentGovernorateId);

                    accountCurrentBinDetails.setWillayat(dataModel.getWillayat_name());
                    accountCurrentBinDetails.setWillayatId(dataModel.getWillayat_id());

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.package.ACTION_WILLAYAT");
                    sendBroadcast(broadcastIntent);

                    finish();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);


                }
            });
        }
    }



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
