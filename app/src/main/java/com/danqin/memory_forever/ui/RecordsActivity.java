package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Module;
import com.danqin.memory_forever.databinding.RecordsLayoutBinding;

import java.io.File;

public class RecordsActivity extends BaseActivity {
    private RecordsLayoutBinding binding;
    private static final int[] ITEM_DRAWABLES = {R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with};

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
            StrictMode.setVmPolicy(builder.build());
        }
        Module module = (Module) getIntent().getSerializableExtra(MainActivity.KEY_MODULE);
        binding.topLayout.moduleName.setText(module.getName());
        initArcMenu();
    }

    @Override
    protected void loadData() {
        super.loadData();
        File imgDir = new File(Environment.getExternalStorageDirectory() + "/Memory/images");
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
        File videoDir = new File(Environment.getExternalStorageDirectory() + "Memory/videos");
        if (!videoDir.exists()) {
            videoDir.mkdirs();
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
                    switch (position) {
                        case 0:
                            captureImg();
                            break;
                        case 1:
                            captureVideo();
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
    private void captureImg() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        file = new File(Environment.getExternalStorageDirectory() + "/Memory/images/" + System.currentTimeMillis() + ".png");
        if (file.exists()) {
            file.delete();
        }
        Uri imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
    }

    //调用系统现有相机拍视频
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
        startActivityForResult(intent, REQUEST_CODE_VIDEO_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(this, EditActivity.class);
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
                intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_IMAGE_CAPTURE);
                intent.putExtra(KEY_RESOURCE_FILE, file);
                break;
            case REQUEST_CODE_VIDEO_CAPTURE:
                intent.putExtra(KEY_REQUEST_CODE, REQUEST_CODE_VIDEO_CAPTURE);
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
                if (cursor!=null&&cursor.moveToNext()){
                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                    file = new File(filePath);
                    intent.putExtra(KEY_RESOURCE_FILE, file);
                    cursor.close();
                }
                break;
        }
        startActivity(intent);

    }
}
