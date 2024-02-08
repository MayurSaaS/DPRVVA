package com.vvautotest.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vvautotest.R;
import com.vvautotest.model.MenuData;

import java.util.HashMap;
import java.util.List;

public class SideMenuAdapter  extends BaseExpandableListAdapter {
    private Context context;
    private List<MenuData> listDataHeader;
    private HashMap<MenuData, List<MenuData>> listDataChild;

    public SideMenuAdapter(Context context, List<MenuData> listDataHeader,
                                        HashMap<MenuData, List<MenuData>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public MenuData getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        final String childText = getChild(groupPosition, childPosition).subName;
        final String detailed = getChild(groupPosition, childPosition).detailName;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_child, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        TextView detailedTV = convertView.findViewById(R.id.detailedTV);

        txtListChild.setText(childText);
        ImageView menuImage = convertView.findViewById(R.id.menuImage);
        try {
            Glide.with(menuImage).load("http://"+ getChild(groupPosition, childPosition).subMenuIcon).into(menuImage);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if("".equals(detailed) || "null".equals(detailed))
        {
            detailedTV.setVisibility(View.GONE);
        }else
        {
            detailedTV.setVisibility(View.VISIBLE);
            detailedTV.setText("(" + detailed + ")");
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null)
            return 0;
        else
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
    }

    @Override
    public MenuData getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).name;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_header, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        ImageView iconExp = convertView.findViewById(R.id.iconExp);
        ImageView menuImage = convertView.findViewById(R.id.menuImage);

        //    lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        try {
            if("logout".equals(getGroup(groupPosition).menuIcon))
            {
                menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.logout));
            }else if("upload".equals(getGroup(groupPosition).menuIcon))
            {
                menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.upload));
            }else
            {
                Glide.with(menuImage).load("http://" + getGroup(groupPosition).menuIcon).into(menuImage);
            }
        //    Glide.with(menuImage).load("http://" + getGroup(groupPosition).menuIcon).into(menuImage);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

     /*   convertView.setBackgroundColor(getGroup(groupPosition).isSelected ? context.getResources().getColor(R.color.colorPrimary)
                : context.getResources().getColor(R.color.form_label_color));
*/
        if(getGroup(groupPosition).hasChildren)
        {
            iconExp.setVisibility(View.VISIBLE);
            iconExp.setBackgroundResource(isExpanded ? R.drawable.minus_sign : R.drawable.plus);
        }else
        {
            iconExp.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void updateData(List<MenuData> listDataHeader)
    {
        this.listDataHeader = listDataHeader;
        notifyDataSetChanged();
    }


}