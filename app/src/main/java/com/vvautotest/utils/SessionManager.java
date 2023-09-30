package com.vvautotest.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.activities.LoginActivity;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;

import java.io.IOException;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context cx;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "VVAutotest";

    public SessionManager(Context cx) {
        this.cx = cx;
        pref = cx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean value) {
        editor.putBoolean("is_login", value);
        editor.commit();
    }

    public void setPassword(String value) {
        editor.putString("user_pass", value);
        editor.commit();
    }

    public boolean isLogin() {
        return pref.getBoolean("is_login", false);
    }

    public String getPassword() {
        return pref.getString("user_pass", "");
    }

    public void saveUser(String userResponse) {
        editor.putString("user_response", userResponse);
        editor.commit();
    }

    public void saveSelectedSite(Site site) {
        ObjectMapper om = new ObjectMapper();
        try
        {
            String siteJson = om.writeValueAsString(site);
            editor.putString("selected_site", siteJson);
            editor.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getSite() {
        return pref.getString("selected_site", "{}");
    }

    public Site getSelectedSite() {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(getSite(), Site.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void logout() {
        editor.clear();
        editor.putBoolean("is_login", false);
        editor.commit();
        Intent i = new Intent(cx, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        cx.startActivity(i);
    }

    private String getUser() {
        return pref.getString("user_response", "{}");
    }

    public User getUserDetails() {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(getUser(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
