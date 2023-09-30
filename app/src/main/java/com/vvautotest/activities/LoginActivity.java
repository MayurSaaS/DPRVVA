package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
        sessionManager = new SessionManager(this);
    }

    public void init(){
        deviceId = AppUtils.getDeviceId();
        deviceIdTV.setText("Device ID: " + deviceId);
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