package com.danqin.memory_forever.view.preview;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Uri> mItems = new ArrayList<>();
    private List<String> imgUrls = new ArrayList<>();
    private PreviewItemFragment.OnPagerClickListener listener;
    public PreviewPagerAdapter(FragmentManager manager, PreviewItemFragment.OnPagerClickListener listener) {
        super(manager);
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        if (imgUrls.size() == 0) {
            return PreviewItemFragment.newInstance(mItems.get(position)).setOnPagerClickListener(listener);
        }else{
            return PreviewItemFragment.newInstance(imgUrls.get(position)).setOnPagerClickListener(listener);
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void addAll(List<Uri> items) {
        mItems.addAll(items);
        mItems.remove(null);
    }

    public void addAllImgs(List<String> imgUrls){
        this.imgUrls.addAll(imgUrls);
    }

    public List<Uri> getItems(){
        return mItems;
    }
    public void clear(){
        mItems.clear();
    }

    public void remove(Uri uri){
        mItems.remove(uri);
    }

    public void remove(int index){
        mItems.remove(index);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view ==(View) object;
//    }
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        ((PreviewViewPager)container).removeView((View) object);
//    }
}
