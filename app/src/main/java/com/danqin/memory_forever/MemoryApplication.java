package com.danqin.memory_forever;

import android.app.Application;
import android.content.Context;

public class MemoryApplication extends Application {
    public static Context context;
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }

}
