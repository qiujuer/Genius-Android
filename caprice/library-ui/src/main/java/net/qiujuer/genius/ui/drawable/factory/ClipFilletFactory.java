/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/25/2015
 * Changed 07/25/2015
 * Version 1.0.0
 * Author Qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.genius.ui.drawable.factory;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;

/**
 * Created by qiujuer on 15/7/25.
 * This is a clip a Rounded Rectangle on Canvas clip
 */
public class ClipFilletFactory extends TouchEffectDrawable.ClipFactory {
    private Path mPath;
    private float[] mRadii;

    /**
     * Create a ClipFilletFactory class by
     * a Radius the value use to four corners of the rectangle
     *
     * @param radius Rounded radius
     */
    public ClipFilletFactory(float radius) {
        setRadius(radius);
    }

    /**
     * Create a ClipFilletFactory class by
     * radii values to four corners of the rectangle
     *
     * @param radii Rounded radius, the value is need 8 length
     */
    public ClipFilletFactory(float[] radii) {
        setRadii(radii);
    }

    public void setRadius(float radius) {
        this.mRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
    }

    public void setRadii(float[] radii) {
        if (radii == null || radii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii must have >= 8 values");
        }
        this.mRadii = radii;
    }


    /**
     * {@link net.qiujuer.genius.ui.drawable.TouchEffectDrawable.ClipFactory}
     */
    @Override
    public void resize(int width, int height) {
        if (mRadii == null) {
            mPath = null;
        } else {

            if (mPath == null)
                mPath = new Path();
            else
                mPath.reset();


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                mPath.addRoundRect(new RectF(0, 0, width, height), mRadii, Path.Direction.CW);
            else
                mPath.addRoundRect(0, 0, width, height, mRadii, Path.Direction.CW);
        }
    }

    /**
     * {@link net.qiujuer.genius.ui.drawable.TouchEffectDrawable.ClipFactory}
     */
    @Override
    public boolean clip(Canvas canvas) {
        return canvas.clipPath(mPath);
    }

}
