package com.danqin.memory_forever.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.AddModuleDialogBinding;
import com.danqin.memory_forever.utils.MDP_PX;

public class AddModuleDialog extends Dialog {

    private AddModuleDialogBinding binding;
    private OnAddModuleListener listener;
    private static final int GIFT_TYPE = 1;
    private static final int RECORD_TYPE = 0;
    private Uri coverUri;
    private int type = GIFT_TYPE;
    private boolean flag;

    public AddModuleDialog(Context context) {
        super(context, R.style.MyDialog);

    }

    public void setCoverUri(Uri coverUri) {
        this.coverUri = coverUri;
        Glide.with(getContext())
                .load(coverUri)
                .override(MDP_PX.dip2px(100), MDP_PX.dip2px(140))
                .centerCrop()
                .into(binding.coverImg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.add_module_dialog, null, false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
        binding.giftRb.setText("赠言(付费)");
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                binding.editModuleName.setText("");
            }
        });

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.record_rb:
                        type = RECORD_TYPE;
                        break;
                    case R.id.gift_rb:
                        type = GIFT_TYPE;
                        break;
                }
            }
        });

        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editModuleName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "请输入名称", Toast.LENGTH_SHORT).show();
                    YoYo.with(Techniques.Bounce)
                            .duration(1200)
                            .playOn(binding.editModuleName);
                    return;
                }

                if (coverUri == null) {
                    Toast.makeText(getContext(), "请设置封面图片", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!flag && type != RECORD_TYPE) {
                    listener.showPayUi();
                    return;
                }

                if (listener != null) {
                    Module module = new Module();
                    module.setType(type);
                    module.setName(name);
                    module.setImageRes(R.drawable.india_pondicherry_fisherman);
                    module.setCoverUri(coverUri);
                    listener.confirm(module);
                }
                dismiss();
            }
        });

        binding.coverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.selectCover();
                }
            }
        });
    }

    public void confirm() {
        if (listener != null) {
            String name = binding.editModuleName.getText().toString().trim();
            Module module = new Module();
            module.setType(type);
            module.setName(name);
            module.setImageRes(R.drawable.india_pondicherry_fisherman);
            module.setCoverUri(coverUri);
            listener.confirm(module);
        }
        dismiss();
    }


    public void setOnConfirmListener(OnAddModuleListener listener) {
        this.listener = listener;
    }

    public interface OnAddModuleListener {
        void confirm(Module module);

        void selectCover();

        void showPayUi();
    }

}
