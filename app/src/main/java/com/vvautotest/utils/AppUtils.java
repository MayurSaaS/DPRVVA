package com.vvautotest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.vvautotest.R;

import java.util.ArrayList;

public class AppUtils {

    public static class UserType {
        public static final String TYPE_MANAGEMENT = "Management";
    }

    public static class AppRoute {

        public static final int ROUTE_PROGRESS_ENTRY = 1;
        public static final int ROUTE_DIRECT_MATERIAL = 2;
        public static final int ROUTE_MAIN_DPR = 3;
        public static final int ROUTE_MW_AND_HSD_ISSUE = 4;

        public static final int ROUTE_OPENING_STOCK = 5;
        public static final int ROUTE_RECEIPT_STOCK = 6;
        public static final int ROUTE_CLOSING_STOCK = 7;

        public static final int ROUTE_PHOTOS = 8;
        public static final int ROUTE_DPR_DWR_PDF = 9;
        public static final int ROUTE_MANPOWER_ENTRY = 10;
        public static final int ROUTE_LEAVE_APPLICATION = 11;
        public static final int ROUTE_FUND_REQUEST = 12;
        public static final int ROUTE_PRE_SALE = 13;

        public static final int ROUTE_HOME = 0;
        public static final int ROUTE_LOGOUT = 14;

        public static final int ROUTE_ADD_PHOTOS = 15;
        public static final int ROUTE_ADD_PDF = 16;
        public static final int ROUTE_ADD_PROGRESS = 17;
        public static final int ROUTE_ADD_MANPOWER_ATTENDANCE = 18;

    }

    public static class Menu {
        public static final String HOME = "Home";
        public static final String PHOTOS = "Photos";
        public static final String DPR_DWR_PDF = "DPR/DWR PDF";
        public static final String MANPOWER_ENTRY = "ME & Attendance";
        public static final String PROGRESS_ENTRY = "Progress Entry";
        public static final String DIRECT_MATERIAL = "DMC Entry";
        public static final String MAIN_DPR = "Main DPR Entry";
        public static final String OPENING_STOCK = "Opening Stock Entry";
        public static final String RECEIPT_STOCK = "Receipt Stock Entry";
        public static final String CLOSING_STOCK = "Closing Stock Entry";
        public static final String MW_AND_HSD_ISSUE = "MW & HID Entry";
        public static final String LEAVE_APPLICATION = "Leave Application";
        public static final String FUND_REQUEST = "Fund request";
        public static final String PRE_SALE = "Pre Sale - Site Survey";
        public static final String LOGOUT = "Logout";

        public static final String ADD_PHOTOS = "Add Photos";
        public static final String ADD_PDF = "Add PDF";
        public static final String ADD_PROGRESS = "Add Progress";
        public static final String ADD_MANPOWER_ATTENDANCE = "Add Manpower/Attendance";


    }

    public static class SubMenu {
        public static final String SUB_MANPOWER_ENTRY = "Manpower Entry & Attendance";
        public static final String SUB_DIRECT_MATERIAL = "Direct Material Consumption";
        public static final String SUB_MW_AND_HSD_ISSUE = "Machine Working & HSD Issue Details";
    }

    public static class Action {
        public static final String Action_View = "isViewAllowed";
        public static final String Action_Add = "isAddAllowed";
        public static final String Action_Edit = "isEditAllowed";
        public static final String Action_Delete = "isDeleteAllowed";
        public static final String Action_Download = "isDownloadAllowed";
        public static final String Action_Upload = "isUploadAllowed";
        public static final String Action_Request = "isRequestAllowed";
        public static final String Action_Approve = "isApproveAllowed";
    }

    public static void showToast(Activity context, String message)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(context);
        //    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.show();
    }


    public static String getDeviceId()
    {
        return "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.SUPPORTED_ABIS.length%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
    }

    public static boolean CheckCoordinateLiesInPolygon(ArrayList<LatLng> list, LatLng point)
    {
        return PolyUtil.containsLocation(point.latitude, point.longitude, list, true);
    }


    public static void setActionValueWithKey(Context context, boolean language, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, language);
        editor.apply();
    }

    public static boolean getActionValueWithKey(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }


    public static void setCameraImagePath(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cam_image_path", language);
        editor.apply();
    }

    public static String getCameraImagePath(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("cam_image_path", "");
    }


    public static void setLatitude(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lati", language);
        editor.apply();
    }

    public static String getLatitude(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("lati", "0");
    }

    public static void setLongitude(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("longi", language);
        editor.apply();
    }

    public static String getLongitude(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("longi", "0");
    }


}
