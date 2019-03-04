package com.danqin.memory_forever.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.ActivityMainBinding;
import com.danqin.memory_forever.databinding.AddModuleDialogBinding;
import com.danqin.memory_forever.databinding.ModuleItemBinding;
import com.danqin.memory_forever.view.AddModuleDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AddModuleDialog.OnConfirmListener {
    private ActivityMainBinding binding;
    private List<Module> modules;
    private boolean isEditting;
    private ModuleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.moduleRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.moduleRecycler.setAdapter(adapter = new ModuleAdapter());

        loadData();
    }

    private void loadData() {
        modules = new ArrayList<>();
        Module module1 = new Module();
        module1.setImageRes(R.drawable.india_chennai_highway);
        module1.setName("大学赠言");
        Module module2 = new Module();
        module2.setImageRes(R.drawable.india_chettinad_produce);
        module2.setName("高中赠言");
        Module module3 = new Module();
        module3.setImageRes(R.drawable.india_chettinad_silk_maker);
        module3.setName("初中赠言");
        modules.add(module1);
        modules.add(module2);
        modules.add(module3);
        adapter.notifyDataSetChanged();
    }

    private AddModuleDialog dialog;
    public void addModule(View view) {
        if (dialog == null) {
            dialog = new AddModuleDialog(this);
            dialog.setOnConfirmListener(this);
        }
        dialog.show();
    }

    public void edit(View view) {
        isEditting = !isEditting;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void confirm(Module module) {
        modules.add(module);
        adapter.notifyDataSetChanged();
    }


    private class ModuleAdapter extends RecyclerView.Adapter<ModuleHolder> {

        @NonNull
        @Override
        public ModuleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ModuleHolder((ModuleItemBinding) DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.module_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ModuleHolder moduleHolder, int i) {
            moduleHolder.setItem(modules.get(i));
        }

        @Override
        public int getItemCount() {
            return modules.size();
        }
    }

    private class ModuleHolder extends RecyclerView.ViewHolder {
        ModuleItemBinding binding;

        public ModuleHolder(ModuleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(final Module module) {
            binding.moduleImg.setImageResource(module.getImageRes());
            binding.moduleName.setText(module.getName());
            binding.moduleDelete.setVisibility(isEditting ? View.VISIBLE : View.GONE);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,GreetsActivity.class);
                    intent.putExtra(GreetsActivity.KEY_MODULE,module);
                    startActivity(intent);
                }
            });
            binding.moduleDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("注意");
                    builder.setMessage("确定要删除该模块吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            modules.remove(module);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }

    }

}
