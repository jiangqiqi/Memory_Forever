package com.danqin.memory_forever.view.preview;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Uri> mItems = new ArrayList<>();
    public PreviewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return PreviewItemFragment.newInstance(mItems.get(position));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void addAll(List<Uri> items) {
        mItems.addAll(items);
        mItems.remove(null);
    }

    public void clear(){
        mItems.clear();
    }

    public void remove(Uri uri){
        mItems.remove(uri);
    }

}
