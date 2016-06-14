package com.mac.chatapp.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by admin on 12/06/2016.
 */
public class Validation {

    public static boolean validateNetworkState (Object obj) {
        ConnectivityManager manager = (ConnectivityManager) obj;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if ( networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) ) {
            return true;
        }
        return false;
    }

}
