package net.qiujuer.genius.ui.drawable;


import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public abstract class TouchAnimateDrawable extends TouchDrawable {
    protected static final long FRAME_DURATION = 16;

    // Base Values
    protected static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.8f);
    protected static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    // Time
    protected static final int IN_ANIM_DURATION = 280;
    protected static final int OUT_ANIM_DURATION = 160;


    private boolean isAnimatingIn = false;

    private Interpolator mInterpolator = DECELERATE_INTERPOLATOR;
    private long mStartTime;
    private int mDuration = IN_ANIM_DURATION;


    @Override
    protected void onTouchDown(float x, float y) {
        // Cancel and Start new animation
        cancelAnim();
        startInAnim();
    }

    @Override
    protected void onTouchReleased(float x, float y) {
        if (!isAnimatingIn) {
            startOutAnim();
        }
    }

    @Override
    protected void onTouchMove(float x, float y) {

    }

    private void startInAnim() {
        isAnimatingIn = true;
        isRunning = true;

        mDuration = IN_ANIM_DURATION;
        mInterpolator = DECELERATE_INTERPOLATOR;

        // Start animation
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mInAnim, mStartTime);
    }

    private void startOutAnim() {

        mDuration = OUT_ANIM_DURATION;
        mInterpolator = ACCELERATE_INTERPOLATOR;

        // Start animation
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mOutAnim, mStartTime);
    }

    private void cancelAnim() {
        unscheduleSelf(mInAnim);
        unscheduleSelf(mOutAnim);
        isRunning = false;
    }

    private final Runnable mInAnim = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff <= mDuration) {
                float interpolation = mInterpolator.getInterpolation((float) diff / (float) mDuration);
                // Notify
                onInAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {

                unscheduleSelf(this);

                // Notify
                onInAnimateUpdate(1f);
                invalidateSelf();

                // End
                isAnimatingIn = false;
                // Call end
                onInAnimateEnd();
                // Is un touch auto startOutAnim()
                if (isTouchReleased) startOutAnim();
            }
        }
    };

    private final Runnable mOutAnim = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff <= mDuration) {
                float interpolation = mInterpolator.getInterpolation((float) diff / (float) mDuration);
                // Notify
                onOutAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {

                unscheduleSelf(this);

                // Notify
                onOutAnimateUpdate(1f);
                invalidateSelf();

                // End
                isRunning = false;
                // Call end
                onOutAnimateEnd();
                // Click
                performClick();
            }
        }
    };

    protected abstract void onInAnimateUpdate(float factor);

    protected abstract void onOutAnimateUpdate(float factor);

    protected abstract void onInAnimateEnd();

    protected abstract void onOutAnimateEnd();
}
