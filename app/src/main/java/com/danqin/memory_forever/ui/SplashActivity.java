package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.databinding.ActivitySplashBinding;
import com.danqin.memory_forever.utils.SpUtil;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        if (SpUtil.isLogined()) {
            binding.loginByWeixin.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean flag;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!flag) {
            YoYo.with(Techniques.SlideInLeft)
                    .duration(1500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(binding.text1);
            YoYo.with(Techniques.SlideInLeft)
                    .duration(1500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(binding.text3);
            YoYo.with(Techniques.SlideInRight)
                    .duration(1500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(binding.text2);
            YoYo.with(Techniques.SlideInRight)
                    .duration(1500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(binding.text4);
            YoYo.with(Techniques.SlideInDown)
                    .duration(1500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(binding.text5);
            YoYo.with(Techniques.SlideInUp)
                    .duration(1500)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(binding.loginByWeixin);
            flag = true;
        }
    }

    public void login(View view) {
        //TODO:微信登录
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
