package com.danqin.memory_forever.ui;

import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageView;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.RecordsLayoutBinding;

public class RecordsActivity extends BaseActivity {
    private RecordsLayoutBinding binding;
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.records_layout);
    }

    @Override
    protected void initView() {
        super.initView();
        Module module = (Module) getIntent().getSerializableExtra(MainActivity.KEY_MODULE);
        binding.topLayout.moduleName.setText(module.getName());
        initArcMenu();
    }
    private void initArcMenu() {
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = i;
            binding.arcMenu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                    }
                }
            });
        }
    }
    @Override
    protected void loadData() {
        super.loadData();

    }
}
