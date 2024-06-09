package com.vvautotest.adapter;

import static com.vvautotest.utils.ServerConfig.My_Document_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class DocumentGalleryAdapter  extends RecyclerView.Adapter<DocumentGalleryAdapter.MyViewHolder> {

    private List<ImageData> imageDataList;
    Context activity;
    ClickListener clickListener;

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;
    String folderName;
    String dirpath = "";
    String selectedUserId = "";

    public void updateList(List<ImageData> listData, String dirpath) {
        this.imageDataList = listData;
        this.dirpath = dirpath;
        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RoundedImageView image;
        TextView nameTV;
        LinearLayout mainCard;
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

    public DocumentGalleryAdapter(Context activity, List<ImageData> countryList, String foldername) {
        this.activity = activity;
        this.imageDataList = countryList;
        this.folderName = foldername;
        sessionManager = new SessionManager(activity);
        currentUser = sessionManager.getUserDetails();
        selectedSite = sessionManager.getSelectedSite();
        selectedUserId = currentUser.userID;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ImageData c = imageDataList.get(position);
        try {

            holder.nameTV.setText(c.file);
            if(c.file.contains(".jpg") || c.file.contains(".png") ||
                    c.file.contains(".jpeg"))
            {
                Glide.with(holder.image).load(My_Document_URL + selectedUserId + "/" +
                        dirpath + c.file)
                        .placeholder(activity.getResources().getDrawable(R.drawable.image_ic_blue)).into(holder.image);
            }else if(c.file.contains(".mp4"))
            {
                holder.image.setImageDrawable(activity.getResources().getDrawable(R.drawable.mp4_image));
            }else if(c.file.contains(".mkv"))
            {
                holder.image.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mkv));
            }else
            {
                holder.image.setImageDrawable(activity.getResources().getDrawable(R.drawable.folder));
            }
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
                .inflate(R.layout.document_item_row,parent, false);
        return new MyViewHolder(v);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }


    public void updateUserId(String selectedUserId){
        this.selectedUserId = selectedUserId;
    }
}