package com.vvautotest.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vvautotest.R;
import com.vvautotest.model.MenuData;

import java.util.List;

public class HomeMenuAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    OnMenuClickListener onVideoClickListener;
    List<MenuData> videoList;

    public HomeMenuAdapter(Activity mContext, List<MenuData> videoList) {
        this.mContext = mContext;
        this.videoList = videoList;
    }

    public void updateList(List<MenuData> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_menu_item_row, parent, false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MenuData data = videoList.get(position);
        ((FileLayoutHolder) holder).title.setText((data.name));
    //    ((FileLayoutHolder) holder).image.setImageDrawable((data.getImage()));
        /*if(position%2 == 0)
        {
            ((FileLayoutHolder) holder).mainCard.setBackground(mContext.getResources().getDrawable(R.drawable.home_menu_design_one));
            setMargins(((FileLayoutHolder) holder).mainCard, 0, 8, 5, 8);
            ((FileLayoutHolder) holder).leftView.setVisibility(View.VISIBLE);
            ((FileLayoutHolder) holder).rightView.setVisibility(View.GONE);
        }else
        {
            ((FileLayoutHolder) holder).mainCard.setBackground(mContext.getResources().getDrawable(R.drawable.home_menu_design_two));
            setMargins(((FileLayoutHolder) holder).mainCard, 5, 8, 0, 8);
            ((FileLayoutHolder) holder).leftView.setVisibility(View.GONE);
            ((FileLayoutHolder) holder).rightView.setVisibility(View.VISIBLE);
        }*/
        ((FileLayoutHolder) holder).mainCard.setOnClickListener(v -> { onVideoClickListener.onMenuClick(position, v);});
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title ;
        ImageView image;
        CardView mainCard;
        View leftView, rightView;


        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mainCard = itemView.findViewById(R.id.mainCard);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);/*
            leftView = itemView.findViewById(R.id.leftView);
            rightView = itemView.findViewById(R.id.rightView);*/

        }

        @Override
        public void onClick(View v) {
            //   onVideoClickListener.onVideoClick(getAdapterPosition(), v);
        }
    }

    public void setOnVideoClickListener(OnMenuClickListener onVideoClickListener) {
        this.onVideoClickListener = onVideoClickListener;
    }

    public interface OnMenuClickListener {
        void onMenuClick(int position, View v);
    }

}