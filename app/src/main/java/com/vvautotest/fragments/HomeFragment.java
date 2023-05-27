package com.vvautotest.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.adapter.HomeMenuAdapter;
import com.vvautotest.adapter.SideMenuAdapter;
import com.vvautotest.model.MenuData;
import com.vvautotest.utils.DummyData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ArrayList<MenuData> sideMenuDataArrayList;
    HomeMenuAdapter sideMenuAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        loadMenu(rootView);
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
}