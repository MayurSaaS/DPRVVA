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
import com.vvautotest.model.UserItem;

import java.util.ArrayList;

public class UserSpinnerAdapter extends ArrayAdapter<UserItem> {

    public UserSpinnerAdapter(Context context, ArrayList<UserItem> algorithmList)
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_spinner_item_row, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.text_view);
        UserItem currentItem = getItem(position);
        if (currentItem != null) {
            textViewName.setText(currentItem.name);
        }
        return convertView;
    }
}

