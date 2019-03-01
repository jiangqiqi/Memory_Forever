package com.danqin.memory_forever.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.danqin.memory_forever.MemoryApplication;

public class SpUtil {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String MEMORY_SP = "memory";
    private static SharedPreferences sp = MemoryApplication.getContext().getSharedPreferences(MEMORY_SP,Context.MODE_PRIVATE);

    public static boolean isLogined(){
        return !TextUtils.isEmpty(getAccessToken());
    }

    public static void putAccessToken(Context context,String accessToken){

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCESS_TOKEN,accessToken);
        editor.apply();
    }

    public static String getAccessToken(){
        return sp.getString(ACCESS_TOKEN,"");
    }

}
