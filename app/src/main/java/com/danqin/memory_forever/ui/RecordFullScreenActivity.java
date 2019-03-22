package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Record;
import com.danqin.memory_forever.databinding.RecordFullScreenLayoutBinding;
import com.danqin.memory_forever.view.preview.PreviewItemFragment;
import com.danqin.memory_forever.view.preview.PreviewPagerAdapter;

import java.io.IOException;
import java.util.List;

public class RecordFullScreenActivity extends BaseActivity {

    private RecordFullScreenLayoutBinding binding;
    public static final String KEY_RECORD = "record";
    private PreviewPagerAdapter pagerAdapter;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.record_full_screen_layout);
    }


    @Override
    protected void initView() {
        super.initView();
        pagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), new PreviewItemFragment.OnPagerClickListener() {
            @Override
            public void pagerClick() {
                finish();
            }
        });
        binding.viewpager.setAdapter(pagerAdapter);
        binding.fullScreenPlay.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (player != null && player.isPlaying()) {
                    player.stop();
                }
            }
        });
    }

    private MediaPlayer player;

    @Override
    protected void loadData() {
        super.loadData();
        Intent intent = getIntent();
        Record record = (Record) intent.getSerializableExtra(KEY_RECORD);
        String videoUrl = record.getVideoUrl();
        Log.e(tag, "video url is : " + videoUrl);
        if (!TextUtils.isEmpty(videoUrl)) {
            binding.viewpager.setVisibility(View.GONE);
            binding.fullScreenPlay.setVisibility(View.VISIBLE);
            playVideo(videoUrl);
        }
        List<String> imgUrls = record.getImgUrls();
        Log.e(tag, "img urls is : " + imgUrls);
        if (imgUrls != null) {
            binding.fullScreenPlay.setVisibility(View.GONE);
            pagerAdapter.addAllImgs(imgUrls);
            pagerAdapter.notifyDataSetChanged();
        }
        binding.content.setText(record.getContent());
    }

    private void playVideo(String videoUrl) {
        try {
            player = new MediaPlayer();
            player.setDataSource(videoUrl);
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
        }
    }
}
