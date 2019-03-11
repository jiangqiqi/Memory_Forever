package com.danqin.memory_forever.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Greet;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.ActivityGreetsBinding;
import com.danqin.memory_forever.databinding.GreetItemBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GreetsActivity extends Activity {
    private ActivityGreetsBinding binding;
    private Module module;
    public static final String KEY_MODULE = "module";
    private List<Greet> greets = new ArrayList<>();
    private String videoPath;
    private GreetAdapter adapter;
    private String tag;
    private MediaPlayer player = new MediaPlayer();
    private SurfaceView videoSurfaceview;
    private TextView invitateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_greets);
        module = (Module) getIntent().getSerializableExtra(KEY_MODULE);

        binding.moduleName.setText(module.getName());
        binding.greetsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.greetsRecycler.setAdapter(adapter = new GreetAdapter());
        videoSurfaceview = binding.videoSurfaceview;
        invitateBtn = binding.invitateBtn;
        videoSurfaceview.getHolder().addCallback(callback);
        loadData();
    }

    private List<String> paths = new ArrayList<>();

    public List<String> refreshFileList(String strPath, String end) {
        String filename;//文件名
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

        if (files == null)
            return null;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath(), end);//递归文件夹！！！
            } else {
                filename = files[i].getName();
                if (filename.endsWith(end))//判断是不是msml后缀的文件
                {
                    paths.add(files[i].getAbsolutePath());//对于文件才把它的路径加到filelist中
                }
            }
        }
        return paths;
    }

    private void loadData() {

        List<String> paths = refreshFileList(Environment.getExternalStorageDirectory() + "/Record", "aac");

        for (String path : paths) {
            Greet greet = new Greet();
            greet.setClassmateName("蒋沁钊");
            greet.setTime("2019-3-5 09:56");
            Greet.Record record = new Greet.Record();
            try {
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(path);
                player.prepare();
                record.setDuration(player.getDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
            record.setRecordPath(path);
            greet.setRecord(record);
            greets.add(greet);
        }
        paths.clear();
        List<String> videos = refreshFileList(Environment.getExternalStorageDirectory() + "/Tencent/MicroMsg/WeiXin", "mp4");
        for (int i = 0; i < 10; i++) {
            Greet greet = new Greet();
            greet.setClassmateName("蒋沁钊");
            greet.setTime("2019.-3-11 09:56");
            greet.setVideoPath(videos.get(i));
            greets.add(greet);
        }

        adapter.notifyDataSetChanged();
    }

    public void back(View view) {
        finish();
    }

    public void invitate(View view) {
        //TODO:通过微信分享到微信群
    }
    
    public void hideSurfaceView(View view){
        binding.videoSurfaceview.setVisibility(View.GONE);
        invitateBtn.setVisibility(View.VISIBLE);
        if (player.isPlaying()){
            player.stop();
        }
        player.setDisplay(null);
    }
    private void play(String dataSource){
        try {
            player.reset();
            player.setDataSource(dataSource);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
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
        if (player.isPlaying()){
            player.stop();
        }
        player.release();
        callback = null;
        player = null;
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
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
    };

    private class GreetAdapter extends RecyclerView.Adapter<GreetHolder> {
        @NonNull
        @Override
        public GreetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new GreetHolder((GreetItemBinding) DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.greet_item, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GreetHolder greetHolder, int i) {
            greetHolder.setData(greets.get(i));
        }

        @Override
        public int getItemCount() {
            return greets.size();
        }
    }

    private class GreetHolder extends RecyclerView.ViewHolder {
        GreetItemBinding binding;

        public GreetHolder(GreetItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(final Greet greet) {
            binding.classmateName.setText(greet.getClassmateName());
            if (greet.getRecord() != null) {
                binding.greetVoice.setVisibility(View.VISIBLE);
                final Greet.Record record = greet.getRecord();
                binding.greetDuration.setText(record.getDuration() / 1000 + "\"");
                binding.greetVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (record.getRecordPath().equals(tag)) {
                            if (player.isPlaying()) {
                                player.pause();
                            } else {
                                player.start();
                            }
                        } else {
                            if (player.isPlaying()) {
                                player.pause();
                            }
                            play(record.getRecordPath());
                        }
                        tag = record.getRecordPath();
                    }
                });
            } else {
                binding.greetVoice.setVisibility(View.GONE);
            }

            if (greet.getVideoPath() != null) {
                binding.greetVideo.setVisibility(View.VISIBLE);
                //TODO:设置视频第一帧图像，点击全屏播放
                Glide.with(GreetsActivity.this)
                        .load(greet.getVideoPath())
                        .into(binding.videoFirstFrame);
                binding.greetVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invitateBtn.setVisibility(View.GONE);
                        videoSurfaceview.setVisibility(View.VISIBLE);
                        tag = greet.getVideoPath();
                        try {
                            player.reset();
                            player.setDataSource(greet.getVideoPath());
                            player.prepareAsync();
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    player.setDisplay(videoSurfaceview.getHolder());
                                    player.start();
                                }
                            });
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                binding.greetVideo.setVisibility(View.GONE);
            }
        }

    }
}
