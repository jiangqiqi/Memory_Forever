package com.danqin.memory_forever.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.danqin.memory_forever.view.SpacesItemDecoration;
import com.danqin.memory_forever.view.preview.PreviewPagerAdapter;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;

import java.io.IOException;
import java.util.ArrayList;

public class EditActivity extends BaseActivity implements ViewPager.OnPageChangeListener, OnFragmentInteractionListener {

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
        requestCode = intent.getIntExtra(RecordsActivity.KEY_REQUEST_CODE, 0);
        uri = intent.getParcelableExtra(RecordsActivity.KEY_RESOURCE_URI);
        switch (requestCode) {
            case RecordsActivity.REQUEST_CODE_IMAGE_CAPTURE:
                handleViewWithImgCap();
                break;
            case RecordsActivity.REQUEST_CODE_VIDEO_CAPTURE:
                handleViewWithVideo();
                break;
            case RecordsActivity.REQUEST_CODE_IMAGE_SELECT_FROM_PHOTO_ALBUM:
                uris = intent.getParcelableArrayListExtra(RecordsActivity.KEY_RESOURCE_URIS);
                handleViewWithImgs();
                break;
            case RecordsActivity.REQUEST_CODE_VIDEO_SELECT_FROM_PHOTO_ALBUM:
                handleViewWithVideo();
                break;
        }
        pagerAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        binding.previewViewpager.setAdapter(pagerAdapter);
        binding.previewViewpager.addOnPageChangeListener(this);
    }

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
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        binding.imagePosition.setText((i + 1) + "/" + (uris.size() - 1));
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
        if (uris.size() < 9) {
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
        binding.fullScreenImageLayout.setVisibility(View.VISIBLE);
        binding.imagePosition.setText((currentPosition + 1) + "/" + (uris.size() - 1));
        pagerAdapter.clear();
        pagerAdapter.addAll(uris);
        pagerAdapter.notifyDataSetChanged();
        binding.previewViewpager.setCurrentItem(currentPosition);
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

    }

    //执行删除操作
    public void delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要删除这段视频吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                hideFullScreen();
                uri = null;
                binding.videoLayout.setVisibility(View.GONE);
                binding.imgAdd.setVisibility(View.VISIBLE);
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

    public void hideFullScreenImage(View view) {
        binding.fullScreenImageLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick() {

    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHoder> {
        @NonNull
        @Override
        public ImageHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ImageHoder((ImgItemBinding) DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.img_item, null, false));
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
                        .override(300, 300)
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
                        .override(300, 300)
                        .into(binding.image);
            }


        }

    }


}
