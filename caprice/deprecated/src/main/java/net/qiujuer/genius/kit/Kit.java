/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/17/2014
 * Changed 2015/11/21
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
import net.qiujuer.genius.kit.util.Log;
import net.qiujuer.genius.kit.util.UiKit;

/**
 * This is Genius-Android Kit class
 * If use Command/Log
 * You should call {@link #initialize(Application)}
 * The End should call {@link #dispose()}  }
 */
public final class Kit {
    private static Application application;

    /**
     * Get this Application
     *
     * @return Application
     */
    public static Application getApplication() {
        return application;
    }

    /**
     * Init this Application
     *
     * @param application Application
     */
    public static void initialize(Application application) {
        Kit.application = application;
    }

    /**
     * When you app exit you should call this
     */
    public static void dispose() {
        Command.dispose();
        Log.dispose();
        UiKit.dispose();
        Kit.application = null;
    }
}
