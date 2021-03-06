package com.danqin.memory_forever.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.ActivityMainBinding;
import com.danqin.memory_forever.databinding.ModuleItemBinding;
import com.danqin.memory_forever.utils.Commons;
import com.danqin.memory_forever.utils.Glide4Engine;
import com.danqin.memory_forever.utils.SpUtil;
import com.danqin.memory_forever.view.dialog.AddModuleDialog;
import com.danqin.memory_forever.view.dialog.MoreDialog;
import com.danqin.memory_forever.view.dialog.PromotionDialog;
import com.danqin.memory_forever.view.dialog.SelectDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AddModuleDialog.OnAddModuleListener, MoreDialog.OnMoreListener {
    private ActivityMainBinding binding;
    private List<Module> modules;
    private ModuleAdapter adapter;
    public static final String KEY_MODULE = "module";

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.moduleRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.moduleRecycler.setAdapter(adapter = new ModuleAdapter());
        String alipayNumber = SpUtil.getAlipayNumber();
        if (!TextUtils.isEmpty(alipayNumber)){
            binding.alipayId.setText(alipayNumber);
            binding.alipayNumEdit.setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadData() {
        modules = new ArrayList<>();
        Module module1 = new Module();
        module1.setImageRes(R.drawable.india_chennai_highway);
        module1.setName("大学赠言");
        module1.setType(1);
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



    private AddModuleDialog addModuleDialog;

    public void addModule(View view) {
        addModuleDialog = new AddModuleDialog(this);
        addModuleDialog.setOnConfirmListener(this);
        addModuleDialog.show();
    }

    @Override
    public void confirm(Module module) {
        //TODO:将新添模块上传至服务器
        modules.add(0, module);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void selectCover() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Zhihu)
                .showSingleMediaType(true)
                .countable(false)
                .maxSelectable(1)
                .originalEnable(true)
                .maxOriginalSize(10)
                .imageEngine(new Glide4Engine())
                .forResult(1000);
    }

    @Override
    public void showPayUi() {
        new PromotionDialog(this, R.style.dialog, getString(R.string.confirm_buy), new PromotionDialog.OnCloseListener() {

            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    addModuleDialog.confirm();
                }
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void edit(Module module) {
        modules.set(currentPosition,module);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 1000) {
            Uri coverUri = Matisse.obtainResult(data).get(0);
            addModuleDialog.setCoverUri(coverUri);
        }
    }

    private void showMoreDialog() {
        new MoreDialog(MainActivity.this).setOnMoreListener(this).show();
    }

    @Override
    public void editModule() {
        addModuleDialog = new AddModuleDialog(this);
        addModuleDialog.setOnConfirmListener(this);
        addModuleDialog.setModule(modules.get(currentPosition));
        addModuleDialog.show();
    }

    private int currentPosition;

    @Override
    public void deleteModule() {
        new PromotionDialog(MainActivity.this, R.style.dialog, getString(R.string.confirm_delete_module), new PromotionDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    modules.remove(currentPosition);
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        }).show();
    }

    public void showSlideLayout(View view) {
        YoYo.with(Techniques.SlideInLeft)
                .duration(600)
                .playOn(binding.slideLayout);
        binding.slideLayout.setVisibility(View.VISIBLE);
        binding.fab.setVisibility(View.GONE);
    }

    public void hideSlideLayout(View view) {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(600)
                .playOn(binding.slideLayout);
        binding.fab.setVisibility(View.VISIBLE);
    }

    public void bindAlipayNumber(View view) {
        if (!TextUtils.isEmpty(SpUtil.getAlipayNumber())){
            binding.alipayId.setVisibility(View.GONE);
            binding.alipayNumEdit.setVisibility(View.VISIBLE);
            return;
        }

        String alipayNumber = binding.alipayNumEdit.getText().toString();
        if (TextUtils.isEmpty(alipayNumber)){
            YoYo.with(Techniques.Shake)
                    .duration(Commons.ANIMATION_DURATION)
                    .playOn(binding.alipayNumEdit);
        }else{
            binding.alipayNumEdit.setVisibility(View.GONE);
            binding.alipayId.setVisibility(View.VISIBLE);
            binding.alipayNum.setText(alipayNumber);
            SpUtil.putAlipayNumber(alipayNumber);
        }
    }

    private class ModuleAdapter extends RecyclerView.Adapter<ModuleHolder> {

        @NonNull
        @Override
        public ModuleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ModuleHolder((ModuleItemBinding) DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.module_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ModuleHolder moduleHolder, int i) {
            moduleHolder.setItem(i);
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

        public void setItem(final int position) {
            final Module module = modules.get(position);
            if (module.getCoverUri() == null) {
                binding.moduleImg.setImageResource(module.getImageRes());
            } else {
                binding.moduleImg.setImageURI(module.getCoverUri());
            }
            binding.moduleName.setText(module.getName());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (module.getType() == 1) {
                        Intent intent = new Intent(MainActivity.this, GreetsActivity.class);
                        intent.putExtra(KEY_MODULE, module);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                        intent.putExtra(KEY_MODULE, module);
                        startActivity(intent);
                    }
                }
            });
            binding.moduleDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    showMoreDialog();
                }
            });
        }

    }

}
