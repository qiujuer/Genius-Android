package net.qiujuer.sample.genius;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import net.qiujuer.genius.graphics.Blur;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

public class BlurClipActivity extends AppCompatActivity {
    private Bitmap mSrc;
    private ImageView mView;
    private Loading mLoading;
    private Button mButton;
    private boolean isBlurring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_clip);

        mButton = (Button) findViewById(R.id.btn_todo1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurStart();
            }
        });
        mView = (ImageView) findViewById(R.id.iv_show);
        mLoading = (Loading) findViewById(R.id.loading);
        load();
    }


    private void load() {
        try {
            // Find Bitmap
            mSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.wallpaper);
            mView.setImageBitmap(mSrc);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            finish();
        }
    }


    private void blurStart() {
        if (isBlurring)
            return;
        isBlurring = true;
        mLoading.start();
        mButton.setVisibility(View.GONE);
        Run.onBackground(new Action() {
            @Override
            public void call() {
                Blur.onStackBlurClip(mSrc, 50);
                blurEnd();
            }
        });
    }

    private void blurEnd() {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                mView.postInvalidate();
                mLoading.stop();
                mButton.setVisibility(View.VISIBLE);
                isBlurring = false;
            }
        });
    }

    private void clear() {
        Drawable drawable = mView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            ((ImageView) findViewById(R.id.iv_show)).setImageDrawable(null);
            ((BitmapDrawable) drawable).getBitmap().recycle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }
}
