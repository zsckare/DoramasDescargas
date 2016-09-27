package xyz.zsckare.doramasdownloader;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.LinkedList;

import xyz.zsckare.doramasdownloader.Models.DoramaThumbModel;
import xyz.zsckare.doramasdownloader.Models.GenereModel;

public class Comun {
    public static String main_url = "";
    public static LinkedList<DoramaThumbModel> list_doramas_thumbs = new LinkedList();
    public static LinkedList<GenereModel> list_generes = new LinkedList();

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
