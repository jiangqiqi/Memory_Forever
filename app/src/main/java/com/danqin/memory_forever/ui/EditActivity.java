package com.danqin.memory_forever.ui;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.danqin.memory_forever.databinding.EditLayoutBinding;
import com.danqin.memory_forever.databinding.ImgItemBinding;
import com.danqin.memory_forever.utils.MDP_PX;
import com.danqin.memory_forever.view.dialog.PromotionDialog;
import com.danqin.memory_forever.view.SpacesItemDecoration;
import com.danqin.memory_forever.view.dialog.SelectDialog;
import com.danqin.memory_forever.view.preview.PreviewItemFragment;
import com.danqin.memory_forever.view.preview.PreviewPagerAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;

import java.io.IOException;
import java.util.ArrayList;

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
                    pagerAdapter.remove(currentPosition);
                    pagerAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    if (pagerAdapter.getItems().size() == 0) {
                        binding.fullScreenImageLayout.setVisibility(View.GONE);
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
                }
            }
        }).setTitle(getString(R.string.promotion)).show();

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

    private void showImageDialog() {

    }


}
