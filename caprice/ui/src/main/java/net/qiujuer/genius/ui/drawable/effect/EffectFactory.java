/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
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

/**
 * This is TouchEffectDrawable draw {@link Effect }'s EffectFactory
 */
public final class EffectFactory {
    public static final int TOUCH_EFFECT_NONE = 0;
    public static final int TOUCH_EFFECT_AUTO = 1;
    public static final int TOUCH_EFFECT_EASE = 2;
    public static final int TOUCH_EFFECT_PRESS = 3;
    public static final int TOUCH_EFFECT_RIPPLE = 4;

    public static Effect creator(int which) {
        if (which == TOUCH_EFFECT_AUTO)
            return (new AutoEffect());
        else if (which == TOUCH_EFFECT_EASE)
            return (new EaseEffect());
        else if (which == TOUCH_EFFECT_PRESS)
            return (new PressEffect());
        else if (which == TOUCH_EFFECT_RIPPLE)
            return (new RippleEffect());
        else
            return null;
    }
}
