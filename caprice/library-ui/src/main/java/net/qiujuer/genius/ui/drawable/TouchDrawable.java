package net.qiujuer.genius.ui.drawable;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;


public abstract class TouchDrawable extends Drawable {
    protected final Paint mPaint;
    protected boolean isTouchReleased = false;
    protected boolean isPerformClick = false;
    protected boolean isRunning = false;
    private WeakReference<PerformClicker> mPerformClicker = null;

    private int mAlpha = 255;

    public TouchDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public ColorFilter getColorFilter() {
        return mPaint.getColorFilter();
    }

    @Override
    public void clearColorFilter() {
        mPaint.setColorFilter(null);
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * Modulate color Alpha
     *
     * @param alpha color alpha
     * @return modulate colorAlpha and this alpha
     */
    protected int modulateAlpha(int alpha) {
        int scale = mAlpha + (mAlpha >> 7);
        return alpha * scale >> 8;
    }

    public void onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                isTouchReleased = true;
                onTouchReleased(event.getX(), event.getY());
            }
            break;
            case MotionEvent.ACTION_DOWN: {
                isTouchReleased = false;
                onTouchDown(event.getX(), event.getY());
            }
            break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
        }
    }

    protected abstract void onTouchDown(float x, float y);

    protected abstract void onTouchReleased(float x, float y);

    protected abstract void onTouchMove(float x, float y);

    public boolean isPerformClick() {
        if (!isPerformClick) {
            isPerformClick = true;
            return false;
        } else {
            return !isRunning;
        }
    }

    protected void performClick() {
        if (isPerformClick) {
            PerformClicker clicker = getPerformClicker();
            if (clicker != null) {
                clicker.perform();
            }
        }
    }

    public final void setPerformClicker(PerformClicker clicker) {
        mPerformClicker = new WeakReference<PerformClicker>(clicker);
    }

    public PerformClicker getPerformClicker() {
        if (mPerformClicker != null) {
            return mPerformClicker.get();
        }
        return null;
    }


    public interface PerformClicker {
        void perform();
    }

}
