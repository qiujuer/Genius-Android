package net.qiujuer.sample.genius;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.qiujuer.genius.blur.StackBlur;


public class BlurActivity extends AppCompatActivity {
    private static final int SCALE_FACTOR = 6;
    private boolean mCompress;
    private TextView mTime;
    private Bitmap mBitmap, mCompressBitmap;
    private ImageView mImageJava, mImageJniPixels, mImageJniBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

        // init bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        initBlur();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyBlur();
    }


    private void initBlur() {
        // Find Bitmap
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_blur);
        mImageJava = (ImageView) findViewById(R.id.image_blur_java);
        mImageJniPixels = (ImageView) findViewById(R.id.image_blur_jni_pixels);
        mImageJniBitmap = (ImageView) findViewById(R.id.image_blur_jni_bitmap);
        mTime = (TextView) findViewById(R.id.text_blur_time);

        // Init src image
        ((ImageView) findViewById(R.id.image_blur_self)).setImageBitmap(mBitmap);

        // Compress and Save Bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(1.0f / SCALE_FACTOR, 1.0f / SCALE_FACTOR);
        // New Compress bitmap
        mCompressBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        // Set On OnCheckedChangeListener
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_blur_isCompress);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCompress = isChecked;
                applyBlur();
            }
        });
    }


    private void applyBlur() {
        // First clear
        clearDrawable();

        // Run Thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Blur Time: ");
                    for (int i = 1; i < 4; i++) {
                        sb.append(blur(i)).append(" ");
                    }

                    BlurActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTime.setText(sb.toString());
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

        // Is Compress
        float radius = 20;
        Bitmap overlay = mBitmap;
        if (mCompress) {
            radius = 3;
            overlay = mCompressBitmap;
        }

        // Java
        if (i == 1)
            overlay = StackBlur.blur(overlay, (int) radius, false);
            // Bitmap JNI Native
        else if (i == 2)
            overlay = StackBlur.blurNatively(overlay, (int) radius, false);
            // Pixels JNI Native
        else if (i == 3)
            overlay = StackBlur.blurNativelyPixels(overlay, (int) radius, false);

        // Show
        showDrawable(view, overlay);

        return System.currentTimeMillis() - startMs;
    }

    private void clearDrawable() {
        mImageJava.setImageBitmap(null);
        mImageJniPixels.setImageBitmap(null);
        mImageJniBitmap.setImageBitmap(null);
    }

    private void showDrawable(final ImageView view, final Bitmap overlay) {
        BlurActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(overlay);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
