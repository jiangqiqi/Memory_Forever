package com.danqin.memory_forever.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.danqin.memory_forever.R;

public class SelectDialog extends Dialog implements View.OnClickListener {

    private OnSelectListener listener;

    public SelectDialog(Context context) {
        super(context);
    }

    public SelectDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SelectDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_dialog);
        setCanceledOnTouchOutside(true);
        findViewById(R.id.capture).setOnClickListener(this);
        findViewById(R.id.select_from_photo).setOnClickListener(this);
    }

    public SelectDialog setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.capture:
                if (null != listener) {
                    listener.selectCapture();
                }
                break;
            case R.id.select_from_photo:
                if (null != listener) {
                    listener.selectPhoto();
                }
                break;
        }
    }

    public interface OnSelectListener {
        void selectCapture();

        void selectPhoto();
    }

}
