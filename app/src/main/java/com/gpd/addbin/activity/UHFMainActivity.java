package com.gpd.addbin.activity;


import com.cocosw.favor.FavorAdapter;

import com.gpd.addbin.R;
import com.gpd.addbin.bin.LoginActivity;
import com.gpd.addbin.bin.SelectManufacturer;
import com.gpd.addbin.bin.info.Account;
import com.gpd.addbin.fragment.UHFKillFragment;
import com.gpd.addbin.fragment.UHFLockFragment;
import com.gpd.addbin.fragment.UHFReadFragment;
import com.gpd.addbin.fragment.UHFReadTagFragment;
import com.gpd.addbin.fragment.UHFSetFragment;
import com.gpd.addbin.fragment.UHFWriteFragment;
import com.rscja.utility.StringUtility;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.HashMap;

/**
 * UHF使用demo
 * <p>
 * 1、使用前请确认您的机器已安装此模块。
 * 2、要正常使用模块需要在\libs\armeabi\目录放置libDeviceAPI.so文件，同时在\libs\目录下放置DeviceAPIver20160728.jar文件。
 * 3、在操作设备前需要调用 init()打开设备，使用完后调用 free() 关闭设备
 * <p>
 * <p>
 * 更多函数的使用方法请查看API说明文档
 *
 * @author 更新于 2016年8月9日
 */
public class UHFMainActivity extends BaseTabFragmentActivity {

    private final static String TAG = "MainActivity";
    Account accountCurrentBinDetails;
    TextView tvSelectedGovernorate, tvSelectedWillayat, tvSelectedCapacity, tvManufacturer;
    LinearLayout linearExit, linearChangeBinCapacity;
    ImageView img_logout, img_logo;
    private LogoutReceiver logoutReceiver;


    Handler handler;
    AlertDialog deleteDialog;
//	public AppContext appContext;// ȫ��Context
//
    // public Reader mReader;
//	public RFIDWithUHF mReader;

//	public void playSound(int id) {
//		appContext.playSound(id);


//	}


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
        setContentView(R.layout.activity_main);
//		if (Build.VERSION.SDK_INT > 21) {
//
//
//			//读写内存权限
//			if (ContextCompat.checkSelfPermission(this,
//					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//				// 请求权限
//				ActivityCompat
//						.requestPermissions(
//								this,
//								new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//								1);
//			}
//
//			int checkCallPhonePermission = ContextCompat.checkSelfPermission(
//					this, Manifest.permission.READ_EXTERNAL_STORAGE);
//			if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//				ActivityCompat.requestPermissions(this, new String[]{
//						Manifest.permission.WRITE_EXTERNAL_STORAGE,
//						Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
//				return;
//			} else {
//				// 已申请权限直接跳转到下一个界面
//
//
//			}
//		}


//			appContext = (AppContext) getApplication();
        initSound();
        initUHF(); //��ʼ��
        initViewPageData();
        initViewPager();
        initTabs();
        hideActionBar();

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
                        Intent intent = new Intent(UHFMainActivity.this, LoginActivity.class);
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
    protected void initViewPageData() {
        lstFrg.add(new UHFReadTagFragment());
        lstFrg.add(new UHFReadFragment());
        lstFrg.add(new UHFWriteFragment());
        lstFrg.add(new UHFKillFragment());
        lstFrg.add(new UHFLockFragment());
        lstFrg.add(new UHFSetFragment());


        lstTitles.add(getString(R.string.uhf_msg_tab_scan));
        lstTitles.add(getString(R.string.uhf_msg_tab_read));
        lstTitles.add(getString(R.string.uhf_msg_tab_write));
        lstTitles.add(getString(R.string.uhf_msg_tab_kill));
        lstTitles.add(getString(R.string.uhf_msg_tab_lock));
        lstTitles.add(getString(R.string.uhf_msg_tab_set));

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
                Toast.makeText(UHFMainActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(UHFMainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }


    /**
     * ��֤ʮ����������Ƿ���ȷ
     *
     * @param str
     * @return
     */
    public boolean vailHexInput(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        // ���ȱ�����ż��
        if (str.length() % 2 == 0) {
            return StringUtility.isHexNumberRex(str);
        }

        return false;
    }

    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;

    private void initSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    /**
     * 播放提示音
     *
     * @param id 成功1，失败2
     */
    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    1, // 优先级，0为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
