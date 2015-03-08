/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/17/2014
 * Changed 03/08/2015
 * Version 3.0.0
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
package net.qiujuer.genius.kit;

import android.app.Application;

import net.qiujuer.genius.kit.command.Command;
import net.qiujuer.genius.kit.util.UIKit;
import net.qiujuer.genius.kit.util.Log;

/**
 * Created by QiuJu
 * on 2014/9/17.
 */
public final class GeniusKit {
    private static Application application;

    public static Application getApplication() {
        return application;
    }

    public static void initialize(Application application) {
        GeniusKit.application = application;
    }

    public static void dispose() {
        Command.dispose();
        Log.dispose();
        UIKit.dispose();
        GeniusKit.application = null;
    }
}
