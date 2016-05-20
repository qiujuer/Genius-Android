/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
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

import android.os.Handler;
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
    private static HandlerPoster uiPoster = null;
    private static HandlerPoster threadPoster = null;

    public static Handler getUiHandler() {
        return getUiPoster();
    }

    private static HandlerPoster getUiPoster() {
        if (uiPoster == null) {
            synchronized (Run.class) {
                if (uiPoster == null) {
                    // This time is 1000/60 (60fps)
                    uiPoster = new HandlerPoster(Looper.getMainLooper(), 16);
                }
            }
        }
        return uiPoster;
    }

    public static Handler getThreadHandler() {
        return getThreadPoster();
    }

    private static HandlerPoster getThreadPoster() {
        if (threadPoster == null) {
            synchronized (Run.class) {
                if (threadPoster == null) {
                    // This time is 1000/60 (60fps)
                    Thread thread = new Thread("ThreadRunHandler") {
                        @Override
                        public void run() {
                            Looper.prepare();
                            synchronized (Run.class) {
                                threadPoster = new HandlerPoster(Looper.myLooper(), 16);
                                // notify run next
                                try {
                                    Run.class.notifyAll();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Looper.loop();
                        }
                    };

                    threadPoster.getLooper().quit();
                    thread.setDaemon(true);
                    //thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();

                    // in this we can wait set the threadPoster
                    try {
                        Run.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return threadPoster;
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
        getUiPoster().async(new ActionAsyncRunnable(action));
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
        getUiPoster().sync(poster);
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
     * @param waitMillis wait for the main thread run milliseconds Time
     * @param cancel     on the child thread cancel the runnable task
     */
    public static void onUiSync(Action action, long waitMillis, boolean cancel) {
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
     * @param waitMillis wait for the main thread run milliseconds Time
     * @param waitNanos  wait for the main thread run nanoseconds Time
     * @param cancel     on the child thread cancel the runnable task
     */
    public static void onUiSync(Action action, long waitMillis, int waitNanos, boolean cancel) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncRunnable poster = new ActionSyncRunnable(action);
        getUiPoster().sync(poster);
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
     * @param <T>  you can set any type to return
     * @return {@link T}
     */
    public static <T> T onUiSync(Func<T> func) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call();
        }

        FuncSyncRunnable<T> poster = new FuncSyncRunnable<T>(func);
        getUiPoster().sync(poster);
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
     * @param waitMillis wait for the main thread run milliseconds Time
     * @param cancel     on the child thread cancel the runnable task
     * @param <T>        you can set any type to return
     * @return {@link T}
     */
    public static <T> T onUiSync(Func<T> func, long waitMillis, boolean cancel) {
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
     * @param waitMillis wait for the main thread run milliseconds Time
     * @param waitNanos  wait for the main thread run nanoseconds Time
     * @param cancel     on the child thread cancel the runnable task
     * @param <T>        you can set any type to return
     * @return {@link T}
     */
    public static <T> T onUiSync(Func<T> func, long waitMillis, int waitNanos, boolean cancel) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call();
        }

        FuncSyncRunnable<T> poster = new FuncSyncRunnable<T>(func);
        getUiPoster().sync(poster);
        return poster.waitRun(waitMillis, waitNanos, cancel);
    }


    /**
     * Call this on you need dispose
     */
    public static void dispose() {
        if (uiPoster != null) {
            uiPoster.dispose();
            uiPoster = null;
        }
    }
}
