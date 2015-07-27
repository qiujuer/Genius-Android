/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/26/2015
 * Changed 07/27/2015
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
package net.qiujuer.genius.ui.drawable.effect;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Ease Draw Effect
 */
public class EaseEffect extends Effect {
    static final int ALPHA_EASE_MAX_DEFAULT = 256;
    protected int mAlpha = 0;
    protected int mMaxAlpha;

    public EaseEffect() {
        mMaxAlpha = ALPHA_EASE_MAX_DEFAULT;
    }

    public EaseEffect(int maxEaseAlpha) {
        mMaxAlpha = maxEaseAlpha;
    }

    public void setMaxEaseAlpha(int alpha) {
        this.mMaxAlpha = alpha;
    }

    public int getMaxEaseAlpha() {
        return mMaxAlpha;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mAlpha > 0) {
            setPaintAlpha(paint, mAlpha);
            canvas.drawColor(paint.getColor());
        }
    }

    @Override
    public void animationEnter(float factor) {
        mAlpha = (int) (factor * mMaxAlpha);
    }

    @Override
    public void animationExit(float factor) {
        mAlpha = mMaxAlpha - (int) (mMaxAlpha * factor);
    }
}
