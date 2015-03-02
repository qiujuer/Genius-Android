/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/02/2015
 * Changed 03/02/2015
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

import android.content.res.ColorStateList;
import android.content.res.Resources;


public class SeekBarAttributes extends Attributes {
    private ColorStateList trackColor;
    private ColorStateList thumbColor;
    private ColorStateList scrubberColor;
    private ColorStateList rippleColor;
    private ColorStateList indicatorColor;

    public SeekBarAttributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        super(attributeChangeListener, resources);
    }

    public void setThumbColor(ColorStateList thumbColor) {
        this.thumbColor = thumbColor;
    }

    public void setTrackColor(ColorStateList trackColor) {
        this.trackColor = trackColor;
    }

    public void setScrubberColor(ColorStateList scrubberColor) {
        this.scrubberColor = scrubberColor;
    }

    public void setRippleColor(ColorStateList rippleColor) {
        this.rippleColor = rippleColor;
    }

    public void setIndicatorColor(ColorStateList indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public ColorStateList getIndicatorColor() {
        return indicatorColor;
    }

    public ColorStateList getRippleColor() {
        return rippleColor;
    }

    public ColorStateList getScrubberColor() {
        return scrubberColor;
    }

    public ColorStateList getThumbColor() {
        return thumbColor;
    }

    public ColorStateList getTrackColor() {
        return trackColor;
    }
}
