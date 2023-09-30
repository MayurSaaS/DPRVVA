package com.vvautotest.adapter;

import static com.vvautotest.utils.ServerConfig.Photos_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vvautotest.R;
import com.vvautotest.model.ImageData;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.utils.SessionManager;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {

    private List<ImageData> imageDataList;
    Context activity;
    ClickListener clickListener;

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;
    String folderName;

    public void updateList(List<ImageData> listData) {
        this.imageDataList = listData;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RoundedImageView image;
        TextView nameTV;
        RelativeLayout mainCard;
        public MyViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
            image = view.findViewById(R.id.image);
            nameTV = view.findViewById(R.id.nameTV);
            mainCard =  view.findViewById(R.id.mainCard);
        }

        @Override
        public void onClick(View v) {
            //    clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public FolderAdapter(Context activity, List<ImageData> countryList, String foldername) {
        this.activity = activity;
        this.imageDataList = countryList;
        this.folderName = foldername;
        sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();
        selectedSite = sessionManager.getSelectedSite();
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ImageData c = imageDataList.get(position);
        try {

            holder.nameTV.setText(c.file);
       /*     Glide.with(holder.image).load(Photos_URL +  selectedSite.id + "/"
                            +  currentUser.userID  + "/" + folderName +   "/" +  c.file)
                    .placeholder(activity.getResources().getDrawable(R.drawable.folder)).into(holder.image);
     */       holder.mainCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(position, v);
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_item_row,parent, false);
        return new MyViewHolder(v);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }


}
