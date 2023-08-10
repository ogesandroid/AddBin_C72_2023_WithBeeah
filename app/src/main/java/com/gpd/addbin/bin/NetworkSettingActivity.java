package com.gpd.addbin.bin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gpd.addbin.R;
import com.gpd.addbin.bin.data.remote.ApiUtils;


public class NetworkSettingActivity extends AppCompatActivity {

    TinyDB tinyDB;
    Button btn_configure;
    EditText edt_base_url;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private static final String TAG = "RemoteApp";
    View focusView;
    boolean cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_setting);

        btn_configure = (Button) findViewById(R.id.btn_configure);
        edt_base_url = (EditText) findViewById(R.id.edt_base_url);

        tinyDB = new TinyDB(this);
        cancel = false;
        focusView = null;

        btn_configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiUtils.BASE_URL="";
                ApiUtils.ICON_URL="";
                String base_url = edt_base_url.getText().toString().trim();
                if (TextUtils.isEmpty(base_url)) {
                    edt_base_url.setError("Please enter the base url");
                    focusView = edt_base_url;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.

                    Log.e(TAG, "onClick:111 ");
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    Log.e(TAG, "onClick: ");

                    tinyDB.setString("base_url", base_url);
                    ApiUtils.BASE_URL=base_url;
                    ApiUtils.ICON_URL=base_url+"data_folder/company/logo/";

                    String base_url1 = tinyDB.preference.getString("base_url", "");
                    Log.e("base_url","From tiny DB"+base_url1);
                    Toast.makeText(NetworkSettingActivity.this, "base="+ApiUtils.BASE_URL, Toast.LENGTH_SHORT).show();
                    finish();
                    Intent i = new Intent(NetworkSettingActivity.this, LoginActivity.class);
                    startActivity(i);
                }

            }
        });
    }
}
