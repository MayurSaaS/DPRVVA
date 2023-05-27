package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.vvautotest.R;
import com.vvautotest.adapter.SpinnerAdapter;
import com.vvautotest.model.SpinnerData;
import com.vvautotest.utils.DummyData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMWHIDEntryActivity extends AppCompatActivity {


    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;

    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;

    SpinnerAdapter categoryAdapter, categoryAdapter2;
    ArrayList<SpinnerData> categoryDataArrayList, categoryDataArrayList2;
    int categorySelectedId = -1;
    int categorySelectedId2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwhidentry);
        ButterKnife.bind(this);
        loadSpinner();
        loadSpinner2();
    }

    @OnClick(R.id.navigationIcon)
    public void setupToolbar(){
        navigationIcon.setOnClickListener(v -> onBackPressed());
    }

    private void loadSpinner()
    {
        categoryDataArrayList = DummyData.loadMDStockCategory();
        categoryAdapter = new SpinnerAdapter(AddMWHIDEntryActivity.this, categoryDataArrayList);
        spinner1.setAdapter(categoryAdapter);
        spinner1.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SpinnerData clickedItem = (SpinnerData) parent.getItemAtPosition(position);
                        String name = clickedItem.getName();
                        categorySelectedId = clickedItem.getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void loadSpinner2()
    {
        categoryDataArrayList2 = DummyData.loadMSNAMECategory();
        categoryAdapter2 = new SpinnerAdapter(AddMWHIDEntryActivity.this, categoryDataArrayList2);
        spinner2.setAdapter(categoryAdapter2);
        spinner2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        SpinnerData clickedItem = (SpinnerData) parent.getItemAtPosition(position);
                        String name = clickedItem.getName();
                        categorySelectedId2 = clickedItem.getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

}