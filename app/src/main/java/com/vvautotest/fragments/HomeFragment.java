package com.vvautotest.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.vvautotest.R;
import com.vvautotest.activities.HomeActivity;
import com.vvautotest.adapter.HomeMenuAdapter;
import com.vvautotest.adapter.SideMenuAdapter;
import com.vvautotest.model.MenuData;
import com.vvautotest.model.Site;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.DummyData;
import com.vvautotest.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback , HomeActivity.HomePageActionsListenr {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ArrayList<MenuData> sideMenuDataArrayList;
    HomeMenuAdapter sideMenuAdapter;
    private GoogleMap mMap;
    private LatLngBounds bounds;
    ArrayList<LatLng> polyLatLng;
    Marker mCurrLocationMarker;

    @BindView(R.id.locationTextMessage)
    TextView locationTextMessage;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity)getActivity()).setHomePageActionsListenr(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    //    loadMenu(rootView);
        return rootView;
    }

    private void loadMenu(View rootView)
    {
        sideMenuDataArrayList = DummyData.loadHomeMenu(getActivity());
        sideMenuAdapter = new HomeMenuAdapter(getActivity(), sideMenuDataArrayList);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(sideMenuAdapter);
        sideMenuAdapter.setOnVideoClickListener((position, v) -> {
            MenuData data = sideMenuDataArrayList.get(position);
        //    displayFragment(data.getAction());
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLoadedCallback(this);

        try {
            if(!"0".equals(AppUtils.getLatitude(getActivity())))
            {
                LatLng latLng = new LatLng(Double.parseDouble(AppUtils.getLatitude(getActivity())),
                        Double.parseDouble(AppUtils.getLongitude(getActivity())));
                updateMap(latLng);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLoaded() {

    }
    @Override
    public void onSiteChange(Site site) {
        createPolygon(site);
    }

    @Override
    public void onSiteRemainSame(Site site) {
        createPolygon(site);
    }

    @Override
    public void onLocationChange(LatLng latLng) {
        updateMap(latLng);
    }

    @Override
    public void onPointInPolygon(boolean isPointIn) {
        try{
            if(isPointIn)
            {
                locationTextMessage.setText("You are inside of Coordinate");
            }else
            {
                locationTextMessage.setText("You are outside of Coordinate");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updateMap(LatLng lll){
        try {
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            //Place current location marker
            LatLng latLng = new LatLng(lll.latitude, lll.longitude);
        //    LatLng latLng = new LatLng(Double.parseDouble("26.01741652199599"), Double.parseDouble("82.85892059082875"));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            //move map camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
            //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));
    }

    private void createPolygon(Site site){
        polyLatLng = new ArrayList<>();
        if(mMap != null)
        {
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
                            mMap.clear();
                            if(polyLatLng.size() > 0)
                            {
                            //    polyLatLng.add(new LatLng(polyLatLng.get(0).latitude, polyLatLng.get(0).longitude));
                                mMap.addPolygon(new PolygonOptions().addAll(polyLatLng).strokeColor(Color.BLUE));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else
                    {

                    }
                }else
                {
                    if(mMap != null)
                    {
                        mMap.clear();
                        locationTextMessage.setText("Site Coordinate Not Exist");
                    }
                }
            }else
            {
                if(mMap != null)
                {
                    mMap.clear();
                    locationTextMessage.setText("Site Coordinate Not Exist");
                }
            }
        }



/*
        if(mMap != null)
        {
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
                            mMap.clear();
                            mMap.addPolygon(new PolygonOptions().addAll(polyLatLng).strokeColor(Color.BLUE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else
                    {

                    }
                }else
                {
                    if(mMap != null)
                    {
                        mMap.clear();
                        locationTextMessage.setText("Site Coordinate Not Exist");
                    }
                }
            }else
            {
                if(mMap != null)
                {
                    mMap.clear();
                    locationTextMessage.setText("Site Coordinate Not Exist");
                }
            }
        }
*/
    }

}