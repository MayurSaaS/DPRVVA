package com.vvautotest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.vvautotest.R;
import com.vvautotest.model.SpinnerData;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerData> {

    public SpinnerAdapter(Context context, ArrayList<SpinnerData> algorithmList)
    {
        super(context, 0, algorithmList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_row, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.text_view);
        SpinnerData currentItem = getItem(position);
        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
        }
        return convertView;
    }
}
