/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/23/2015
 * Changed 08/08/2015
 * Version 3.0.0
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

package net.qiujuer.genius.ui.compat;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import net.qiujuer.genius.ui.drawable.BalloonMarkerDrawable;

/**
 * Wrapper compatibility class to call some API-Specific methods
 * And offer alternate procedures when possible
 *
 * @hide
 */
@TargetApi(21)
class UiCompatNotCrash {
    public static void setOutlineProvider(View marker, final BalloonMarkerDrawable balloonMarkerDrawable) {
        marker.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setConvexPath(balloonMarkerDrawable.getPath());
            }
        });
    }

    public static void setBackground(View view, Drawable background) {
        view.setBackground(background);
    }

    public static void setTextDirection(TextView number, int textDirection) {
        number.setTextDirection(textDirection);
    }
}
