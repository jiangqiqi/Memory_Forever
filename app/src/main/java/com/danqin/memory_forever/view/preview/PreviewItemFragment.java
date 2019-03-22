package com.danqin.memory_forever.view.preview;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.danqin.memory_forever.R;
import com.danqin.memory_forever.databinding.PreviewItemBinding;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;


public class PreviewItemFragment extends Fragment implements View.OnClickListener {


    public static final String ARGS_URI = "args_uri";
    public static final String ARGS_URL = "args_url";
    private PreviewItemBinding binding;
    private Uri uri;
    private OnPagerClickListener listener;
    private String imgUrl;

    public static PreviewItemFragment newInstance(Uri uri) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_URI, uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PreviewItemFragment newInstance(String imgUrl) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_URL, imgUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    public PreviewItemFragment setOnPagerClickListener(OnPagerClickListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnPagerClickListener {
        void pagerClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.preview_item, container, false);
        binding.imageView.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uri = getArguments().getParcelable(ARGS_URI);
        if (uri != null) {
            Glide.with(getContext())
                    .load(uri)
                    .centerCrop()
                    .into(binding.imageView);
        }
        imgUrl = getArguments().getString(ARGS_URL);
        if (imgUrl != null) {
            Glide.with(getContext())
                    .load(imgUrl)
                    .centerCrop()
                    .into(binding.imageView);
        }
    }

    @Override
    public void onClick(View v) {
        Log.e(getClass().getName(), "on click   " + uri);
        if (null != listener) {
            listener.pagerClick();
        }
    }
}
