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
@SuppressWarnings("WeakerAccess")
final public class Run {
    private static HandlerPoster uiPoster = null;
    private static HandlerPoster backgroundPoster = null;

    /**
     * Get the Ui thread Handler
     *
     * @return Handler
     */
    public static Handler getUiHandler() {
        return getUiPoster();
    }

    private static HandlerPoster getUiPoster() {
        if (uiPoster == null) {
            synchronized (Run.class) {
                if (uiPoster == null) {
                    // This time is 1000/60 (60fps)
                    // And the async dif to sync method
                    uiPoster = new HandlerPoster(Looper.getMainLooper(), 16, false);
                }
            }
        }
        return uiPoster;
    }

    /**
     * Get the Background thread Handler
     *
     * @return Handler
     */
    public static Handler getBackgroundHandler() {
        return getBackgroundPoster();
    }

    private static HandlerPoster getBackgroundPoster() {
        if (backgroundPoster == null) {
            synchronized (Run.class) {
                if (backgroundPoster == null) {
                    Thread thread = new Thread("ThreadRunHandler") {
                        @Override
                        public void run() {
                            Looper.prepare();
                            synchronized (Run.class) {
                                // This time is 1000*3 (3 milliseconds)
                                // And the async dif to sync method
                                backgroundPoster = new HandlerPoster(Looper.myLooper(), 3 * 1000, true);
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

                    thread.setDaemon(true);
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();

                    // in this we can wait set the backgroundPoster
                    try {
                        Run.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return backgroundPoster;
    }

    /**
     * Asynchronously
     * The current thread asynchronous run relative to the sub thread,
     * not blocking the current thread
     *
     * @param action Action Interface, you can do something in this
     */
    public static Result onBackground(Action action) {
        final HandlerPoster poster = getBackgroundPoster();
        if (Looper.myLooper() == poster.getLooper()) {
            action.call();
            return new ActionAsyncTask(action, true);
        }
        ActionAsyncTask task = new ActionAsyncTask(action);
        poster.async(task);
        return task;
    }


    /**
     * Asynchronously
     * The current thread asynchronous run relative to the main thread,
     * not blocking the current thread
     *
     * @param action Action Interface
     */
    public static Result onUiAsync(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return new ActionAsyncTask(action, true);
        }
        ActionAsyncTask task = new ActionAsyncTask(action);
        getUiPoster().async(task);
        return task;
    }

    /**
     * Synchronously
     * The current thread relative thread synchronization operation,
     * blocking the current thread,
     * thread for the main thread to complete
     *
     * @param action Action Interface
     */
    public static void onUiSync(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncTask poster = new ActionSyncTask(action);
        getUiPoster().sync(poster);
        poster.waitRun();
    }

    /**
     * Synchronously
     * The current thread relative thread synchronization operation,
     * blocking the current thread,
     * thread for the main thread to complete
     * But the current thread just wait for the waitTime long.
     *
     * @param action          Action Interface
     * @param waitMillis      wait for the main thread run milliseconds Time
     * @param cancelOnTimeOut on timeout the current thread cancel the runnable task
     */
    public static void onUiSync(Action action, long waitMillis, boolean cancelOnTimeOut) {
        onUiSync(action, waitMillis, 0, cancelOnTimeOut);
    }

    /**
     * Synchronously
     * The current thread relative thread synchronization operation,
     * blocking the current thread,
     * thread for the main thread to complete
     * But the current thread just wait for the waitTime long.
     *
     * @param action          Action Interface
     * @param waitMillis      wait for the main thread run milliseconds Time
     * @param waitNanos       wait for the main thread run nanoseconds Time
     * @param cancelOnTimeOut on timeout the current thread cancel the runnable task
     */
    public static void onUiSync(Action action, long waitMillis, int waitNanos, boolean cancelOnTimeOut) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.call();
            return;
        }
        ActionSyncTask poster = new ActionSyncTask(action);
        getUiPoster().sync(poster);
        poster.waitRun(waitMillis, waitNanos, cancelOnTimeOut);
    }


    /**
     * Synchronously
     * <p/>
     * In this you can receiver {@link Func#call()} return
     * <p/>
     * The current thread relative thread synchronization operation,
     * blocking the current thread,
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

        FuncSyncTask<T> poster = new FuncSyncTask<T>(func);
        getUiPoster().sync(poster);
        return poster.waitRun();
    }

    /**
     * Synchronously
     * <p/>
     * In this you can receiver {@link Func#call()} return
     * <p/>
     * The current thread relative thread synchronization operation,
     * blocking the current thread,
     * thread for the main thread to complete
     * But the current thread just wait for the waitTime long.
     *
     * @param func            Func Interface
     * @param waitMillis      wait for the main thread run milliseconds Time
     * @param cancelOnTimeOut on timeout the current thread cancel the runnable task
     * @param <T>             you can set any type to return
     * @return {@link T}
     */
    public static <T> T onUiSync(Func<T> func, long waitMillis, boolean cancelOnTimeOut) {
        return onUiSync(func, waitMillis, 0, cancelOnTimeOut);
    }


    /**
     * Synchronously
     * <p/>
     * In this you can receiver {@link Func#call()} return
     * <p/>
     * The current thread relative thread synchronization operation,
     * blocking the current thread,
     * thread for the main thread to complete
     * But the current thread just wait for the waitTime long.
     *
     * @param func            Func Interface
     * @param waitMillis      wait for the main thread run milliseconds Time
     * @param waitNanos       wait for the main thread run nanoseconds Time
     * @param cancelOnTimeOut on timeout the current thread cancel the runnable task
     * @param <T>             you can set any type to return
     * @return {@link T}
     */
    public static <T> T onUiSync(Func<T> func, long waitMillis, int waitNanos, boolean cancelOnTimeOut) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call();
        }

        FuncSyncTask<T> poster = new FuncSyncTask<T>(func);
        getUiPoster().sync(poster);
        return poster.waitRun(waitMillis, waitNanos, cancelOnTimeOut);
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
