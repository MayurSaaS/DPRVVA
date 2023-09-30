package com.vvautotest.activities;

import static com.vvautotest.utils.ServerConfig.Photos_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.R;
import com.vvautotest.model.ImageDetail;
import com.vvautotest.model.Site;
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

public class FullPhotoViewActivity extends AppCompatActivity {
    @BindView(R.id.backBtn)
    LinearLayout backBtn;

    @BindView(R.id.image)
    ImageView image;

    String currentDate = "";
    String imageUrl = "";

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;

    @BindView(R.id.imageDetailsTV)
    TextView imageDetailsTV;

    String[] parts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo_view);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetails();
        selectedSite = sessionManager.getSelectedSite();
        Intent intent = getIntent();
        if(intent != null)
        {
            currentDate = intent.getStringExtra("currentDate");
            imageUrl = intent.getStringExtra("imageUrl");
        }
        parts = imageUrl.split("\\.");
        init();
        getImageDetails();
    }

    public void init(){
        try {
            Glide.with(image).load(Photos_URL +  selectedSite.id+ "/"
                    + currentUser.userID  + "/" + currentDate +   "/" +  imageUrl).into(image);
        }catch (Exception e)
        {e.printStackTrace();}
    }

    @OnClick(R.id.backBtn)
    public void onBack(){
        onBackPressed();
    }

    private void getImageDetails(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", currentUser.userID);
            jsonObject.put("siteID", selectedSite.id);
            jsonObject.put("dateFolder", currentDate);
            jsonObject.put("name", parts[0]);
            jsonObject.put("imageExt", parts[1]);

            L.printError("Request.... " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidNetworking.post(ServerConfig.Image_Details_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("image details")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             ImageDetail imageDetail = om.readValue(response.toString(), ImageDetail.class);
                                             imageDetailsTV.setText(imageDetail.imageDesc);
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }
                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         progressDialog.dismiss();
                                         L.printError(anError.toString());
                                         AppUtils.showToast(FullPhotoViewActivity.this, "Something went wrong, please try after sometime");
                                     }
                                 }
                );

    }

}