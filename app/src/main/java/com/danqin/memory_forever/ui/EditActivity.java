package com.danqin.memory_forever.ui;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.bean.Record;
import com.danqin.memory_forever.databinding.EditLayoutBinding;
import com.danqin.memory_forever.databinding.ImgItemBinding;
import com.danqin.memory_forever.utils.FileUtils;
import com.danqin.memory_forever.utils.MDP_PX;
import com.danqin.memory_forever.view.dialog.PromotionDialog;
import com.danqin.memory_forever.view.SpacesItemDecoration;
import com.danqin.memory_forever.view.dialog.SelectDialog;
import com.danqin.memory_forever.view.preview.PreviewItemFragment;
import com.danqin.memory_forever.view.preview.PreviewPagerAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.media.CamcorderProfile.get;

public class EditActivity extends ResActivity implements ViewPager.OnPageChangeListener, OnFragmentInteractionListener, SelectDialog.OnSelectListener {

    private EditLayoutBinding binding;
    private MediaPlayer player;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_layout);
    }

    private int requestCode;
    private Uri uri;
    private ArrayList<Uri> uris;
    private PreviewPagerAdapter pagerAdapter;

    @Override
    protected void initView() {
        super.initView();
        binding.editContent.requestFocus();
        Intent intent = getIntent();
        requestCode = intent.getIntExtra(KEY_REQUEST_CODE, 0);
        uri = intent.getParcelableExtra(KEY_RESOURCE_URI);
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
                uris = new ArrayList<>();
                uris.add(uri);
                handleViewWithImgs();
                break;
            case REQUEST_CODE_VIDEO_CAPTURE:
                handleViewWithVideo();
                break;
            case REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM:
                uris = intent.getParcelableArrayListExtra(KEY_RESOURCE_URIS);
                handleViewWithImgs();
                break;
            case REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM:
                handleViewWithVideo();
                break;
        }
        pagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), pagerClickListener);
        binding.previewViewpager.setAdapter(pagerAdapter);
        binding.previewViewpager.addOnPageChangeListener(this);
        if (uri != null | uris.size() > 0) {
            binding.publishContent.setEnabled(true);
        } else {
            binding.publishContent.setEnabled(false);
        }
    }

    private PreviewItemFragment.OnPagerClickListener pagerClickListener = new PreviewItemFragment.OnPagerClickListener() {
        @Override
        public void pagerClick() {
            if (binding.fullScreenImageTopLayout.getVisibility() == View.GONE) {
                binding.fullScreenImageTopLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.fullScreenImageLayout.getVisibility() == View.VISIBLE) {
                binding.fullScreenImageLayout.setVisibility(View.GONE);
                return false;
            }
            if (binding.fullScreenLayout.getVisibility() == View.VISIBLE) {
                binding.fullScreenLayout.setVisibility(View.GONE);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            if (!isCaptureImg) {
                return;
            } else {
                if (file.length() == 0) {
                    return;
                }
            }
            isCaptureImg = false;
        }
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
                uri = Uri.fromFile(file);
                uris.add(uris.size() - 1, uri);
                handleViewWithImgs();
                break;
            case REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM:
                uris.remove(null);
                uris.addAll(Matisse.obtainResult(data));
                handleViewWithImgs();
                break;
            case REQUEST_CODE_VIDEO_CAPTURE:
                uri = data.getData();
                handleViewWithVideo();
                break;
            case REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM:
                uri = Matisse.obtainResult(data).get(0);
                handleViewWithVideo();
                break;
            case REQUEST_CODE_ONLY_TEXT:
                break;
        }
        binding.publishContent.setEnabled(true);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        currentPosition = i;
        binding.imagePosition.setText((i + 1) + "/" + (uris.size() - 1));
        if (binding.fullScreenImageTopLayout.getVisibility() == View.VISIBLE) {
            binding.fullScreenImageTopLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    /**
     * 从相册中选择多张图片
     */
    private ImageAdapter adapter;

    private void handleViewWithImgs() {
        binding.videoLayout.setVisibility(View.GONE);
        if (uris.size() < 9 && !uris.contains(null)) {
            uris.add(null);
        }
        Log.e(tag, "uris'size is : " + uris.size());
        binding.imageRecycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        binding.imageRecycler.addItemDecoration(new SpacesItemDecoration(MDP_PX.dip2px(10f), MDP_PX.dip2px(10f)));
        binding.imageRecycler.setAdapter(adapter = new ImageAdapter());
    }

    //图片来自相机拍摄
    private void handleViewWithImgCap() {
        binding.videoLayout.setVisibility(View.GONE);
        binding.imgAdd.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(300, 600)
                .fitCenter()
                .into(binding.imgAdd);

    }

    //视频来自相机拍摄
    private void handleViewWithVideo() {
        binding.videoLayout.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(300, 600)
                .fitCenter()
                .into(binding.imgVideo);
        player = new MediaPlayer();
        binding.fullScreenPlay.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (player != null && player.isPlaying()) {
                    player.stop();
                }
            }
        });
    }

    /**
     * uri转换为绝对路径
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {

            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //全屏播放视频
    public void fullScreenPlayVideo(View view) {
        binding.fullScreenLayout.setVisibility(View.VISIBLE);
        playVideo();
    }

    private int currentPosition;

    private void fullScreenHandleImage() {
        pagerAdapter.clear();
        pagerAdapter.addAll(uris);
        pagerAdapter.notifyDataSetChanged();
        binding.previewViewpager.setCurrentItem(currentPosition);
        binding.imagePosition.setText((currentPosition + 1) + "/" + (uris.size() - 1));

        binding.fullScreenImageLayout.setVisibility(View.VISIBLE);
    }

    private void playVideo() {
        try {
            player.reset();
            player.setDataSource(this, uri);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.setDisplay(binding.fullScreenPlay.getHolder());
                    player.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideFullScreen();
    }

    private void hideFullScreen() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        if (binding.fullScreenLayout.getVisibility() == View.VISIBLE) {
            player.setDisplay(null);
            binding.fullScreenLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    public void hideFullScreen(View view) {
        hideFullScreen();
    }

    public void addVideo(View view) {
        showSelectDialog();
    }

    public void hideFullScreenImage(View view) {
        binding.fullScreenImageLayout.setVisibility(View.GONE);
    }

    public void deleteImg(View view) {
        new PromotionDialog(this, R.style.dialog, getString(R.string.confirm_delete_image), new PromotionDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    uris.remove(currentPosition);
                    if (!uris.contains(null)) {
                        uris.add(null);
                    }
                    pagerAdapter.remove(currentPosition);
                    pagerAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    if (pagerAdapter.getItems().size() == 0) {
                        binding.fullScreenImageLayout.setVisibility(View.GONE);
                        disenablePublish();
                    }
                    binding.imagePosition.setText((currentPosition + 1) + "/" + pagerAdapter.getItems().size());
                }
            }
        }).setTitle(getString(R.string.promotion)).show();

    }

    public void deleteVideo(View view) {
        new PromotionDialog(this, R.style.dialog, getString(R.string.confirm_delete_video), new PromotionDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    hideFullScreen();
                    uri = null;
                    binding.videoLayout.setVisibility(View.GONE);
                    binding.imgAdd.setVisibility(View.VISIBLE);
                    disenablePublish();
                }
            }
        }).setTitle(getString(R.string.promotion)).show();

    }

    private void disenablePublish() {
        if (null == binding.editContent.getText()) {
            binding.publishContent.setEnabled(false);
        }
    }

    private void showSelectDialog() {
        new SelectDialog(EditActivity.this).setOnSelectListener(this).show();
    }

    @Override
    public void selectCapture() {
        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE || requestCode == REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM) {
            captureImg();
        }
        if (requestCode == REQUEST_CODE_VIDEO_CAPTURE || requestCode == REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM) {
            captureVideo();
        }
    }

    @Override
    public void selectPhoto() {
        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE || requestCode == REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM) {
            size = uris.size() - 1;
            selectImgFromPhotoAlbum();
        }
        if (requestCode == REQUEST_CODE_VIDEO_CAPTURE || requestCode == REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM) {
            selectVideoFromPhotoAlbum();
        }
    }

    @Override
    public void onClick() {

    }

    public void publish(View view) {
        //TODO：绘制位图保存在本地，并将该记录上传至服务器
        Record record = new Record();
        Calendar calendar = Calendar.getInstance();
        record.setMonth(calendar.get(Calendar.MONTH) + 1);
        record.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        record.setContent(binding.editContent.getText().toString());
        if (requestCode == REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM | requestCode == REQUEST_CODE_VIDEO_CAPTURE) {
            String videoPath = FileUtils.getFilePathByUri(this, uri);
            record.setVideoUrl(videoPath);
        } else {
            uris.remove(null);
            if (uris.size() > 1) {
                int mWidth = MDP_PX.dip2px(100);
                int mHeight = MDP_PX.dip2px(100);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                Bitmap mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mBitmap);
                if (uris.size() == 2) {
                    Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
                    Bitmap bitmap1 = scaleBitmap(temp1, mWidth / 2, mHeight);
                    Rect src1 = new Rect(0, 0, bitmap1.getWidth() / 2 - 2, bitmap1.getHeight());
                    Rect dst1 = new Rect(0, 0, mWidth / 2 - 2, mHeight);

                    Bitmap temp2 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_beach);
                    Bitmap bitmap2 = scaleBitmap(temp2, mWidth / 2, mHeight);
                    Rect src2 = new Rect(0, 0, bitmap2.getWidth() / 2 - 2, bitmap2.getHeight());
                    Rect dst2 = new Rect(mWidth / 2 + 2, 0, mWidth, mHeight);

                    canvas.drawBitmap(bitmap1, src1, dst1, paint);
                    canvas.drawBitmap(bitmap2, src2, dst2, paint);
                } else if (uris.size() == 3) {
                    Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
                    Bitmap bitmap1 = scaleBitmap(temp1, mWidth / 2, mHeight);
                    Rect src1 = new Rect(0, 0, bitmap1.getWidth() / 2 - 2, bitmap1.getHeight());
                    Rect dst1 = new Rect(0, 0, mWidth / 2 - 2, mHeight);

                    Bitmap temp2 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_beach);
                    Bitmap bitmap2 = scaleBitmap(temp2, mWidth / 2, mHeight / 2);

                    Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
                    Rect dst2 = new Rect(mWidth / 2 + 2, 0, mWidth, mHeight / 2 - 2);

                    Bitmap temp3 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_salt_farm);
                    Bitmap bitmap3 = scaleBitmap(temp3, mWidth / 2, mHeight / 2);
                    Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
                    Rect dst3 = new Rect(mWidth / 2 + 2, mHeight / 2 + 2, mWidth, mHeight);

                    canvas.drawBitmap(bitmap1, src1, dst1, paint);
                    canvas.drawBitmap(bitmap2, src2, dst2, paint);
                    canvas.drawBitmap(bitmap3, src3, dst3, paint);
                } else if (uris.size() >= 4) {
                    Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
                    Bitmap bitmap1 = scaleBitmap(temp1, mWidth / 2, mHeight / 2);
                    Rect src1 = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
                    Rect dst1 = new Rect(0, 0, mWidth / 2 - 2, mHeight / 2 - 2);

                    Bitmap temp2 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_beach);
                    Bitmap bitmap2 = scaleBitmap(temp2, mWidth / 2, mHeight / 2);
                    Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
                    Rect dst2 = new Rect(mWidth / 2 + 2, 0, mWidth, mHeight / 2 - 2);

                    Bitmap temp3 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_salt_farm);
                    Bitmap bitmap3 = scaleBitmap(temp3, mWidth / 2, mHeight / 2);
                    Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
                    Rect dst3 = new Rect(0, mHeight / 2 + 2, mWidth / 2 - 2, mHeight);

                    Bitmap temp4 = BitmapFactory.decodeResource(getResources(), R.drawable.india_chennai_highway);
                    Bitmap bitmap4 = scaleBitmap(temp4, mWidth / 2, mHeight / 2);
                    Rect src4 = new Rect(0, 0, bitmap4.getWidth(), bitmap4.getHeight());
                    Rect dst4 = new Rect(mWidth / 2 + 2, mHeight / 2 + 2, mWidth, mHeight);

                    canvas.drawBitmap(bitmap1, src1, dst1, paint);
                    canvas.drawBitmap(bitmap2, src2, dst2, paint);
                    canvas.drawBitmap(bitmap3, src3, dst3, paint);
                    canvas.drawBitmap(bitmap4, src4, dst4, paint);
                }
                canvas.save();
                canvas.restore();
                File file = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/Memory/images/" + System.currentTimeMillis() + ".png");// 保存到sdcard根目录下，文件名为share_pic.png
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    record.setSmallImgUrl(file.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String imgPath = FileUtils.getFilePathByUri(this, uris.get(0));
                record.setSmallImgUrl(imgPath);
            }
            record.setUris(uris);
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_EDIT_RESULT, record);
        setResult(RESULT_CODE, intent);
        finish();
    }

    private Bitmap scaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Matrix m = new Matrix();

        final int width = src.getWidth();
        final int height = src.getHeight();
        if (width != dstWidth || height != dstHeight) {
            final float sx = dstWidth / (float) width;
            final float sy = dstHeight / (float) height;
            float scale = Math.max(sx, sy);
            m.setScale(scale, scale);
        }
        return Bitmap.createBitmap(src, 0, 0, width, height, m, true);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHoder> {
        @NonNull
        @Override
        public ImageHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ImageHoder((ImgItemBinding) DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.img_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ImageHoder imageHoder, int i) {
            imageHoder.setData(i);
        }

        @Override
        public int getItemCount() {
            return uris.size();
        }
    }

    private class ImageHoder extends RecyclerView.ViewHolder {
        ImgItemBinding binding;

        public ImageHoder(ImgItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(final int position) {
            final Uri uri = uris.get(position);
            if (uri != null) {
                Glide.with(EditActivity.this)
                        .load(uri)
                        .centerCrop()
                        .into(binding.image);
                binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPosition = position;
                        fullScreenHandleImage();
                    }
                });
            } else {
                Glide.with(EditActivity.this)
                        .load(R.drawable.img_add)
                        .centerCrop()
                        .into(binding.image);
                binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(tag, "添加图片");
                        showSelectDialog();
                    }
                });
            }
        }

    }

}
