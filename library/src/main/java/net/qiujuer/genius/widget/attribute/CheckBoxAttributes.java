/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/10/2015
 * Changed 02/10/2015
 * Version 2.0.0
 * GeniusEditText
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
package net.qiujuer.genius.widget.attribute;

import android.content.res.Resources;

import net.qiujuer.genius.R;


public class CheckBoxAttributes extends Attributes {
    private int mCircleRadius = Attributes.INVALID;
    private int mRingWidth;
    private boolean isCustomCircleRadius;

    public CheckBoxAttributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        super(attributeChangeListener, resources);

        mRingWidth = resources.getDimensionPixelSize(R.dimen.genius_checkBox_ringWidth);
    }

    public void setRingWidth(int ringWidth) {
        this.mRingWidth = ringWidth;
    }

    public void setCircleRadius(int circleRadius) {
        if (mCircleRadius != circleRadius) {
            if (circleRadius < 0)
                isCustomCircleRadius = false;
            else {
                isCustomCircleRadius = true;
                mCircleRadius = circleRadius;
            }
        }
    }

    public void setCustomCircleRadius(boolean isCustomCircleRadius) {
        this.isCustomCircleRadius = isCustomCircleRadius;
    }

    public int getRingWidth() {
        return mRingWidth;
    }

    public int getCircleRadius() {
        return mCircleRadius;
    }

    public boolean isCustomCircleRadius() {
        return isCustomCircleRadius;
    }
}
