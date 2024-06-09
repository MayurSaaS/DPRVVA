package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.VideoView;

import com.vvautotest.R;
import com.vvautotest.utils.FullScreenMediaController;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VVVideoPlayer extends AppCompatActivity {

    @BindView(R.id.videoview)
    VideoView videoView;

    String  url;
    FullScreenMediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vvvideo_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null)
        {
            url = intent.getStringExtra("url");
        }
        init();
    }
    public void init(){
        mediaController = new FullScreenMediaController(this);
        mediaController.setAnchorView(videoView);

        Uri video = Uri.parse(url);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}