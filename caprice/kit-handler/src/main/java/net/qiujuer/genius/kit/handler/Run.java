/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 11/24/2014
 * Changed 04/19/2016
 * Version 2.0.0
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
package net.qiujuer.genius.kit.handler;

import android.os.Looper;

import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.kit.handler.runable.Func;

/**
 * This is UI operation class
 * You can run thread on MainThread By Async and Sync
 * <p/>
 * You don't need initialize, but when you don't need run
 * You should call {@link #dispose()} operation for destruction.
 */
final public class Run {
    private static RunHandler mainPoster = null;

    private static RunHandler getMainPoster() {
        if (mainPoster == null) {
            synchronized (Run.class) {
                if (mainPoster == null) {
                    // This time is 1000/60 (60fps)
                    mainPoster = new RunHandler(Looper.getMainLooper(), 16);
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
     * @param action Action Interface
     */
    public static void onUiAsync(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        getMainPoster().async(new ActionAsyncRunnable(action));
    }

    /**
     * Synchronously
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     *
     * @param action Action Interface
     */
    public static void onUiSync(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncRunnable poster = new ActionSyncRunnable(action);
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
     * @param action     Action Interface
     * @param waitMillis wait for the main thread run millis Time
     * @param cancel     on the child thread cancel the runnable task
     */
    public static void onUiSync(Action action, int waitMillis, boolean cancel) {
        onUiSync(action, waitMillis, 0, cancel);
    }

    /**
     * Synchronously
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     * But the child thread just wait for the waitTime long.
     *
     * @param action     Action Interface
     * @param waitMillis wait for the main thread run millis Time
     * @param waitNanos  wait for the main thread run nanos Time
     * @param cancel     on the child thread cancel the runnable task
     */
    public static void onUiSync(Action action, int waitMillis, int waitNanos, boolean cancel) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncRunnable poster = new ActionSyncRunnable(action);
        getMainPoster().sync(poster);
        poster.waitRun(waitMillis, waitNanos, cancel);
    }


    /**
     * Synchronously
     * <p/>
     * In this you can receiver {@link Func#call()} return
     * <p/>
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     *
     * @param func Func Interface
     */
    public static <T> T onUiSync(Func<T> func) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call();
        }

        FuncSyncRunnable<T> poster = new FuncSyncRunnable<T>(func);
        getMainPoster().sync(poster);
        return poster.waitRun();
    }

    /**
     * Synchronously
     * <p/>
     * In this you can receiver {@link Func#call()} return
     * <p/>
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     * But the child thread just wait for the waitTime long.
     *
     * @param func       Func Interface
     * @param waitMillis wait for the main thread run millis Time
     * @param cancel     on the child thread cancel the runnable task
     */
    public static <T> T onUiSync(Func<T> func, int waitMillis, boolean cancel) {
        return onUiSync(func, waitMillis, 0, cancel);
    }


    /**
     * Synchronously
     * <p/>
     * In this you can receiver {@link Func#call()} return
     * <p/>
     * The child thread relative thread synchronization operation,
     * blocking the child thread,
     * thread for the main thread to complete
     * But the child thread just wait for the waitTime long.
     *
     * @param func       Func Interface
     * @param waitMillis wait for the main thread run millis Time
     * @param waitNanos  wait for the main thread run nanos Time
     * @param cancel     on the child thread cancel the runnable task
     */
    public static <T> T onUiSync(Func<T> func, int waitMillis, int waitNanos, boolean cancel) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call();
        }

        FuncSyncRunnable<T> poster = new FuncSyncRunnable<T>(func);
        getMainPoster().sync(poster);
        return poster.waitRun(waitMillis, waitNanos, cancel);
    }


    /**
     * Call this on you need dispose
     */
    public static void dispose() {
        if (mainPoster != null) {
            mainPoster.dispose();
            mainPoster = null;
        }
    }
}
