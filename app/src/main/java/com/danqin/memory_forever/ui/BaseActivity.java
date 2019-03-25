package com.danqin.memory_forever.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseActivity extends AppCompatActivity {
    protected String tag = getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView();
        initView();
        loadData();
    }

    protected void loadData(){

    }

    protected void initView() {

    }

    protected abstract void setContentView();

    public void back(View view){
        finish();
    }

}
