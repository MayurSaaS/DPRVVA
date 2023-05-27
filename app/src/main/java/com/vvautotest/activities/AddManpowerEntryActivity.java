package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.media.Image;
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

public class AddManpowerEntryActivity extends AppCompatActivity {

    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;

    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    SpinnerAdapter categoryAdapter;
    ArrayList<SpinnerData> categoryDataArrayList;

    int categorySelectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manpower_entry);
        ButterKnife.bind(this);
        setupToolbar();
        loadSpinner();
    }

    private void loadSpinner()
    {
        categoryDataArrayList = DummyData.loadCategory();
        categoryAdapter = new SpinnerAdapter(AddManpowerEntryActivity.this, categoryDataArrayList);
        spinner1.setAdapter(categoryAdapter);
        spinner1.setOnItemSelectedListener(
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

        spinner2.setAdapter(categoryAdapter);
        spinner2.setOnItemSelectedListener(
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