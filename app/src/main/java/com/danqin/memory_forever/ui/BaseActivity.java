package com.danqin.memory_forever.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity {
    protected String tag = getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
