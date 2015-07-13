/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/10/2015
 * Changed 02/16/2015
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
package net.qiujuer.genius.ui.widget.attribute;


import android.content.res.Resources;

public class ButtonAttributes extends BaseAttributes {
    private int mBottom = 0;
    private boolean mDelayClick = true;

    public ButtonAttributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        super(attributeChangeListener, resources);
    }

    public int getBottom() {
        return mBottom;
    }

    public void setBottom(int bottom) {
        this.mBottom = bottom;

    }

    public boolean isDelayClick() {
        return mDelayClick;
    }

    public void setDelayClick(boolean delayClick) {
        this.mDelayClick = delayClick;
    }
}
