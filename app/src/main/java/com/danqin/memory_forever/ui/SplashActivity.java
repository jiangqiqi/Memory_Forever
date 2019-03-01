package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.databinding.ActivitySplashBinding;
import com.danqin.memory_forever.utils.SpUtil;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        if (SpUtil.isLogined()){
            binding.loginByWeixin.setVisibility(View.INVISIBLE);
        }

    }

    public void login(View view) {
        //TODO:微信登录
        startActivity(new Intent(this,MainActivity.class));
    }
}
