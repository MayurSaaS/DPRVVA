package com.vvautotest.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.vvautotest.R;
import com.vvautotest.activities.HomeActivity;
import com.vvautotest.activities.PDFAdapter;
import com.vvautotest.activities.UploadPDFActivity;
import com.vvautotest.adapter.SiteSpinnerAdapter;
import com.vvautotest.adapter.UserSpinnerAdapter;
import com.vvautotest.model.ImageData;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.model.UserItem;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.DateUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;
import com.vvautotest.utils.XMLParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DPRDWRFragment extends Fragment implements HomeActivity.HomePageActionsListenr {

    @BindView(R.id.uploadPhotos)
    CardView uploadPhotos;
    @BindView(R.id.wrongSiteMessageLL)
    LinearLayout wrongSiteMessageLL;
    @BindView(R.id.noRecordMessageTV)
    TextView noRecordMessageTV;
    @BindView(R.id.siteSpinner)
    TextView siteSpinner;
    @BindView(R.id.userSpinner)
    TextView userSpinner;
    @BindView(R.id.filterLL)
    LinearLayout filterLL;


    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;

    String currentDate = "";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<ImageData> imageDataArrayList;
    PDFAdapter photoAdapter;

    String menuId;
    boolean currentPointPolygonStatus;
    Context context;

    ArrayList<Site> sitesArrayList;
    ArrayList<UserItem> usersArrayList;

    String selectedUserId;
    String selectedUserName;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public DPRDWRFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity)context).setHomePageActionsListenr(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_d_p_r_d_w_r, container, false);
        ButterKnife.bind(this, rootView);
        sessionManager = new SessionManager(context);
        if (getArguments() != null) {
            menuId = getArguments().getString("menuId");
            currentPointPolygonStatus = getArguments().getBoolean("currentPointPolygonStatus", false);
        }
        currentUser = sessionManager.getUserDetails();
        selectedUserId = currentUser.userID;
        selectedUserName = currentUser.fName + " " + currentUser.mName + " " + currentUser.lName ;
        selectedSite = sessionManager.getSelectedSite();
        if(currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT))
        {
            filterLL.setVisibility(View.VISIBLE);
            siteSpinner.setText(selectedSite.name);
            userSpinner.setText(currentUser.fName + " " + currentUser.mName + " " + currentUser.lName);
            loadSite();
         //   loadUser();
        }else
        {
            filterLL.setVisibility(View.GONE);
        }
        currentDate = DateUtils.getCurrentDate("dd-MM-yyyy");
        L.printError("Current Formatted Date : " + currentDate);
        init(rootView);
        updateData();
        getGalleryFolders();
        return rootView;
    }

    public void updateData()
    {
        if(currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT))
        {
            wrongSiteMessageLL.setVisibility(View.GONE);
            if (currentPointPolygonStatus) {
                uploadPhotos.setVisibility(View.VISIBLE);
            } else {
                uploadPhotos.setVisibility(View.GONE);
            }
        }else
        {
            if (currentPointPolygonStatus) {
                uploadPhotos.setVisibility(View.VISIBLE);
                wrongSiteMessageLL.setVisibility(View.GONE);
            } else {
                uploadPhotos.setVisibility(View.GONE);
                wrongSiteMessageLL.setVisibility(View.VISIBLE);
            }
        }
    }


    private void init(View rootView)
    {
        if(AppUtils.getActionValueWithKey(context, AppUtils.Action.Action_Add))
        {
            uploadPhotos.setVisibility(View.VISIBLE);
        }else
        {
            uploadPhotos.setVisibility(View.GONE);
        }

        imageDataArrayList = new ArrayList<>();
        photoAdapter = new PDFAdapter(context, imageDataArrayList, currentDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickListener(new PDFAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ImageData data = imageDataArrayList.get(position);
                openPDF(data.file);
            }
        });
    }


    public void getGalleryFolders(){
        noRecordMessageTV.setVisibility(View.GONE);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.get(ServerConfig.PDF_URL + selectedSite.id + "/"
                        +  selectedUserId + "/")
                .setTag("PDF")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                                 @Override
                                 public void onResponse(String response) {
                                     progressDialog.dismiss();
                                     noRecordMessageTV.setVisibility(View.GONE);
                                     L.printInfo("PDF.......... " + response.toString());
                                     try {
                                         JSONArray parseJson =  XMLParser.parseHtmlToJSON2(response);
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
                                         noRecordMessageTV.setVisibility(View.VISIBLE);

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

    @OnClick(R.id.uploadPhotos)
    public void uploadPDF()
    {
        Intent intent = new Intent(context, UploadPDFActivity.class);
        startUploadPDF.launch(intent);
    }

    public void openPDF(String url)
    {
        Uri path = null;
        try {
            path = Uri.parse(ServerConfig.PDF_URL + selectedSite.id + "/"
                    +  selectedUserId + "/" + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try
        {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText( context, "NO Pdf Viewer", Toast.LENGTH_SHORT).show();
        }
    }

    private void CopyAssetsbrochure() {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try
        {
            files = assetManager.list("");
        }
        catch (IOException e)
        {
            Log.e("tag", e.getMessage());
        }
        for(int i=0; i<files.length; i++)
        {
            String fStr = files[i];
            if(fStr.equalsIgnoreCase("abc.pdf"))
            {
                InputStream in = null;
                OutputStream out = null;
                try
                {
                    in = assetManager.open(files[i]);
                    out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + files[i]);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                    break;
                }
                catch(Exception e)
                {
                    Log.e("tag", e.getMessage());
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    ActivityResultLauncher<Intent> startUploadPDF = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    getGalleryFolders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

    @Override
    public void onSiteChange(Site site) {
        try {
            if(currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT))
            {

            }else
            {
                selectedSite = sessionManager.getSelectedSite();
                getGalleryFolders();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSiteRemainSame(Site site) {

    }

    @Override
    public void onLocationChange(LatLng latLng) {

    }

    @Override
    public void onPointInPolygon(boolean isPointIn) {
        currentPointPolygonStatus = isPointIn;
        if(currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT))
        {

        }else
        {
            updateData();
        }
    }


    private void loadSite() {
        sitesArrayList = new ArrayList<>();
        if (currentUser.sites != null) {
            for (Site si: currentUser.sites) {
                if(si.id != 0)
                {
                    sitesArrayList.add(si);
                }
            }
        }
        if (sitesArrayList != null && sitesArrayList.size() > 0) {
            try {
                Site dataModel = sitesArrayList.get(0);
                //    sessionManager.saveSelectedSite(dataModel);
                siteSpinner.setText(dataModel.name);
                selectedSite = dataModel;
                getUsers(dataModel.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        siteSpinner.setOnClickListener(v -> {
            if (sitesArrayList.size() > 1) {
                showSiteSelectionDialog();
            }
        });
        //    spinner1.setText("Site One");
    }

    private void loadUser() {
        if (usersArrayList != null && usersArrayList.size() > 0) {
            int index = 0;
            for (int i = 0; i < usersArrayList.size(); i++) {
                if (usersArrayList.get(i).id == Integer.parseInt(currentUser.userID)) {
                    index = i;
                }
            }

            UserItem dataModel = usersArrayList.get(index);
            userSpinner.setText(dataModel.name);
            selectedUserId = String.valueOf(dataModel.id);
            selectedUserName = dataModel.name;
            getGalleryFolders();

        }

        userSpinner.setOnClickListener(v -> {
            if (usersArrayList.size() > 1) {
                showUserSelectionDialog();
            }
        });
        //    spinner1.setText("Site One");
    }

    public void showSiteSelectionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.site_selection_dailog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setView(customLayout);

        ListView listView = customLayout.findViewById(R.id.list);
        SiteSpinnerAdapter adapter = new SiteSpinnerAdapter(context, sitesArrayList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Site dataModel = sitesArrayList.get(position);
                    //    sessionManager.saveSelectedSite(dataModel);
                    siteSpinner.setText(dataModel.name);
                    selectedSite = dataModel;
                    getGalleryFolders();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showUserSelectionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.user_list_dailog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setView(customLayout);

        ListView listView = customLayout.findViewById(R.id.list);
        UserSpinnerAdapter adapter = new UserSpinnerAdapter(context, usersArrayList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserItem dataModel = usersArrayList.get(position);
                userSpinner.setText(dataModel.name);
                selectedUserId = String.valueOf(dataModel.id);
                selectedUserName = dataModel.name;
                getGalleryFolders();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*
     * Load Users Filter Dropdown based on Site
     * */
    private void getUsers(int siteid){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteID", siteid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.Load_Users_By_Site_URL)
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
                                            usersArrayList = new ArrayList<>();
                                            ArrayList<UserItem>   sitesArrayList = om.readValue(response.toString(), new TypeReference<List<UserItem>>(){});
                                            usersArrayList.addAll(sitesArrayList);
                                            loadUser();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        L.printError(anError.toString());
                                    }
                                }
                );

    }
}