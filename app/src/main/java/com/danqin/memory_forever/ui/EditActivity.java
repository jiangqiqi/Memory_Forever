package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.View;

import com.bumptech.glide.Glide;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.databinding.EditLayoutBinding;

import java.io.File;
import java.io.IOException;

public class EditActivity extends BaseActivity {

    private EditLayoutBinding binding;
    private MediaPlayer player;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_layout);
        binding.editContent.requestFocus();
    }

    private int requestCode;
    private File file;

    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        requestCode = intent.getIntExtra(RecordsActivity.KEY_REQUEST_CODE, 0);
        file = (File) intent.getSerializableExtra(RecordsActivity.KEY_RESOURCE_FILE);

        if (requestCode == RecordsActivity.REQUEST_CODE_IMAGE_CAPTURE) {
            binding.videoLayout.setVisibility(View.GONE);
//            Glide.with(this)
//                    .load(file)
//                    .override(300,600)
//                    .fitCenter()
//                    .into(binding.imgAdd);
        }

        if (requestCode == RecordsActivity.REQUEST_CODE_VIDEO_CAPTURE) {
            binding.videoLayout.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(file)
                    .override(300, 600)
                    .fitCenter()
                    .into(binding.imgVideo);
            player = new MediaPlayer();
            binding.fullScreenPlay.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

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


    }

    //全屏播放视频，并可以执行删除操作。
    public void fullScreenPlayVideo(View view) {
        binding.fullScreenLayout.setVisibility(View.VISIBLE);
        playVideo();
    }

    private void playVideo(){
        try {
            player.reset();
            player.setDataSource(file.getAbsolutePath());
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.setDisplay(binding.fullScreenPlay.getHolder());
                    player.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    binding.playIv.setVisibility(View.VISIBLE);
                    player.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        if (binding.fullScreenLayout.getVisibility() == View.VISIBLE){
            player.setDisplay(null);
            binding.fullScreenLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    public void hideFullScreen(View view) {
        binding.fullScreenLayout.setVisibility(View.GONE);
        if (player.isPlaying()) {
            player.stop();
        }
    }

}
