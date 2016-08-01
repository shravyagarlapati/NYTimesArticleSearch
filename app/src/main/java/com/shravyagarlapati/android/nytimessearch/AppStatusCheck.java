package com.shravyagarlapati.android.nytimessearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by shravyagarlapati on 7/31/16.
 */
public class AppStatusCheck
{
    private static AppStatusCheck instance = new AppStatusCheck();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    public static AppStatusCheck getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}
