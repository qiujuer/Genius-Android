package net.qiujuer.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.qiujuer.genius.app.BlurKit;
import net.qiujuer.genius.app.ToolKit;


public class BlurActivity extends Activity {
    private LinearLayout linearLayout;
    private boolean scale;
    private TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        status = (TextView) findViewById(R.id.text_status);
        applyBlur();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void applyBlur() {
        // 清理子控件背景，消除干扰
        clearDrawable();
        //添加监听
        linearLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                linearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                linearLayout.buildDrawingCache();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("耗时：");
                            for (int i = 1; i < 4; i++) {
                                Bitmap bmp = linearLayout.getDrawingCache();
                                sb.append(blur(bmp, i)).append(" ");
                            }

                            ToolKit.runOnMainThreadAsync(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText(sb.toString());
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.setDaemon(true);
                thread.start();
                return true;
            }
        });
    }


    private long blur(Bitmap bkg, int i) {
        View view = null;
        if (i == 1)
            view = findViewById(R.id.text1);
        else if (i == 2)
            view = findViewById(R.id.text2);
        else if (i == 3)
            view = findViewById(R.id.text3);

        long startMs = System.currentTimeMillis();

        //设置参数：是否压缩 模糊半径
        float scaleFactor = 1;
        float radius = 20;
        if (scale) {
            scaleFactor = 8;
            radius = 2;
        }

        // 剪切 背景
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        //模糊
        if (i == 1)
            overlay = BlurKit.fastBlurInJava(overlay, (int) radius, true);
        else if (i == 2)
            overlay = BlurKit.fastBlurInJniArray(overlay, (int) radius, true);
        else if (i == 3)
            overlay = BlurKit.fastBlurInJniBitmap(overlay, (int) radius, true);

        setDrawable(view, overlay);

        return System.currentTimeMillis() - startMs;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void clearDrawable() {
        findViewById(R.id.text1).setBackground(null);
        findViewById(R.id.text2).setBackground(null);
        findViewById(R.id.text3).setBackground(null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setDrawable(final View view, final Bitmap overlay) {
        ToolKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                //Api Build.VERSION_CODES.JELLY_BEAN 以上支持直接setBackground
                view.setBackground(new BitmapDrawable(getResources(), overlay));
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
                item.setTitle("直接模糊背景");
            else
                item.setTitle("压缩背景后模糊");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
