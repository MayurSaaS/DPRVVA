package com.vvautotest.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.vvautotest.MainActivity;
import com.vvautotest.adapter.SideMenuAdapter;
import com.vvautotest.adapter.SiteSpinnerAdapter;
import com.vvautotest.adapter.SpinnerAdapter;
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
import com.vvautotest.location.LocationTrack;
import com.vvautotest.model.MenuData;
import com.vvautotest.model.SpinnerData;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.DummyData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements LocationListener {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    @BindView(R.id.titleTV)
    TextView titleTV;
    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<MenuData> MenuDataArrayList;
    SideMenuAdapter sideMenuAdapter;


    boolean doubleBackToExitPressedOnce = false;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    private LocationManager locationManager;
    private String provider;

    ArrayList<SpinnerData> categoryDataArrayList;

    @BindView(R.id.spinner1)
    TextView spinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();
        loadSpinner();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setTitle(AppUtils.Menu.HOME);
        displayFragment(AppUtils.AppRoute.ROUTE_HOME);
        closeDrawer();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            else {
                startLocationTrack();
            }
        }
        loadSideMenu();
        showSiteSelectionDialog();
    }

    private void loadSideMenu()
    {
        MenuDataArrayList = DummyData.loadMenu(this);
        sideMenuAdapter = new SideMenuAdapter(this, MenuDataArrayList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(sideMenuAdapter);
        sideMenuAdapter.setOnVideoClickListener(new SideMenuAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(int position, View v) {
                MenuData data = MenuDataArrayList.get(position);
                displayFragment(data.getAction());
            }
        });
    }

    private void loadSpinner()
    {
        categoryDataArrayList = DummyData.loadSite();
        spinner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSiteSelectionDialog();
            }
        });
        spinner1.setText("Site One");
    }

    public void showSiteSelectionDialog(){
        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.site_selection_dailog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setView(customLayout);
        TextView site1 = customLayout.findViewById(R.id.site1);
        TextView site2 = customLayout.findViewById(R.id.site2);
        TextView site3 = customLayout.findViewById(R.id.site3);

        site1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.setText("Site One");
                dialog.dismiss();
            }
        });
        site2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.setText("Site Two");
                dialog.dismiss();
            }
        });
        site3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.setText("Site Three");
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void init() {

    }

    public void displayFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case AppUtils.AppRoute.ROUTE_HOME:
                setTitle(getResources().getString(R.string.ad_menu_1));
                fragment = new HomeFragment();
                break;
            case AppUtils.AppRoute.ROUTE_PHOTOS:
                setTitle(getResources().getString(R.string.ad_menu_2));
                fragment = new PhotosFragment();
                break;
            case AppUtils.AppRoute.ROUTE_DPR_DWR_PDF:
                setTitle(getResources().getString(R.string.ad_menu_3));
                fragment = new DPRDWRFragment();
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
                setTitle(getResources().getString(R.string.ad_menu_7));
                fragment = new HomeFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).
                    addToBackStack(null).commit();
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


    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    startLocationTrack();
                }
                break;
        }
    }

    public void startLocationTrack() {
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
//provider = LocationManager.GPS_PROVIDER; // We want to use the GPS
// Initialize the location fields
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);


/*
        locationTrack = new LocationTrack(HomeActivity.this);
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
        //    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }
*/
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
     //   Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(lat) + "\nLatitude:" + Double.toString(lng), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
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
        }, 2000);
    }
}