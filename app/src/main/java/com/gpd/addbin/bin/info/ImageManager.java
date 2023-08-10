package com.gpd.addbin.bin.info;

/**
 * Created by lenovo on 5/8/2018.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ImageManager {

    private static final String TAG ="AddBin";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;


    public void volley_download_icon(final Context context, String mUrl, final String company_id){
        final HashMap<String, Object> map = new HashMap<String, Object>();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        try {


                            if (response!=null) {

                                String filename = company_id+"_icon.png";
                                Log.e("DEBUG::FILE NAME @@@ ", filename);

                                try{
                                    long lenghtOfFile = response.length;

                                    //covert reponse to input stream
                                    InputStream input = new ByteArrayInputStream(response);

                                    //Create a file on desired path and write stream data to it

                                    File folder = new File(Environment.getExternalStorageDirectory().toString()+"/AddBin/");
                                    if (!folder.exists()) {
                                        if (!folder.mkdir()) {
                                            Log.e("ERROR", "Cannot create a directory!");
                                        } else {
                                            folder.mkdir();
                                        }
                                    }
                                    File file = new File(folder, filename);
                                    map.put("resume_path", file.toString());
                                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));


                                    Log.d(TAG, "doInBackground: IN "+file.length());
                                    // Output stream
                                    //   OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/timeattendance/"+company_ID+"_"+empID+"_"+img_);


                                    byte data[] = new byte[1024];

                                    long total = 0;
                                    int count;


                                    while ((count = input.read(data)) != -1) {
                                        total += count;
                                        output.write(data, 0, count);

                                    }



                                    output.flush();

                                    output.close();
                                    input.close();



                                }catch(IOException e){
                                    e.printStackTrace();

                                }
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d(TAG, "UNABLE TO DOWNLOAD FILE @@@ ");
                            e.printStackTrace();
                        }
                    }
                } ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
            }
        }, null);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
