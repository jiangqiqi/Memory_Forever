package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.view.View;

import com.bumptech.glide.Glide;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.databinding.EditLayoutBinding;

import java.io.File;

public class EditActivity extends BaseActivity {

    private EditLayoutBinding binding;

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
        requestCode = intent.getIntExtra(RecordsActivity.KEY_REQUEST_CODE,0);
        file = (File) intent.getSerializableExtra(RecordsActivity.KEY_RESOURCE_FILE);

        if (requestCode == RecordsActivity.REQUEST_CODE_IMAGE_CAPTURE){
            binding.videoLayout.setVisibility(View.GONE);
            Glide.with(this)
                    .load(file)
                    .override(300,600)
                    .fitCenter()
                    .into(binding.imgAdd);
        }

    }

    //全屏播放视频，并可以执行删除操作。
    public void fullScreenPlayVideo(View view){

    }

}
