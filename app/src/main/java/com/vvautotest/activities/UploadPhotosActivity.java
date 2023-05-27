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

public class UploadPhotosActivity extends AppCompatActivity {

    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;
    SpinnerAdapter categoryAdapter;
    ArrayList<SpinnerData> categoryDataArrayList;
    int categorySelectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photos);
        ButterKnife.bind(this);
        setupToolbar();
        loadSpinner();
    }

    private void loadSpinner()
    {
        categoryDataArrayList = DummyData.loadCategory();
        categoryAdapter = new SpinnerAdapter(UploadPhotosActivity.this, categoryDataArrayList);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        SpinnerData clickedItem = (SpinnerData) parent.getItemAtPosition(position);
                        String name = clickedItem.getName();
                        categorySelectedId = clickedItem.getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

    }

    private void setupToolbar(){
        navigationIcon.setOnClickListener(v -> onBackPressed());
    }
}