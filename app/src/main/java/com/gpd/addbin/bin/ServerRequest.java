package com.gpd.addbin.bin;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ServerRequest {

    String TAG="AddBin";

    private static AsyncHttpClient client = new AsyncHttpClient();


    AddBin addBin;
    public ServerRequest(AddBin addBin, Context cxt) {
        this.addBin = addBin;
    }

    public void postData(HashMap<String, String> parameters, String method, int status) throws IOException {

        StringBuilder apiUrl = new StringBuilder();
        apiUrl.append(method);


        RequestParams params = new RequestParams(parameters);

        if (status==1) {

            client.post(apiUrl.toString(), params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String serverResponse = new String(responseBody);
                    Log.i(TAG, serverResponse);
                    addBin.serverResponseData(serverResponse);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    String serverResponse = new String(responseBody);
                    Log.i(TAG, serverResponse);
                    Log.e(TAG, "NOOOOO");
                }


            });
        }else {

        }

    }






}
