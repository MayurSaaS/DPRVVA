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

public class SideMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    OnMenuClickListener onVideoClickListener;
    List<MenuData> videoList;

    public SideMenuAdapter(Activity mContext, List<MenuData> videoList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.side_menu_item_row, parent, false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MenuData data = videoList.get(position);
        ((FileLayoutHolder) holder).title.setText((data.getTitle()));
        if(!"".equals(data.getSubTitle()))
        {
            ((FileLayoutHolder) holder).subtitle.setVisibility(View.VISIBLE);
            ((FileLayoutHolder) holder).subtitle.setText((data.getSubTitle()));
        }else
        {
            ((FileLayoutHolder) holder).subtitle.setVisibility(View.GONE);
        }
        ((FileLayoutHolder) holder).title.setText((data.getTitle()));
        ((FileLayoutHolder) holder).image.setImageDrawable((data.getImage()));
        ((FileLayoutHolder) holder).mainCard.setOnClickListener(v -> { onVideoClickListener.onMenuClick(position, v);});
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title , subtitle;
        ImageView image;
        CardView mainCard;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mainCard = itemView.findViewById(R.id.mainCard);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            image = itemView.findViewById(R.id.image);

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