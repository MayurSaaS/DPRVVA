package com.vvautotest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.activities.HomeActivity;
import com.vvautotest.activities.LoginActivity;
import com.vvautotest.db.CategoryRepo;
import com.vvautotest.db.UserItemRepo;
import com.vvautotest.model.Category;
import com.vvautotest.model.User;
import com.vvautotest.model.UserItem;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                getCategories();
            }
        };

        countDownTimer.start();
        L.printError("Device Id : " + AppUtils.getDeviceId());

    }

    private void moveToLogin()
    {
        if(sessionManager.isLogin())
        {
            doLogin(sessionManager.getUserDetails().email, sessionManager.getPassword());
        }else
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }



    private void doLogin(String user, String pass){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginID", user);
            jsonObject.put("password", pass);
            jsonObject.put("deviceID", AppUtils.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.printError("Login Request : " + jsonObject.toString());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login, Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidNetworking.post(ServerConfig.Login_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("login")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             User user = om.readValue(response.toString(), User.class);
                                             if(user.result.result)
                                             {
                                                 sessionManager.setLogin(true);
                                                 sessionManager.saveUser(response.toString());
                                                 Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                 startActivity(intent);
                                                 finish();
                                             //    AppUtils.showToast(MainActivity.this, user.result.message);
                                             }else
                                             {
                                                 AppUtils.showToast(MainActivity.this, user.result.message);
                                                 Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                 startActivity(intent);
                                                 finish();
                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }

                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         progressDialog.dismiss();
                                         L.printError(anError.toString());
                                         AppUtils.showToast(MainActivity.this, "Something went wrong, please try after sometime");
                                     }
                                 }
                );

    }


    private void getCategories(){
        AndroidNetworking.post(ServerConfig.Categories_URL)
                .setTag("Categories")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        L.printInfo(response.toString());
                                        ObjectMapper om = new ObjectMapper();
                                        try {
                                            ArrayList<Category> tempList = om.readValue(response.toString(), new TypeReference<List<Category>>(){});
                                            for (Category data: tempList) {
                                                CategoryRepo studentRepository = new CategoryRepo(MainActivity.this);
                                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (!studentRepository.isCategoryExist(data.id))
                                                        {
                                                            studentRepository.insertNewCategory(data);
                                                        }
                                                    }
                                                });
                                            }
                                          getUsers();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                           getUsers();
                                        }
                                    }
                                    @Override
                                    public void onError(ANError anError) {
                                     //   L.printError(anError.toString());
                                       getUsers();
                                    }
                                }
                );

    }

    private void getUsers(){
        JSONObject jsonObject = new JSONObject();
        AndroidNetworking.post(ServerConfig.Users_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Users")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        L.printInfo(response.toString());
                                        ObjectMapper om = new ObjectMapper();
                                        try {
                                        ArrayList<UserItem>   sitesArrayList = om.readValue(response.toString(), new TypeReference<List<UserItem>>(){});
                                            for (UserItem data: sitesArrayList) {
                                                UserItemRepo userItemRepo = new UserItemRepo(MainActivity.this);
                                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (!userItemRepo.isUserItemExist(data.id))
                                                        {
                                                            userItemRepo.insertNewUserItem(data);
                                                        }
                                                    }
                                                });
                                            }
                                            moveToLogin();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            moveToLogin();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        L.printError(anError.toString());
                                        moveToLogin();
                                    }
                                }
                );

    }


    @Override
    protected void onPause() {
        super.onPause();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}