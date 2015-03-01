package net.qiujuer.genius.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.qiujuer.genius.app.BlurKit;
import net.qiujuer.genius.widget.GeniusAbsSeekBar;
import net.qiujuer.genius.widget.GeniusCheckBox;
import net.qiujuer.genius.widget.GeniusSeekBar;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private static final int SCALE_FACTOR = 6;
    private boolean mCompress;
    private TextView mTime;
    private Bitmap mBitmap, mCompressBitmap;
    private ImageView mImageJava, mImageJniPixels, mImageJniBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCheckBox();
        initEditText();
        initBlur();

        // Init GeniusButton and Test delayClick
        findViewById(R.id.button_skip_isDelay).setOnClickListener(this);
        findViewById(R.id.button_skip_disDelay).setOnClickListener(this);

        GeniusSeekBar seekBar = (GeniusSeekBar) findViewById(R.id.discrete1);
        seekBar.setNumericTransformer(new GeniusAbsSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value * 100;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyBlur();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_case) {
            this.startActivity(new Intent(this, CaseActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initCheckBox() {
        GeniusCheckBox blue = (GeniusCheckBox) findViewById(R.id.checkbox_enable_blue);
        GeniusCheckBox strawberryIce = (GeniusCheckBox) findViewById(R.id.checkbox_enable_strawberryIce);

        blue.setOnCheckedChangeListener(new GeniusCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(GeniusCheckBox checkBox, boolean isChecked) {
                (findViewById(R.id.checkbox_disEnable_blue)).setEnabled(isChecked);
            }
        });

        strawberryIce.setOnCheckedChangeListener(new GeniusCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(GeniusCheckBox checkBox, boolean isChecked) {
                (findViewById(R.id.checkbox_disEnable_strawberryIce)).setEnabled(isChecked);
            }
        });
    }

    private void initEditText() {
        GeniusCheckBox checkBox = (GeniusCheckBox) findViewById(R.id.checkbox_editText_isEnable);
        checkBox.setOnCheckedChangeListener(new GeniusCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(GeniusCheckBox checkBox, boolean isChecked) {
                (findViewById(R.id.editText_fill)).setEnabled(isChecked);
                (findViewById(R.id.editText_box)).setEnabled(isChecked);
                (findViewById(R.id.editText_transparent)).setEnabled(isChecked);
                (findViewById(R.id.editText_line)).setEnabled(isChecked);
                (findViewById(R.id.editText_noHave)).setEnabled(isChecked);
                (findViewById(R.id.editText_radius)).setEnabled(isChecked);
                (findViewById(R.id.editText_title)).setEnabled(isChecked);
            }
        });
    }

    private void initBlur() {
        // Find Bitmap
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_blur);
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
        GeniusCheckBox checkBox = (GeniusCheckBox) findViewById(R.id.checkbox_blur_isCompress);
        checkBox.setOnCheckedChangeListener(new GeniusCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(GeniusCheckBox checkBox, boolean isChecked) {
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

                    MainActivity.this.runOnUiThread(new Runnable() {
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
            overlay = BlurKit.blur(overlay, (int) radius, false);
            // Bitmap JNI Native
        else if (i == 2)
            overlay = BlurKit.blurNatively(overlay, (int) radius, false);
            // Pixels JNI Native
        else if (i == 3)
            overlay = BlurKit.blurNativelyPixels(overlay, (int) radius, false);

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
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setImageBitmap(overlay);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, TwoActivity.class);
        startActivity(intent);
    }
}
