package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.R;
import com.vvautotest.adapter.PhotoAdapter;
import com.vvautotest.model.ImageData;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;
import com.vvautotest.utils.XMLParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageListActivity extends AppCompatActivity {

    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;
    @BindView(R.id.titleTV)
    TextView titleTV;

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.siteInfoTV)
    TextView siteInfoTV;
    @BindView(R.id.userInfoTV)
    TextView userInfoTV;
    ArrayList<ImageData> imageDataArrayList;
    PhotoAdapter photoAdapter;
    String folderName =  "";
    String selectedUserId =  "";
    String selectedSiteId =  "";
    String selectedUserName =  "";
    String selectedSiteName =  "";

    @BindView(R.id.noRecordMessageTV)
    TextView noRecordMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetails();
        selectedSite = sessionManager.getSelectedSite();

        Intent intent = getIntent();
        if(intent != null)
        {
            folderName = intent.getStringExtra("name");
            selectedUserId = intent.getStringExtra("selectedUserId");
            selectedSiteId = intent.getStringExtra("selectedSiteId");
            selectedUserName = intent.getStringExtra("selectedUserName");
            selectedSiteName = intent.getStringExtra("selectedSiteName");
        }
        titleTV.setText(folderName);
        siteInfoTV.setText("Site: " + selectedSiteName);
        userInfoTV.setText("User: " + selectedUserName);
        init();
        getGalleryImages();
    }

    public void init()
    {
        imageDataArrayList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(ImageListActivity.this, imageDataArrayList, folderName, selectedUserId, selectedSiteId);
        recyclerView.setLayoutManager(new GridLayoutManager(ImageListActivity.this, 3));
        recyclerView.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickListener(new PhotoAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ImageData data = imageDataArrayList.get(position);

                Intent intent = new Intent(ImageListActivity.this
                        , UploadPhotosActivity.class);
                intent.putExtra("currentDate", folderName);
                intent.putExtra("imageUrl", data.file);
                intent.putExtra("selectedUserId", selectedUserId);
                intent.putExtra("selectedSiteId", selectedSiteId);
                intent.putExtra("action", "edit");
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.navigationIcon)
    public void setupToolbar(){
        navigationIcon.setOnClickListener(v -> onBackPressed());
    }

    public void getGalleryImages(){
        ProgressDialog progressDialog = new ProgressDialog(ImageListActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.get(ServerConfig.Photos_URL + selectedSiteId + "/"
                        +  selectedUserId  + "/" + folderName +   "/")
                .setTag("Gallery Images")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                                 @Override
                                 public void onResponse(String response) {
                                     progressDialog.dismiss();
                                     L.printInfo(response.toString());
                                     try {
                                         JSONArray parseJson =  XMLParser.parseHtmlToJSON(response);
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             imageDataArrayList = new ArrayList<>();
                                             imageDataArrayList = om.readValue(parseJson.toString(), new TypeReference<List<ImageData>>(){});
                                             if(imageDataArrayList != null && imageDataArrayList.size() > 0)
                                             {
                                                 noRecordMessageTV.setVisibility(View.GONE);
                                                 photoAdapter.updateList(imageDataArrayList);
                                             }else {
                                                 photoAdapter.updateList(imageDataArrayList);
                                                 noRecordMessageTV.setVisibility(View.VISIBLE);
                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                             photoAdapter.updateList(imageDataArrayList);
                                             noRecordMessageTV.setVisibility(View.VISIBLE);
                                         }
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }

                                 @Override
                                 public void onError(ANError anError) {
                                     L.printError(anError.toString());
                                     imageDataArrayList = new ArrayList<>();
                                     photoAdapter.updateList(imageDataArrayList);
                                     noRecordMessageTV.setVisibility(View.VISIBLE);
                                     progressDialog.dismiss();
                                 }
                             }
                );
    }
}