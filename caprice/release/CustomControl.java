import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.AutoEffect;
import net.qiujuer.genius.ui.drawable.factory.ClipFilletFactory;

public class CustomControl extends FrameLayout implements TouchEffectDrawable.PerformClicker {
    private TouchEffectDrawable mTouchDrawable;

    public CustomControl(Context context) {
        super(context);
        init();
    }

    public CustomControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomControl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        if (mTouchDrawable == null) {
            mTouchDrawable = new TouchEffectDrawable();
            mTouchDrawable.setCallback(this);
            mTouchDrawable.setPerformClicker(this);
        }

        // set the touch effect
        mTouchDrawable.setEffect(new AutoEffect());
        //mTouchDrawable.setEffect(new EaseEffect());
        //mTouchDrawable.setEffect(new PressEffect());
        //mTouchDrawable.setEffect(new RippleEffect());

        // set the touch color
        mTouchDrawable.getPaint().setColor(0x30000000);

        // set the touch duration rate
        mTouchDrawable.setEnterDuration(0.8f);
        mTouchDrawable.setExitDuration(0.8f);

        if (!this.isInEditMode()) {
            float[] radius = new float[]{2, 2, 2, 2, 2, 2, 2, 2};
            ClipFilletFactory touchFactory = new ClipFilletFactory(radius);
            // ClipFilletFactory touchFactory = new ClipFilletFactory(2);

            // Touch factory
            mTouchDrawable.setClipFactory(touchFactory);
        }

        //setBackground(mTouchDrawable);
    }

    @Override
    public void perform() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                performClick();
            }
        };

        if (!this.post(runnable)) {
            performClick();
        }
    }

    @Override
    public boolean performClick() {
        final TouchEffectDrawable d = mTouchDrawable;

        if (d != null) {
            return d.isPerformClick() && super.performClick();
        } else
            return super.performClick();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        TouchEffectDrawable drawable = mTouchDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable = mTouchDrawable;
        return (drawable != null && who == drawable) || super.verifyDrawable(who);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw the effect on the image
        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null) {
            d.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null && isEnabled()) {
            d.onTouch(event);
            super.onTouchEvent(event);
            return true;
        }

        return super.onTouchEvent(event);
    }
}
