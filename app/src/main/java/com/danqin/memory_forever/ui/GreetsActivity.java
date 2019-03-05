package com.danqin.memory_forever.ui;

import android.app.Activity;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private GreetAdapter adapter;
    private String tag;
    private MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_greets);
        module = (Module) getIntent().getSerializableExtra(KEY_MODULE);


        binding.moduleName.setText(module.getName());
        binding.greetsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.greetsRecycler.setAdapter(adapter = new GreetAdapter());
        loadData();
    }

    private List<String> paths = new ArrayList<>();

    public List<String> refreshFileList(String strPath) {
        String filename;//文件名
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

        if (files == null)
            return null;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath());//递归文件夹！！！
            } else {
                filename = files[i].getName();
                if (filename.endsWith("aac"))//判断是不是msml后缀的文件
                {
                    paths.add(files[i].getAbsolutePath());//对于文件才把它的路径加到filelist中
                }
            }
        }
        return paths;
    }


    private void loadData() {

        List<String> paths = refreshFileList(Environment.getExternalStorageDirectory() + "/Record");

        for (String path : paths) {
            Greet greet = new Greet();
            greet.setClassmateName("蒋沁钊");
            greet.setTime("2019.03.05 09:56");
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
            greet.getRecords().add(record);
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
        LinearLayout[] recordLayouts = new LinearLayout[3];
        FrameLayout[] videoLayouts = new FrameLayout[3];
        Button[] recordPaths = new Button[3];
        TextView[] recordDurations = new TextView[3];

        ImageView[] videoPaths = new ImageView[3];


        public GreetHolder(GreetItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            recordLayouts[0] = binding.record1;
            recordLayouts[1] = binding.record2;
            recordLayouts[2] = binding.record3;
            videoLayouts[0] = binding.greetVideoLayout1;
            videoLayouts[1] = binding.greetVideoLayout2;
            videoLayouts[2] = binding.greetVideoLayout3;
            recordPaths[0] = binding.greetRecord1;
            recordPaths[1] = binding.greetRecord2;
            recordPaths[2] = binding.greetRecord3;
            recordDurations[0] = binding.recordDuration1;
            recordDurations[1] = binding.recordDuration2;
            recordDurations[2] = binding.recordDuration3;
            videoPaths[0] = binding.greetVideo1;
            videoPaths[1] = binding.greetVideo2;
            videoPaths[2] = binding.greetVideo3;
        }

        public void setData(Greet greet) {
            for (LinearLayout layout : recordLayouts) {
                layout.setVisibility(View.GONE);
            }
            for (FrameLayout layout : videoLayouts) {
                layout.setVisibility(View.GONE);
            }
            binding.classmateName.setText(greet.getClassmateName());
            binding.greetTime.setText(greet.getTime());
            List<Greet.Record> records = greet.getRecords();
            for (int i = 0; i < records.size(); i++) {
                final Greet.Record record = records.get(i);
                recordLayouts[i].setVisibility(View.VISIBLE);
                recordPaths[i].setText(record.getRecordPath());
                recordDurations[i].setText(record.getDuration() + "''");
                recordPaths[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("JiangLiang","record path is : " + record.getRecordPath() + ",tag is : " + tag);
                        if (record.getRecordPath().equals(tag)) {
                            if (player.isPlaying()) {
                                player.pause();
                            }else{
                                player.start();
                            }
                        }
                        if (!record.getRecordPath().equals(tag)){
                            if (player.isPlaying()){
                                Log.e("JiangLiang","player is playing ... ");
                                player.pause();
                            }
                            try {
                                player.reset();
                                player.setDataSource(record.getRecordPath());
                                player.prepare();
                                player.start();
                                Log.e("JiangLiang","player is start ... ");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        tag = record.getRecordPath();
                    }
                });
            }
            List<String> paths = greet.getVideoPaths();
            binding.constraintLayout.setVisibility(paths.isEmpty() ? View.GONE : View.VISIBLE);
            for (int i = 0; i < paths.size(); i++) {
                videoLayouts[i].setVisibility(View.VISIBLE);
            }
        }

    }


}
