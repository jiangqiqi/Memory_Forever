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
import com.danqin.memory_forever.utils.Commons;
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
            animate(Techniques.SlideInLeft,binding.text1);
            animate(Techniques.SlideInLeft,binding.text3);
            animate(Techniques.SlideInRight,binding.text2);
            animate(Techniques.SlideInRight,binding.text4);
            animate(Techniques.SlideInDown,binding.text5);
            animate(Techniques.SlideInUp,binding.loginByWeixin);
            flag = true;
        }
    }

    private void animate(Techniques techniques,View target){
        YoYo.with(techniques)
                .duration(Commons.ANIMATION_DURATION)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(target);
    }

    public void login(View view) {
        //TODO:微信登录
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
