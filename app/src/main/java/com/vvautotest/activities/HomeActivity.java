package com.vvautotest.activities;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
import com.vvautotest.GeofenceService;
import com.vvautotest.adapter.SideMenuAdapter;
import com.vvautotest.adapter.SiteSpinnerAdapter;
import com.vvautotest.fragments.ClosingStockFragment;
import com.vvautotest.fragments.DMCEntryFragment;
import com.vvautotest.fragments.DPRDWRFragment;
import com.vvautotest.fragments.FundRequestFragment;
import com.vvautotest.fragments.HomeFragment;
import com.vvautotest.R;
import com.vvautotest.fragments.LeaveApplicationFragment;
import com.vvautotest.fragments.MWHIDEntryFragment;
import com.vvautotest.fragments.MainDPRFragment;
import com.vvautotest.fragments.ManPowerEntryFregment;
import com.vvautotest.fragments.OpeningStockFragment;
import com.vvautotest.fragments.PhotosFragment;
import com.vvautotest.fragments.PreSaleSiteSurwayFragment;
import com.vvautotest.fragments.ProgressEntryFragment;
import com.vvautotest.fragments.ReceiptStockFragment;
import com.vvautotest.fragments.UploadGalleryFragment;
import com.vvautotest.location.LocationTrack;
import com.vvautotest.model.Action;
import com.vvautotest.model.MenuData;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.reciever.GeofenceBroadcastReceiver;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.DummyData;
import com.vvautotest.utils.GeofenceManager;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements BaseActivity.BaseClassListener {


    public static final String GEOFENCE_ID = "RLOGICAL";
    private boolean isMonitoring = false;

    public HashMap<String, LatLng> LOCATION_LIST = new HashMap<String, LatLng>();

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    @BindView(R.id.titleTV)
    TextView titleTV;
    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;


    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;
    LinearLayoutManager linearLayoutManager;
    SideMenuAdapter sideMenuAdapter;
    List<MenuData> headerList = new ArrayList<>();
    HashMap<MenuData, List<MenuData>> childList = new HashMap<>();

    boolean doubleBackToExitPressedOnce = false;


    LocationTrack locationTrack;



    ArrayList<Site> sitesArrayList;

    @BindView(R.id.spinner1)
    TextView spinner1;
    @BindView(R.id.nameTV)
    TextView nameTV;

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;
    @BindView(R.id.latlongTV)
    TextView latlongTV;

    ArrayList<LatLng> polyLatLng;
    GeofenceBroadcastReceiver geofenceBroadcastReceiver;
    HomePageActionsListenr homePageActionsListenr;

    ProgressDialog progressDialog;
    LatLng currentLatLong;
    boolean isLocationLoad = true;
    boolean currentPointPolygonStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetails();
        setBaseListener(this);
        init();
        updateUserView();
        loadSpinner();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Location Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getLastUpdatedLocation();
        //    getSites();
    }

    public void loadSites(){
        if (currentUser.sites != null) {
            sitesArrayList.addAll(currentUser.sites);
        }
    }

    private void setHomePage(){
        displayFragment(AppUtils.AppRoute.ROUTE_HOME, AppUtils.Menu.HOME);
    }

    private void populateExpandableList() {
        sideMenuAdapter = new SideMenuAdapter(this, headerList, childList);
        expandableListView.setAdapter(sideMenuAdapter);

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (headerList.get(groupPosition).isGroup) {
                if (!headerList.get(groupPosition).hasChildren) {
                    displayFragment(headerList.get(groupPosition).getId(), "Logout");
                }
            }
            for (int i = 0; i < headerList.size(); i++) {
                MenuData menu = headerList.get(i);
                if (headerList.get(groupPosition).name.equalsIgnoreCase(menu.name)) {
                    headerList.get(groupPosition).setSelected(true);
                } else {
                    headerList.get(i).setSelected(false);
                }
            }
            sideMenuAdapter.updateData(headerList);
            //  menuSelection(getTitle(1));
            return false;
        });

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (childList.get(headerList.get(groupPosition)) != null) {
                MenuData model = childList.get(headerList.get(groupPosition)).get(childPosition);
                getAction(model.getId(), model.subName);
            //    displayFragment(model.getId(), model.subName);
                //    AppUtils.showToast(HomeActivity.this, model.subName + " " + model.detailName);
            }
            return false;
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = sideMenuAdapter.getGroupCount();

                for (int i = 0; i < len; i++) {
                    if (i != groupPosition) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
        //    menuSelection("Home");
    }


    private void loadSpinner() {
        sitesArrayList = new ArrayList<>();

        spinner1.setOnClickListener(v -> {
            if (sitesArrayList.size() > 1) {
             //   showSiteSelectionDialog();
            }
        });
        //    spinner1.setText("Site One");
    }

    public void showSiteSelectionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.site_selection_dailog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCancelable(false);
        dialog.setView(customLayout);

        ListView listView = customLayout.findViewById(R.id.list);
        SiteSpinnerAdapter adapter = new SiteSpinnerAdapter(getApplicationContext(), sitesArrayList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* Site dataModel = sitesArrayList.get(position);
                sessionManager.saveSelectedSite(dataModel);
                spinner1.setText(dataModel.name);
                selectedSite = sessionManager.getSelectedSite();
                getMenuItems();
                loadPolygonAndSite();
                if(homePageActionsListenr != null)
                {
                    homePageActionsListenr.onSiteChange(selectedSite);
                }*/
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void init() {

    }

    public void displayFragment(int position, String title) {
        Fragment fragment = null;
        Bundle args = null;
        switch (position) {
            case AppUtils.AppRoute.ROUTE_HOME:
                setTitle(getResources().getString(R.string.ad_menu_1));
                fragment = new HomeFragment();
                break;
            case AppUtils.AppRoute.ROUTE_PHOTOS:
                setTitle(getResources().getString(R.string.ad_menu_2));
                args = new Bundle();
                args.putString("menuId", String.valueOf(position));
                args.putBoolean("currentPointPolygonStatus",    currentPointPolygonStatus);
                fragment = new PhotosFragment();
                fragment.setArguments(args);
                break;
            case AppUtils.AppRoute.ROUTE_UPLOAD_DOCUMENT:
                setTitle(getResources().getString(R.string.ad_menu_8));
                args = new Bundle();
                args.putString("menuId", String.valueOf(position));
                args.putBoolean("currentPointPolygonStatus",    currentPointPolygonStatus);
                fragment = new UploadGalleryFragment();
                fragment.setArguments(args);
                break;
            case AppUtils.AppRoute.ROUTE_DPR_DWR_PDF:
                setTitle(getResources().getString(R.string.ad_menu_3));
                args = new Bundle();
                args.putBoolean("currentPointPolygonStatus",    currentPointPolygonStatus);
                args.putString("menuId", String.valueOf(position));
                fragment = new DPRDWRFragment();
                fragment.setArguments(args);
                break;
            case AppUtils.AppRoute.ROUTE_MANPOWER_ENTRY:
                setTitle(getResources().getString(R.string.ad_menu_4));
                fragment = new ManPowerEntryFregment();
                break;
            case AppUtils.AppRoute.ROUTE_PROGRESS_ENTRY:
                setTitle(getResources().getString(R.string.ad_menu_5));
                fragment = new ProgressEntryFragment();
                break;
            case AppUtils.AppRoute.ROUTE_DIRECT_MATERIAL:
                setTitle(getResources().getString(R.string.ad_menu_6));
                fragment = new DMCEntryFragment();
                break;
            case AppUtils.AppRoute.ROUTE_MAIN_DPR:
                setTitle(AppUtils.Menu.MAIN_DPR);
                fragment = new MainDPRFragment();
                break;
            case AppUtils.AppRoute.ROUTE_OPENING_STOCK:
                setTitle(AppUtils.Menu.OPENING_STOCK);
                fragment = new OpeningStockFragment();
                break;
            case AppUtils.AppRoute.ROUTE_RECEIPT_STOCK:
                setTitle(AppUtils.Menu.RECEIPT_STOCK);
                fragment = new ReceiptStockFragment();
                break;
            case AppUtils.AppRoute.ROUTE_CLOSING_STOCK:
                setTitle(AppUtils.Menu.CLOSING_STOCK);
                fragment = new ClosingStockFragment();
                break;
            case AppUtils.AppRoute.ROUTE_MW_AND_HSD_ISSUE:
                setTitle(AppUtils.Menu.MW_AND_HSD_ISSUE);
                fragment = new MWHIDEntryFragment();
                break;
            case AppUtils.AppRoute.ROUTE_LEAVE_APPLICATION:
                setTitle(AppUtils.Menu.LEAVE_APPLICATION);
                fragment = new LeaveApplicationFragment();
                break;
            case AppUtils.AppRoute.ROUTE_FUND_REQUEST:
                setTitle(AppUtils.Menu.FUND_REQUEST);
                fragment = new FundRequestFragment();
                break;
            case AppUtils.AppRoute.ROUTE_PRE_SALE:
                setTitle(AppUtils.Menu.PRE_SALE);
                fragment = new PreSaleSiteSurwayFragment();
                break;
            case AppUtils.AppRoute.ROUTE_LOGOUT:
                sessionManager.logout();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, title).
                    addToBackStack(title).commit();
            closeDrawer();
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @OnClick(R.id.navigationIcon)
    public void openDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void setTitle(String title) {
        titleTV.setText(title);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                int fragments = getSupportFragmentManager().getBackStackEntryCount();
                if (fragments == 1) {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                } else if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                    String title = getSupportFragmentManager().getBackStackEntryAt(fragments-2).getName();
                    setTitle(title);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);*/
    }

    public void updateUserView()
    {
        nameTV.setText(currentUser.fName + " " + currentUser.mName + " " +  currentUser.lName);
    }

   /* private void getSites(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();

        AndroidNetworking.post(ServerConfig.Site_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Sites")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                     @Override
                                     public void onResponse(JSONArray response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             sitesArrayList = new ArrayList<>();
                                             sitesArrayList = om.readValue(response.toString(), new TypeReference<List<Site>>(){});
                                             showSiteSelectionDialog();
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }
                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         L.printError(anError.toString());
                                         progressDialog.dismiss();
                                    }
                                 }
                );

    }*/
    private void getMenuItems(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", currentUser.userID);
            jsonObject.put("siteID", selectedSite.id);
        }catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.Menus_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Menus")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                     @Override
                                     public void onResponse(JSONArray response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         parseSideBarMenu(response);
                                         populateExpandableList();
                                     }
                                     @Override
                                     public void onError(ANError anError) {
                                         L.printError(anError.toString());
                                         progressDialog.dismiss();
                                    }
                                 }
                );

    }

    public void parseSideBarMenu(JSONArray jsonArray)
    {
        try {
                    if (jsonArray.length() > 0) {
                        ArrayList<MenuData> tempMenu = new ArrayList<>();
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            String name = "";
                            String subName = "";
                            String detailName = "";
                            String menuIcon = "";
                            String subMenuIcon = "";

                            int id = 0;
                            JSONObject objectInArray = jsonArray.getJSONObject(i);
                            if (objectInArray.has("name")) {
                                name = objectInArray.getString("name");
                            }
                            if (objectInArray.has("subName")) {
                                subName = objectInArray.getString("subName");
                            }
                            if (objectInArray.has("detailName")) {
                                detailName = objectInArray.getString("detailName");
                            }
                            if (objectInArray.has("menuIcon")) {
                                menuIcon = objectInArray.getString("menuIcon");
                            }if (objectInArray.has("subMenuIcon")) {
                                subMenuIcon = objectInArray.getString("subMenuIcon");
                            }
                            if (objectInArray.has("id")) {
                                String text = objectInArray.getString("id");
                                if(!"".equalsIgnoreCase(text))
                                { id = Integer.parseInt(text);}
                            }
                            tempMenu.add(new MenuData(id, name,
                                    subName, detailName, false, true, false, menuIcon, subMenuIcon));
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            Map<String, List<MenuData>> studlistGrouped =
                                    tempMenu.stream().collect(Collectors.groupingBy(w -> w.name));

                            headerList = new ArrayList<>();
                            childList = new HashMap<>();
                            int count = 0;
                            for (Map.Entry<String,List<MenuData>> entry : studlistGrouped.entrySet())
                            {
                                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                                List<MenuData> list = entry.getValue();
                                MenuData head = new MenuData(count +1, entry.getKey() ,  ""
                                        , "", true, true, false, list.get(0).menuIcon, list.get(0).subMenuIcon);
                                headerList.add(head);
                                childList.put(head, list);
                            }

                            MenuData upload = new MenuData(AppUtils.AppRoute.ROUTE_UPLOAD_DOCUMENT, "Documents" ,  ""
                                    , "", false,  true, false, "upload", "upload");
                            headerList.add(upload);
                            childList.put(upload, null);
                            //Add Logout
                            MenuData head = new MenuData(14, "Logout" ,  ""
                                    , "", false,  true, false, "logout", "logout");
                            headerList.add(head);
                            childList.put(head, null);
                        }
                        }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setHomePageActionsListenr(HomePageActionsListenr homePageActionsListenr){
        this.homePageActionsListenr = homePageActionsListenr;
    }

    @Override
    public void onLocationChange(LatLng latLng) {
        currentLatLong = latLng;
        if(isLocationLoad)
        {
            if(progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
            loadSites();
            setHomePage();
            checkPolygonAndAutoSelectSite();
            isLocationLoad = false;
        }else
        {
            checkPolygonAndAutoSelectSite();
        }
        if(homePageActionsListenr != null)
        {
            homePageActionsListenr.onLocationChange(latLng);
        }
    }

    public void checkPolygonAndAutoSelectSite(){
        if(sitesArrayList != null && sitesArrayList.size() > 0)
        {
            int index = -1;
            for (int j = 0;  j< sitesArrayList.size(); j++) {
                Site site = sitesArrayList.get(j);
                if(site != null)
                {
                    if(site.geoFenceApp != null)
                    {
                        String strLatLng = site.geoFenceApp;
                        ArrayList<LatLng> polyLatLng = new ArrayList<>();
                        if (!"".equals(strLatLng))
                        {
                            try {
                                String parts[] = strLatLng.trim().split("\n");
                                for(String part: parts) {
                                    if(!part.contains("#"))
                                    {
                                        String latLong[] = part.trim().split(",");
                                        if(latLong.length == 2)
                                        {
                                            polyLatLng.add( new LatLng( Double.parseDouble(latLong[0]),Double.parseDouble(latLong[1])) ); // Should match last point
                                        }
                                    }
                                }
                                boolean contains1 = PolyUtil.containsLocation(currentLatLong.latitude, currentLatLong.longitude, polyLatLng, true);
                                if(contains1)
                                {
                                    index = j;
                                    currentPointPolygonStatus = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if(index == -1)
            {
                currentPointPolygonStatus = false;
                Site dataModel = sitesArrayList.get(0);
                if(selectedSite == null)
                {
                    sessionManager.saveSelectedSite(dataModel);
                    spinner1.setText(dataModel.name);
                    selectedSite = sessionManager.getSelectedSite();
                    getMenuItems();
                    if(homePageActionsListenr != null)
                    {
                        homePageActionsListenr.onSiteChange(selectedSite);
                    }
                }else {
                    if(!dataModel.name.equalsIgnoreCase(selectedSite.name))
                    {
                        sessionManager.saveSelectedSite(dataModel);
                        spinner1.setText(dataModel.name);
                        selectedSite = sessionManager.getSelectedSite();
                        getMenuItems();
                        if(homePageActionsListenr != null)
                        {
                            homePageActionsListenr.onSiteChange(selectedSite);
                        }
                    }else
                    {
                        if(homePageActionsListenr != null)
                        {
                            homePageActionsListenr.onSiteRemainSame(selectedSite);
                        }
                   /* if(homePageActionsListenr != null)
                    {
                        homePageActionsListenr.onSiteChange(selectedSite);
                    }*/
                    }
                }
                if(homePageActionsListenr != null)
                {
                    homePageActionsListenr.onPointInPolygon(currentPointPolygonStatus);
                }

            }else
            {
                Site dataModel = sitesArrayList.get(index);
                if(selectedSite == null)
                {
                    sessionManager.saveSelectedSite(dataModel);
                    spinner1.setText(dataModel.name);
                    selectedSite = sessionManager.getSelectedSite();
                    getMenuItems();
                    if(homePageActionsListenr != null)
                    {
                        homePageActionsListenr.onSiteChange(selectedSite);
                    }
                }else {
                    if(!dataModel.name.equalsIgnoreCase(selectedSite.name))
                    {
                        sessionManager.saveSelectedSite(dataModel);
                        spinner1.setText(dataModel.name);
                        selectedSite = sessionManager.getSelectedSite();
                        getMenuItems();
                        if(homePageActionsListenr != null)
                        {
                            homePageActionsListenr.onSiteChange(selectedSite);
                        }
                    }else
                    {
                        if(homePageActionsListenr != null)
                        {
                            homePageActionsListenr.onSiteRemainSame(selectedSite);
                        }
                   /* if(homePageActionsListenr != null)
                    {
                        homePageActionsListenr.onSiteChange(selectedSite);
                    }*/
                    }
                }
                if(homePageActionsListenr != null)
                {
                    homePageActionsListenr.onPointInPolygon(currentPointPolygonStatus);
                }


            }

        }
    }


    public void loadPolygonAndSite(){

        int index = 0;
        for (int j = 0;  j< sitesArrayList.size(); j++) {
            Site site = sitesArrayList.get(j);
            if(site != null)
            {
                if(site.geoFenceApp != null)
                {
                    String strLatLng = site.geoFenceApp;
                    ArrayList<LatLng> polyLatLng = new ArrayList<>();
                    if (!"".equals(strLatLng))
                    {
                        try {
                            JSONObject jsonObject = new JSONObject(strLatLng);
                            JSONArray jsonArray = jsonObject.getJSONArray("coordinates");
                            JSONArray jsonArray1 = jsonArray.getJSONArray(0);

                            for (int i = 0; i < jsonArray1.length() ; i++) {
                                JSONArray jsonArray2 = jsonArray1.getJSONArray(i);
                                polyLatLng.add( new LatLng( jsonArray2.getDouble(1), jsonArray2.getDouble(0) ) ); // Should match last point
                            }
                            boolean contains1 = PolyUtil.containsLocation(currentLatLong.latitude, currentLatLong.longitude, polyLatLng, true);
                            if(contains1)
                            {
                                currentPointPolygonStatus = true;
                                index = j;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        Site temp  = sitesArrayList.get(index);
        if(index != 0)
        {
            if(selectedSite.name.equalsIgnoreCase(temp.name))
            {
            }else
            {
                sessionManager.saveSelectedSite(temp);
                spinner1.setText(temp.name);
                selectedSite = sessionManager.getSelectedSite();
                getMenuItems();
            }
            if(homePageActionsListenr != null)
            {
                homePageActionsListenr.onPointInPolygon(true);
            }

        }else
        {

            if(selectedSite.name.equalsIgnoreCase(temp.name))
            {

            }else
            {
                sessionManager.saveSelectedSite(temp);
                spinner1.setText(temp.name);
                selectedSite = sessionManager.getSelectedSite();
                getMenuItems();
            }
            if(homePageActionsListenr != null)
            {
                homePageActionsListenr.onPointInPolygon(currentPointPolygonStatus);
            }
        }
/*
        sessionManager.saveSelectedSite(sitesArrayList.get(index));
        spinner1.setText(sitesArrayList.get(index).name);
        selectedSite = sessionManager.getSelectedSite();
        getMenuItems();
        loadPolygonAndSite();
        if(homePageActionsListenr != null)
        {
            homePageActionsListenr.onSiteChange(selectedSite);
        }

        if(selectedSite != null)
        {
            if(selectedSite.geoFenceApp != null)
            {
                String strLatLng = selectedSite.geoFenceApp;
                ArrayList<LatLng> polyLatLng = new ArrayList<>();
                if (!"".equals(strLatLng))
                {
                    try {
                        JSONObject jsonObject = new JSONObject(strLatLng);
                        JSONArray jsonArray = jsonObject.getJSONArray("coordinates");
                        JSONArray jsonArray1 = jsonArray.getJSONArray(0);

                        for (int i = 0; i < jsonArray1.length() ; i++) {
                            JSONArray jsonArray2 = jsonArray1.getJSONArray(i);
                            polyLatLng.add( new LatLng( jsonArray2.getDouble(1), jsonArray2.getDouble(0) ) ); // Should match last point
                        }
                        boolean contains1 = PolyUtil.containsLocation(currentLatLong.latitude, currentLatLong.longitude, polyLatLng, true);
                        currentPointPolygonStatus = contains1;
                        if(homePageActionsListenr != null)
                        {
                            homePageActionsListenr.onPointInPolygon(contains1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    if(homePageActionsListenr != null) {
                        currentPointPolygonStatus = false;
                        homePageActionsListenr.onPointInPolygon(false);
                    }
                }
            }else
            {
                if(homePageActionsListenr != null) {
                    currentPointPolygonStatus = false;
                    homePageActionsListenr.onPointInPolygon(false);
                }
            }
        }else
        {
            if(homePageActionsListenr != null) {
                currentPointPolygonStatus = false;
                homePageActionsListenr.onPointInPolygon(false);
            }
        }
*/
    }


    public interface HomePageActionsListenr{
        void onSiteChange(Site site);
        void onSiteRemainSame(Site site);
        void onLocationChange(LatLng latLng);
        void onPointInPolygon(boolean isPointIn);
    }


    public void getAction(int menuId, String title) {
        ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", currentUser.userID);
            jsonObject.put("siteID", selectedSite.id);
            jsonObject.put("menuID", menuId);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        AndroidNetworking.post(ServerConfig.Action_Details_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Actions")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                            progressDialog.dismiss();
                                        L.printInfo("Action Details : " + response.toString());
                                        ObjectMapper om = new ObjectMapper();
                                        try {
                                            List<Action> list = om.readValue(response.toString(), new TypeReference<List<Action>>() {});
                                            boolean isViewAllowed = false;
                                            boolean isAddAllowed = false;
                                            boolean isEditAllowed = false;
                                            boolean isDeleteAllowed = false;
                                            boolean isDownloadAllowed = false;
                                            boolean isUploadAllowed = false;
                                            boolean isRequestAllowed = false;
                                            boolean isApproveAllowed = false;
                                            for (Action a: list) {
                                                L.printError("Name : " + a.getName());
                                                if("View".equalsIgnoreCase(a.getName()))
                                                {
                                                    isViewAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isViewAllowed, AppUtils.Action.Action_View);
                                                }else if("Add".equalsIgnoreCase(a.getName()))
                                                {
                                                    isAddAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isAddAllowed, AppUtils.Action.Action_Add);
                                                }else if("Edit".equalsIgnoreCase(a.getName()))
                                                {
                                                    isEditAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isEditAllowed, AppUtils.Action.Action_Edit);
                                                }else if("Delete".equalsIgnoreCase(a.getName()))
                                                {
                                                    isDeleteAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isDeleteAllowed, AppUtils.Action.Action_Delete);
                                                }else if("Download".equalsIgnoreCase(a.getName()))
                                                {
                                                    isDownloadAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isDownloadAllowed, AppUtils.Action.Action_Download);
                                                }else if("Upload".equalsIgnoreCase(a.getName()))
                                                {
                                                    isUploadAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isUploadAllowed, AppUtils.Action.Action_Upload);
                                                }else if("Request".equalsIgnoreCase(a.getName()))
                                                {
                                                    isRequestAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isRequestAllowed, AppUtils.Action.Action_Request);
                                                }else if("Approve".equalsIgnoreCase(a.getName()))
                                                {
                                                    isApproveAllowed = true;
                                                    AppUtils.setActionValueWithKey(HomeActivity.this, isApproveAllowed, AppUtils.Action.Action_Approve);
                                                }
                                            }

                                            if(isViewAllowed)
                                            {
                                                displayFragment(menuId, title);
                                            }else{
                                                AppUtils.showToast(HomeActivity.this, "Not Access to View");
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        L.printError(anError.toString());
                                        progressDialog.dismiss();
                                    }
                                }
                );
    }

}