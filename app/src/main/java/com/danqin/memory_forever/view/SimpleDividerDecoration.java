package com.danqin.memory_forever.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleDividerDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight=2;
    private Paint dividerPaint;

    public SimpleDividerDecoration() {
        dividerPaint = new Paint();
        dividerPaint.setColor(0xfff5f5f5);
        this.dividerHeight = 2;
    }
    public SimpleDividerDecoration(int dividerHeight) {
        dividerPaint = new Paint();
        dividerPaint.setColor(0xfff5f5f5);
        this.dividerHeight = dividerHeight;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }
}