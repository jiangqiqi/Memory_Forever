package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.RecordsLayoutBinding;

import java.io.File;

public class RecordsActivity extends BaseActivity {
    private RecordsLayoutBinding binding;
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };

    public static final int REQUEST_CODE_IMAGE_CAPTURE = 10000;
    public static final int REQUEST_CODE_VIDEO_CAPTURE = 10001;
    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final String KEY_RESOURCE_FILE = "resourceFile";
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.records_layout);
    }

    @Override
    protected void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }
        Module module = (Module) getIntent().getSerializableExtra(MainActivity.KEY_MODULE);
        binding.topLayout.moduleName.setText(module.getName());
        initArcMenu();
    }

    @Override
    protected void loadData() {
        super.loadData();
        File file = new File(Environment.getExternalStorageDirectory() + "/Images");
        if (!file.exists()){
            file.mkdir();
        }
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
//                    Intent intent = new Intent(RecordsActivity.this,EditActivity.class);
//                    startActivity(intent);
                    switch (position){
                        case 0:
                            captureImg();
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

    private File file;
    //调用系统现有相机拍照
    private void captureImg(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        file = new File(Environment.getExternalStorageDirectory()+"/Images/" + System.currentTimeMillis()+".png");
        if (file.exists()){
            file.delete();
        }
        Uri imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,REQUEST_CODE_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap = data.getExtras().getParcelable("data");
//        binding.img.setImageBitmap(bitmap);
        Log.e(tag,"file's size is : " + file.length());
        switch (requestCode){
            case REQUEST_CODE_IMAGE_CAPTURE:
                Intent intent = new Intent(this,EditActivity.class);
                intent.putExtra(KEY_REQUEST_CODE,REQUEST_CODE_IMAGE_CAPTURE);
                intent.putExtra(KEY_RESOURCE_FILE,file);
                startActivity(intent);
                break;
            case REQUEST_CODE_VIDEO_CAPTURE:
                break;
        }

    }
}
