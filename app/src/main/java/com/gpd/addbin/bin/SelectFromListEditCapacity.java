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


public class SelectFromListEditCapacity extends AppCompatActivity {



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_from_list_edit_governorate);

        tvPageTitle = (TextView) findViewById(R.id.tv_page_title_SelectFromListEditGovernorate);

        accountCurrentBinDetails = new FavorAdapter.Builder(SelectFromListEditCapacity.this).build().create(Account.class);

        // get the governorate DAO
        DaoSession daoSession = ((AppController) getApplication()).getDaoSession();

        capacityDao = daoSession.getCapacityDao();

        capacityQuery = capacityDao.queryBuilder().build();

        listView=(ListView)findViewById(R.id.lv_SelectFromListEditGovernorate);

        setTitleText("Select Capacity");
        getCapacityFromDb();

    }

    public void setTitleText(String pageTitle){
        tvPageTitle.setText(pageTitle);
    }

    public void getCapacityFromDb(){

        final List<Capacity> governorateNames = capacityQuery.list();

        if(governorateNames.isEmpty()){

        }else{

            capacityAdapter= new CapacityListAdapter(governorateNames,getApplicationContext());
            listView.setAdapter(capacityAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Capacity dataModel= governorateNames.get(position);

                    accountCurrentBinDetails.setCapacity(dataModel.getCapacity_name());
                    accountCurrentBinDetails.setCapacityId(dataModel.getCapacity_id());

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
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
