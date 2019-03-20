package com.danqin.memory_forever.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.danqin.memory_forever.R;
import com.danqin.memory_forever.utils.Glide4Engine;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;

public abstract class ResActivity extends BaseActivity {

    public static final int REQUEST_CODE_IMAGE_CAPTURE = 10000;
    public static final int REQUEST_CODE_VIDEO_CAPTURE = 10001;
    public static final int REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM = 10002;
    public static final int REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM = 10003;
    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final String KEY_RESOURCE_URI = "resourceUri";
    public static final String KEY_RESOURCE_URIS = "resourceUris";

    protected void selectVideoFromPhotoAlbum() {
        Matisse.from(this)
                .choose(MimeType.ofVideo())
                .theme(R.style.Matisse_Zhihu)
                .showSingleMediaType(true)
                .countable(false)
                .maxSelectable(1)
                .originalEnable(true)
                .maxOriginalSize(10)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM);
    }

    protected File file;
    protected boolean isCaptureImg;

    //调用系统现有相机拍照
    protected void captureImg() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        file = new File(Environment.getExternalStorageDirectory() + "/Memory/images/" + System.currentTimeMillis() + ".png");
        if (file.exists()) {
            file.delete();
        }
        Uri imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
        isCaptureImg = true;
    }

    //调用系统现有相机拍视频
    protected void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        startActivityForResult(intent, REQUEST_CODE_VIDEO_CAPTURE);
    }


    private static final int MAX_SELECTABLE_PHOTOS_SIZE = 9;
    protected int size = 0;


    //从相册中选取照片
    protected void selectImgFromPhotoAlbum() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Zhihu)
                .showSingleMediaType(true)
                .countable(false)
                .maxSelectable(MAX_SELECTABLE_PHOTOS_SIZE - size)
                .originalEnable(true)
                .maxOriginalSize(10)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM);

    }

}
