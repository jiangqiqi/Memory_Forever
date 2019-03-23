package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Record;
import com.danqin.memory_forever.databinding.RecordFullScreenLayoutBinding;
import com.danqin.memory_forever.view.preview.PreviewItemFragment;
import com.danqin.memory_forever.view.preview.PreviewPagerAdapter;

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
    }

    @Override
    protected void loadData() {
        super.loadData();
        Intent intent = getIntent();
        Record record = (Record) intent.getSerializableExtra(KEY_RECORD);
        String videoUrl = record.getVideoUrl();
        if (!TextUtils.isEmpty(videoUrl)) {
            binding.viewpager.setVisibility(View.GONE);
            playVideo(videoUrl);
        }
        List<String> imgUrls = record.getImgUrls();
        if (imgUrls != null) {
            binding.fullScreenPlay.setVisibility(View.GONE);
            pagerAdapter.addAllImgs(imgUrls);
            pagerAdapter.notifyDataSetChanged();
        }
        binding.content.setText(record.getContent());
    }

    private void playVideo(String videoUrl) {
        binding.fullScreenPlay.setVideoPath(videoUrl);
        binding.fullScreenPlay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        binding.fullScreenPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        binding.fullScreenPlay.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding.fullScreenPlay.isPlaying()){
            binding.fullScreenPlay.stopPlayback();
        }
    }
}
