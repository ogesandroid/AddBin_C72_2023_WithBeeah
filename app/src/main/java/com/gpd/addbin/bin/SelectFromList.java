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

public class SelectFromList extends AppCompatActivity {

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
        setContentView(R.layout.activity_select_from_list);

        tvPageTitle = (TextView) findViewById(R.id.tv_page_title_SelectFromList);

        accountCurrentBinDetails = new FavorAdapter.Builder(SelectFromList.this).build().create(Account.class);

        // get the governorate DAO
        DaoSession daoSession = ((AppController) getApplication()).getDaoSession();

        governorateDao = daoSession.getGovernorateDao();
        willayatDao = daoSession.getWillayatDao();
        capacityDao = daoSession.getCapacityDao();
        manufacturerDao = daoSession.getManufacturerDao();

        governorateQuery = governorateDao.queryBuilder().build();
        willayatQuery = willayatDao.queryBuilder().where(WillayatDao.Properties.Governorate_id.eq(accountCurrentBinDetails.getGovernorateId())).build();
        capacityQuery = capacityDao.queryBuilder().build();
        manufacturerQuery = manufacturerDao.queryBuilder().build();

        listView = (ListView) findViewById(R.id.lv_SelectFromList);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String value = extras.getString("page_id");

            if (value.equals("GOVERNORATE")) {
                if (accountCurrentBinDetails.getGovernarateStatus().equals("1")) {
                    setTitleText("Select "+accountCurrentBinDetails.getGovernarateTitle());
                } else {
                    setTitleText("Select Governorate");
                }

                getGovernorateFromDb();

            } else if (value.equals("WILLAYAT")) {
                if (accountCurrentBinDetails.getWillayathStatus().equals("1")) {
                    setTitleText("Select "+accountCurrentBinDetails.getWillayathTitle());
                } else {
                    setTitleText("Select Willayat");
                }

                getWillayatFromDb();

            } else if (value.equals("CAPACITY")) {

                setTitleText("Select Bin Capacity");
                getCapacityFromDb();

            } else if (value.equals("MANUFACTURER")) {

                setTitleText("Select Manufacturer");
                getManufacturerDb();

            }
            //get the value based on the key
        }

    }

    public void setTitleText(String pageTitle) {
        tvPageTitle.setText(pageTitle);
    }

    public void getGovernorateFromDb() {

        final List<Governorate> governorateNames = governorateQuery.list();

        if (governorateNames.isEmpty()) {

        } else {

            adapter = new GovernorateListAdapter(governorateNames, getApplicationContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Governorate dataModel = governorateNames.get(position);

                    accountCurrentBinDetails.setGovernorate(dataModel.getGovernorate_name());
                    accountCurrentBinDetails.setGovernorateId(dataModel.getGovernorate_id());
                    accountCurrentBinDetails.setGovernorateValue(dataModel.getGovernorate_value());

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

                }
            });
        }

    } // getGovernorateFromDb

    public void getWillayatFromDb() {

        final List<Willayat> governorateNames = willayatQuery.list();

        if (governorateNames.isEmpty()) {

        } else {

            willayatAdapter = new WillayatListAdapter(governorateNames, getApplicationContext());
            listView.setAdapter(willayatAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Willayat dataModel = governorateNames.get(position);

                    accountCurrentBinDetails.setWillayat(dataModel.getWillayat_name());
                    accountCurrentBinDetails.setWillayatId(dataModel.getWillayat_id());
                    accountCurrentBinDetails.setWillayatValue(dataModel.getWillayat_value());

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

                }
            });
        }
    }

    public void getCapacityFromDb() {

        final List<Capacity> governorateNames = capacityQuery.list();

        if (governorateNames.isEmpty()) {

        } else {

            capacityAdapter = new CapacityListAdapter(governorateNames, getApplicationContext());
            listView.setAdapter(capacityAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Capacity dataModel = governorateNames.get(position);

                    accountCurrentBinDetails.setCapacity(dataModel.getCapacity_name());
                    accountCurrentBinDetails.setCapacityId(dataModel.getCapacity_id());
                    accountCurrentBinDetails.setCapacityValue(dataModel.getCapacity_value());

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

                }
            });
        }
    }

    public void getManufacturerDb() {

        final List<Manufacturer> governorateNames = manufacturerQuery.list();

        if (governorateNames.isEmpty()) {

        } else {

            manufacturerAdapter = new ManufacturerListAdapter(governorateNames, getApplicationContext());
            listView.setAdapter(manufacturerAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Manufacturer dataModel = governorateNames.get(position);

                    accountCurrentBinDetails.setManufacturer(dataModel.getManufacturer_name());
                    accountCurrentBinDetails.setManufacturerId(dataModel.getManufacturer_id());

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }
}
