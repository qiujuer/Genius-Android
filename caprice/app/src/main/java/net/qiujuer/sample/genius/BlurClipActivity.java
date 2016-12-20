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

public class BlurClipActivity extends AppCompatActivity {
    private Bitmap mSrc1;
    private Bitmap mSrc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_clip);

        try {
            // Find Bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.walkthrough);
            mSrc1 = bitmap.copy(bitmap.getConfig(), true);
            mSrc2 = bitmap.copy(bitmap.getConfig(), true);
            bitmap.recycle();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            finish();
        }

        findViewById(R.id.btn_todo1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipBlur();
            }
        });
        findViewById(R.id.btn_todo2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blur();
            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
    }

    private void clear() {
        Drawable drawable = ((ImageView) findViewById(R.id.iv_show1)).getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            ((ImageView) findViewById(R.id.iv_show1)).setImageDrawable(null);
            ((BitmapDrawable) drawable).getBitmap().recycle();
        }
    }

    private void clipBlur() {
        ((ImageView) findViewById(R.id.iv_show1)).setImageBitmap(Blur.onStackBlurClip(mSrc1, 80));
    }

    private void blur() {
        ((ImageView) findViewById(R.id.iv_show1)).setImageBitmap(Blur.onStackBlur(mSrc2, 80));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSrc1 != null && !mSrc1.isRecycled())
            mSrc1.recycle();
        if (mSrc2 != null && !mSrc2.isRecycled())
            mSrc2.recycle();

        System.gc();
    }
}
