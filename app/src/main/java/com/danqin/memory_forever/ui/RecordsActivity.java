package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.bean.Record;
import com.danqin.memory_forever.databinding.RecordItemBinding;
import com.danqin.memory_forever.databinding.RecordsLayoutBinding;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends ResActivity {
    private RecordsLayoutBinding binding;
    private static final int[] ITEM_DRAWABLES = {R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with};
    String s = "但发斯蒂芬的佛教啊哦啊二等奖发了恐惧似懂非懂，爱递交奇怪阿尔发票奥支付到上世纪的阿斯顿发送到撒旦法大的事发的爱的色放的阿斯顿发阿斯顿发阿萨德发的多发点";
    private List<Record> records;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.records_layout);
    }

    private RecordAdapter adapter;

    @Override
    protected void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        Module module = getIntent().getParcelableExtra(MainActivity.KEY_MODULE);
        binding.topLayout.moduleName.setText(module.getName());
        initArcMenu();
        binding.recordRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recordRecycler.setAdapter(adapter = new RecordAdapter());
    }

    @Override
    protected void loadData() {
        super.loadData();
        File imgDir = new File(Environment.getExternalStorageDirectory() + "/Memory/images");
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
        File videoDir = new File(Environment.getExternalStorageDirectory() + "Memory/videos");
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }
        records = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Record record = new Record();
            record.setContent(s);
            record.setDay(i + 1);
            record.setMonth(3);
            records.add(record);
        }
        adapter.notifyDataSetChanged();
    }

    private void initArcMenu() {
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = i;
            binding.arcMenu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            captureImg();
                            break;
                        case 1:
                            captureVideo();
                            break;
                        case 2:
                            selectImgFromPhotoAlbum();
                            break;
                        case 3:
                            selectVideoFromPhotoAlbum();
                            break;
                        case 4:
                            editOnlyText();
                            break;
                        case 5:
                            break;
                    }
                }
            });
        }
    }

    private void editOnlyText() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_ONLY_TEXT);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(tag, "result code is : " + resultCode);
        if (resultCode == RESULT_CODE) {
            Record record = data.getParcelableExtra(KEY_EDIT_RESULT);
            records.add(0, record);
            adapter.notifyDataSetChanged();
            return;
        }

        Intent intent = new Intent(this, EditActivity.class);
        if (data == null) {
            if (!isCaptureImg) {
                return;
            } else {
                if (file.length() == 0) {
                    return;
                }
            }
            isCaptureImg = false;
        }

        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
                intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_IMAGE_CAPTURE);
                intent.putExtra(KEY_RESOURCE_URI, Uri.fromFile(file));
                break;
            case REQUEST_CODE_VIDEO_CAPTURE:
                intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_VIDEO_CAPTURE);
                Uri uri = data.getData();
                intent.putExtra(KEY_RESOURCE_URI, uri);
                break;
            case REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM:
                intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM);
                ArrayList<Uri> selectedImgUris = (ArrayList<Uri>) Matisse.obtainResult(data);
                intent.putParcelableArrayListExtra(KEY_RESOURCE_URIS, selectedImgUris);
                break;
            case REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM:
                ArrayList<Uri> selectedVideoUris = (ArrayList<Uri>) Matisse.obtainResult(data);
                intent.putExtra(KEY_RESOURCE_URI, selectedVideoUris.get(0));
                intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM);
                break;
        }
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {

        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RecordHolder((RecordItemBinding) DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.record_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder recordHolder, int i) {
            recordHolder.setData(records.get(i));
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

    private class RecordHolder extends RecyclerView.ViewHolder {
        RecordItemBinding binding;

        public RecordHolder(RecordItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(final Record record) {
            binding.videoFlag.setVisibility(TextUtils.isEmpty(record.getVideoUrl()) ? View.GONE : View.VISIBLE);
            binding.contentTv.setText(record.getContent());
            String date = "";
            int day = record.getDay();
            if (day < 10) {
                date = 0 + date + day;
            } else {
                date = Integer.toString(day);
            }
            binding.dateTv.setText(date);
            binding.monthTv.setText(record.getMonth() + "月");
            //TODO:根据uri设置图片
            if (record.getVideoUrl() != null) {
                Glide.with(RecordsActivity.this)
                        .load(record.getVideoUrl())
                        .into(binding.itemImg);
            } else {
                if (TextUtils.isEmpty(record.getSmallImgUrl())) {
                    Glide.with(RecordsActivity.this)
                            .load(R.drawable.india_thanjavur_market)
                            .into(binding.itemImg);
                } else {
                    Glide.with(RecordsActivity.this)
                            .load(record.getSmallImgUrl())
                            .into(binding.itemImg);
                }
            }

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecordsActivity.this,RecordFullScreenActivity.class);
                    intent.putExtra(RecordFullScreenActivity.KEY_RECORD,record);
                    startActivity(intent);
                }
            });


        }

    }

}
