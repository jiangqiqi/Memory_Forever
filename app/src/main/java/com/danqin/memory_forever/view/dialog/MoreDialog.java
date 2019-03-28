package com.danqin.memory_forever.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.danqin.memory_forever.R;

public class MoreDialog extends Dialog implements View.OnClickListener {

    private OnMoreListener listener;

    public MoreDialog(Context context) {
        super(context);
    }

    public MoreDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MoreDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_dialog);
        setCanceledOnTouchOutside(true);
        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
    }

    public MoreDialog setOnMoreListener(OnMoreListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.edit:
                if (null != listener) {
                    listener.editModule();
                }
                break;
            case R.id.delete:
                if (null != listener) {
                    listener.deleteModule();
                }
                break;
        }
    }

    public interface OnMoreListener {
        void editModule();

        void deleteModule();
    }

}
