package com.vvautotest.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.R;
import com.vvautotest.model.User;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.logInBtn)
    CardView logInBtn;
    @BindView(R.id.forgetPasswordBtn)
    TextView forgetPasswordBtn;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.userET)
    EditText userET;
    @BindView(R.id.deviceIdTV)
    TextView deviceIdTV;
    String deviceId;
    SessionManager sessionManager;
    final int REQUEST_CODE = 101;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
        sessionManager = new SessionManager(this);
    }

    @SuppressLint("MissingPermission")
    public void init(){
        deviceId = AppUtils.getDeviceId(this);
        L.printError("Device Id : " + deviceId);
        /*// in the below line, we are initializing our variables.
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        // in the below line, we are checking for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // if permissions are not provided we are requesting for permissions.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        }

        // in the below line, we are setting our imei to our text view.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceId = telephonyManager.getImei();
        }*/
        deviceIdTV.setText("Device ID: " + deviceId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            // in the below line, we are checking if permission is granted.
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // in the below line, we are setting our imei to our text view.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceId = telephonyManager.getImei();
                }
                deviceIdTV.setText("Device ID: " + deviceId);
            } else {
                // in the below line, we are displaying toast message
                // if permissions are not granted.
             //   Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.logInBtn)
    public void onLoginBtnClick()
    {
        String user = userET.getText().toString().trim();
        String pass = passwordET.getText().toString().trim();

        if("".equals(user))
        {
            AppUtils.showToast(LoginActivity.this, "Please enter user");
            return;
        }
        if("".equals(pass))
        {
            AppUtils.showToast(LoginActivity.this, "Please enter password");
            return;
        }

        doLogin(user, pass);

    }

    @OnClick(R.id.forgetPasswordBtn)
    public void onForgotPasswordClick()
    {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }


    private void doLogin(String user, String pass){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginID", user);
            jsonObject.put("password", pass);
            jsonObject.put("deviceID", deviceId);
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
                                sessionManager.setPassword(pass);
                                sessionManager.saveUser(response.toString());
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                AppUtils.showToast(LoginActivity.this, user.result.message);
                            }else
                            {
                                AppUtils.showToast(LoginActivity.this, user.result.message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        L.printError(anError.toString());
                        AppUtils.showToast(LoginActivity.this, "Something went wrong, please try after sometime");
                    }
                }
                );

    }




}