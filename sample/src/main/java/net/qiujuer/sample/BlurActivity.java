package net.qiujuer.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import net.qiujuer.genius.app.BlurKit;
import net.qiujuer.genius.app.ToolKit;


public class BlurActivity extends ActionBarActivity {
    private static final int SCALE_FACTOR = 6;
    private boolean scale;
    private TextView mStatus;
    private Bitmap mBitmap, mCompressBitmap;
    private ImageView mImageJava, mImageJniPixels, mImageJniBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_blur);
        mImageJava = (ImageView) findViewById(R.id.image_blur_java);
        mImageJniPixels = (ImageView) findViewById(R.id.image_blur_jni_pixels);
        mImageJniBitmap = (ImageView) findViewById(R.id.image_blur_jni_bitmap);
        mStatus = (TextView) findViewById(R.id.text_status);

        ImageView self = ((ImageView) findViewById(R.id.image_blur_self));
        self.setImageBitmap(mBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();

        compress();
        applyBlur();
    }

    private void compress() {
        // 进行压缩，并保存压缩后的Bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(1.0f / SCALE_FACTOR, 1.0f / SCALE_FACTOR);
        // new bitmap
        mCompressBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
    }


    private void applyBlur() {
        // 清理子控件背景，消除干扰
        clearDrawable();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Time：");
                    for (int i = 1; i < 4; i++) {
                        sb.append(blur(i)).append(" ");
                    }

                    ToolKit.runOnMainThreadAsync(new Runnable() {
                        @Override
                        public void run() {
                            mStatus.setText(sb.toString());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    private long blur(int i) {
        ImageView view = null;
        if (i == 1)
            view = mImageJava;
        else if (i == 2)
            view = mImageJniPixels;
        else if (i == 3)
            view = mImageJniBitmap;

        long startMs = System.currentTimeMillis();

        //是否压缩 模糊半径
        float radius = 20;
        Bitmap overlay = mBitmap;
        if (scale) {
            radius = 3;
            overlay = mCompressBitmap;
        }

        // Java 直接模糊
        if (i == 1)
            overlay = BlurKit.blur(overlay, (int) radius, false);
            // 传递 Bitmap 到 JNI 模糊
        else if (i == 2)
            overlay = BlurKit.blurNatively(overlay, (int) radius, false);
            // 传递 图片的像素点集合到 JNI 模糊
        else if (i == 3)
            overlay = BlurKit.blurNativelyPixels(overlay, (int) radius, false);

        // 显示
        setDrawable(view, overlay);

        return System.currentTimeMillis() - startMs;
    }

    private void clearDrawable() {
        mImageJava.setImageBitmap(null);
        mImageJniPixels.setImageBitmap(null);
        mImageJniBitmap.setImageBitmap(null);
    }

    private void setDrawable(final ImageView view, final Bitmap overlay) {
        ToolKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(overlay);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blur, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_scale) {
            scale = !scale;
            applyBlur();

            if (scale)
                item.setTitle("直接背景");
            else
                item.setTitle("压缩后模糊");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
