package net.qiujuer.sample.genius;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.qiujuer.genius.graphics.Blur;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


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

    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, options1);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    private void initBlur() {
        // Find Bitmap
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_blur);
        //Bitmap.Config config = mBitmap.getConfig();
        //mBitmap = mBitmap.copy(Bitmap.Config.RGB_565, true);
        //mBitmap = compressImage(mBitmap);


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

        mCompressBitmap = mCompressBitmap.copy(Bitmap.Config.RGB_565, true);

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
        Bitmap overlay = mBitmap.copy(mBitmap.getConfig(), true);
        if (mCompress) {
            radius = 3;
            overlay = mCompressBitmap.copy(mCompressBitmap.getConfig(), true);
        }


        if (i == 1) {
            // Java
            overlay = Blur.onStackBlurJava(overlay, (int) radius);
        } else if (i == 2) {
            // Pixels JNI Native
            int w = overlay.getWidth();
            int h = overlay.getHeight();
            int[] pix = new int[w * h];
            overlay.getPixels(pix, 0, w, 0, 0, w, h);
            // Jni Pixels Blur
            pix = Blur.onStackBlurPixels(pix, w, h, (int) radius);
            overlay.setPixels(pix, 0, w, 0, 0, w, h);
        } else if (i == 3) {
            // Bitmap JNI Native
            overlay = Blur.onStackBlur(overlay, (int) radius);
        }

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
