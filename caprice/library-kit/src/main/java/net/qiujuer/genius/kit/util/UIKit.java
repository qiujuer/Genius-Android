/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 11/24/2014
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
package net.qiujuer.genius.kit.util;

import android.os.Looper;

/**
 * This is UI operation class
 * You can run thread on MainThread By Async and Sync
 * <p/>
 * You don't need initialize, but when you don't need run
 * You should call {@link #dispose()} operation for destruction.
 */
final public class UiKit {
    private static UiKitHandlerPoster mainPoster = null;

    private static UiKitHandlerPoster getMainPoster() {
        if (mainPoster == null) {
            synchronized (UiKit.class) {
                if (mainPoster == null) {
                    mainPoster = new UiKitHandlerPoster(Looper.getMainLooper(), 20);
                }
            }
        }
        return mainPoster;
    }

    /**
     * Asynchronously
     * The child thread asynchronous run relative to the main thread,
     * not blocking the child thread
     *
     * @param runnable Runnable Interface
     */
    public static void runOnMainThreadAsync(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        getMainPoster().async(runnable);
    }

    /**
     * Synchronously
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     *
     * @param runnable Runnable Interface
     */
    public static void runOnMainThreadSync(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        UiKitSyncPost poster = new UiKitSyncPost(runnable);
        getMainPoster().sync(poster);
        poster.waitRun();
    }

    /**
     * Synchronously
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     * But the child thread just wait for the waitTime long.
     *
     * @param runnable Runnable Interface
     * @param waitTime wait for the main thread run Time
     * @param cancel   on the child thread cancel the runnable task
     */
    public static void runOnMainThreadSync(Runnable runnable, int waitTime, boolean cancel) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        UiKitSyncPost poster = new UiKitSyncPost(runnable);
        getMainPoster().sync(poster);
        poster.waitRun(waitTime, cancel);
    }

    public static void dispose() {
        if (mainPoster != null) {
            mainPoster.dispose();
            mainPoster = null;
        }
    }
}
