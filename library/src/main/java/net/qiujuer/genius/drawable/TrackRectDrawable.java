/*
 * Copyright (c) Gustavo Claramunt (AnderWeb) 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.qiujuer.genius.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Simple {@link net.qiujuer.genius.drawable.TrackRectDrawable } implementation
 * to draw rectangles
 *
 * @hide
 */
public class TrackRectDrawable extends PaintStateDrawable {

    public TrackRectDrawable(@NonNull ColorStateList tintStateList) {
        super(tintStateList);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(getBounds(), paint);
    }

}
