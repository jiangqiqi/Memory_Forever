package com.danqin.memory_forever.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.danqin.memory_forever.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomImgView extends ImageView {

    public CustomImgView(Context context) {
        super(context);
    }

    public CustomImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomImgView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int mWidth;
    private int mHeight;
    private Paint paint;

//    private Bitmap bitmap1;
//    private Rect src1;
//    private Rect dst1;
//
//    private Bitmap bitmap2;
//    private Rect src2;
//    private Rect dst2;
//
//    private Bitmap bitmap3;
//    private Rect src3;
//    private Rect dst3;
//
//    private Bitmap bitmap4;
//    private Rect src4;
//    private Rect dst4;

    private List<String> urls = new ArrayList<>();


    public void setImages(List<String> urls) {
        this.urls = urls;
    }

    private int type;

    public void setType(int type) {
        Log.e("JiangLiang", "setType.............." + type + ",width is : " + mWidth + ",height is : " + mHeight);
        this.type = type;
        if (mWidth != 0 && mHeight != 0) {
            createBitmap();
        }
    }

    private Bitmap scaleBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) {
        Matrix m = new Matrix();

        final int width = src.getWidth();
        final int height = src.getHeight();
        if (width != dstWidth || height != dstHeight) {
            final float sx = dstWidth / (float) width;
            final float sy = dstHeight / (float) height;
            float scale = Math.max(sx, sy);
            m.setScale(scale, scale);
        }
        return Bitmap.createBitmap(src, 0, 0, width, height, m, filter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        paint = new Paint();
        paint.setAntiAlias(true);
        createBitmap();
    }

    private Bitmap mBitmap;

    private void createBitmap() {
        long begin = System.currentTimeMillis();
        if (urls == null) {
            return;
        }
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        if (type == 1 || urls.size() == 1) {
            Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
            Bitmap bitmap1 = scaleBitmap(temp1, mWidth, mHeight, true);
            Rect src1 = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
            Rect dst1 = new Rect(0, 0, mWidth, mHeight);
            canvas.drawBitmap(bitmap1, src1, dst1, paint);
        }

        if (type == 2 || urls.size() == 2) {
            Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
            Bitmap bitmap1 = scaleBitmap(temp1, mWidth / 2, mHeight, true);
            Rect src1 = new Rect(0, 0, bitmap1.getWidth() / 2 - 2, bitmap1.getHeight());
            Rect dst1 = new Rect(0, 0, mWidth / 2 - 2, mHeight);

            Bitmap temp2 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_beach);
            Bitmap bitmap2 = scaleBitmap(temp2, mWidth / 2, mHeight, true);
            Rect src2 = new Rect(0, 0, bitmap2.getWidth() / 2 - 2, bitmap2.getHeight());
            Rect dst2 = new Rect(mWidth / 2 + 2, 0, mWidth, mHeight);

            canvas.drawBitmap(bitmap1, src1, dst1, paint);
            canvas.drawBitmap(bitmap2, src2, dst2, paint);
        }

        if (type == 3 || urls.size() == 3) {
            Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
            Bitmap bitmap1 = scaleBitmap(temp1, mWidth / 2, mHeight, true);
            Rect src1 = new Rect(0, 0, bitmap1.getWidth() / 2 - 2, bitmap1.getHeight());
            Rect dst1 = new Rect(0, 0, mWidth / 2 - 2, mHeight);

            Bitmap temp2 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_beach);
            Bitmap bitmap2 = scaleBitmap(temp2, mWidth / 2, mHeight / 2, true);

            Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
            Rect dst2 = new Rect(mWidth / 2 + 2, 0, mWidth, mHeight / 2 - 2);

            Bitmap temp3 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_salt_farm);
            Bitmap bitmap3 = scaleBitmap(temp3, mWidth / 2, mHeight / 2, true);
            Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
            Rect dst3 = new Rect(mWidth / 2 + 2, mHeight / 2 + 2, mWidth, mHeight);

            canvas.drawBitmap(bitmap1, src1, dst1, paint);
            canvas.drawBitmap(bitmap2, src2, dst2, paint);
            canvas.drawBitmap(bitmap3, src3, dst3, paint);
        }
        if (type >= 4 || urls.size() >= 4) {
            Bitmap temp1 = BitmapFactory.decodeResource(getResources(), R.drawable.india_tanjore_market_merchant);
            Bitmap bitmap1 = scaleBitmap(temp1, mWidth / 2, mHeight / 2, true);
            Rect src1 = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
            Rect dst1 = new Rect(0, 0, mWidth / 2 - 2, mHeight / 2 - 2);

            Bitmap temp2 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_beach);
            Bitmap bitmap2 = scaleBitmap(temp2, mWidth / 2, mHeight / 2, true);
            Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
            Rect dst2 = new Rect(mWidth / 2 + 2, 0, mWidth, mHeight / 2 - 2);

            Bitmap temp3 = BitmapFactory.decodeResource(getResources(), R.drawable.india_pondicherry_salt_farm);
            Bitmap bitmap3 = scaleBitmap(temp3, mWidth / 2, mHeight / 2, true);
            Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
            Rect dst3 = new Rect(0, mHeight / 2 + 2, mWidth / 2 - 2, mHeight);

            Bitmap temp4 = BitmapFactory.decodeResource(getResources(), R.drawable.india_chennai_highway);
            Bitmap bitmap4 = scaleBitmap(temp4, mWidth / 2, mHeight / 2, true);
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
        Log.e("JiangLiang", "create bitmap take time is : " + (System.currentTimeMillis() - begin));
    }

    //将绘制的位图存储在本地
    private void save2Local(Canvas canvas) {
        canvas.save();
        canvas.restore();

        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/Memory/images/" + System.currentTimeMillis() + ".png");// 保存到sdcard根目录下，文件名为share_pic.png
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
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
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (urls == null) {
            return;
        }
        canvas.drawBitmap(mBitmap, 0, 0, paint);
    }
}
