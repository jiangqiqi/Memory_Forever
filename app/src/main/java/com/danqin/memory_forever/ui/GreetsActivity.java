package com.danqin.memory_forever.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.ActivityGreetsBinding;

public class GreetsActivity extends Activity {

    private ActivityGreetsBinding binding;
    private Module module;
    public static final String KEY_MODULE = "module";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_greets);
        module = (Module) getIntent().getSerializableExtra(KEY_MODULE);


        binding.moduleName.setText(module.getName());

    }


    public void back(View view){
        finish();
    }

    public void invitate(View view){
        //TODO:通过微信分享到微信群
    }

}
