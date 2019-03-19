package com.danqin.memory_forever.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.danqin.memory_forever.R;

public class PromotionDialog extends Dialog implements View.OnClickListener {
    private TextView contentTxt;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public PromotionDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public PromotionDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public PromotionDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected PromotionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public PromotionDialog setTitle(String title) {
        this.title = title;
        Log.e("JiangLiang", "set title ........... ");
        return this;
    }

    public PromotionDialog setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }

    public PromotionDialog setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("JiangLiang", "on create.............");
        setContentView(R.layout.promotion_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        contentTxt = (TextView) findViewById(R.id.content);
        titleTxt = (TextView) findViewById(R.id.title);
        submitTxt = (TextView) findViewById(R.id.confirm);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView) findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);

        contentTxt.setText(content);
        if (!TextUtils.isEmpty(positiveName)) {
            submitTxt.setText(positiveName);
        }

        if (!TextUtils.isEmpty(negativeName)) {
            cancelTxt.setText(negativeName);
        }

        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }

    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.cancel:
                if (listener != null) {
                    listener.onClick(this, false);
                }
                break;
            case R.id.confirm:
                if (listener != null) {
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}
