package com.danqin.memory_forever.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.AddModuleDialogBinding;

public class AddModuleDialog extends Dialog {

    private AddModuleDialogBinding binding;
    private OnConfirmListener listener;
    private static final int GIFT_TYPE = 1;
    private static final int RECORD_TYPE = 0;


    public AddModuleDialog(Context context) {
        super(context, R.style.MyDialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.add_module_dialog,null,false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                binding.editModuleName.setText("");
            }
        });
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = GIFT_TYPE;
                if (binding.recordRb.isSelected()){
                    type = RECORD_TYPE;
                }
                String name = binding.editModuleName.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(getContext(), "请输入名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (listener!=null){
                    Module module = new Module();
                    module.setType(type);
                    module.setName(name);
                    module.setImageRes(R.drawable.india_pondicherry_fisherman);
                    listener.confirm(module);
                    dismiss();
                }
                binding.editModuleName.setText("");
            }
        });
    }

    public void setOnConfirmListener(OnConfirmListener listener){
        this.listener = listener;
    }

    public interface OnConfirmListener{
        void confirm(Module module);
    }

}
