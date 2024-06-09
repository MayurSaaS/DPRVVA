package com.vvautotest.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.vvautotest.model.Image;
import com.vvautotest.utils.TouchImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class FullViewImageAdapter extends PagerAdapter {

    ArrayList<Image> images;
    Activity activity;
    public FullViewImageAdapter (Activity activity, ArrayList<Image> images){
        this.activity = activity;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        Image postAdsImages = images.get(position);
        TouchImageView img = new TouchImageView(container.getContext());
        try
        {
            String urlStr = "";
            String url1 = postAdsImages.getUrl();
            if(url1.contains(" "))
                urlStr = url1.replace(" ", "%20");
            else
                urlStr = url1;
            URL url = new URL(urlStr);
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap image = null;
                    try {
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //     Glide.with(img).load(postAdsImages.getImage_url()).into(img);

                    img.setImageBitmap(image);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        }
                    });
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return img;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
