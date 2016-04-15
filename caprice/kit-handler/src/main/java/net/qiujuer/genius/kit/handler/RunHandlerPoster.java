/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 11/24/2014
 * Changed 04/15/2016
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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Run Handler Poster extends Handler
 * <p/>
 * In class have two queue with {@link #mAsyncPool,#mSyncPool}
 */
final class RunHandlerPoster extends Handler {
    private static final int ASYNC = 0x1;
    private static final int SYNC = 0x2;
    private final Queue<Runnable> mAsyncPool;
    private final Queue<Runnable> mSyncPool;
    private final int mMaxMillisInsideHandleMessage;
    private boolean isAsyncActive;
    private boolean isSyncActive;

    /**
     * Init this
     *
     * @param looper                       Handler Looper
     * @param maxMillisInsideHandleMessage The maximum time occupied the main thread each cycle
     */
    RunHandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.mMaxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        mAsyncPool = new LinkedList<>();
        mSyncPool = new LinkedList<>();
    }

    /**
     * Pool clear
     */
    void dispose() {
        this.removeCallbacksAndMessages(null);
        this.mAsyncPool.clear();
        this.mSyncPool.clear();
    }

    /**
     * Add a async post to Handler pool
     *
     * @param runnable Runnable
     */
    void async(Runnable runnable) {
        synchronized (mAsyncPool) {
            mAsyncPool.offer(runnable);
            if (!isAsyncActive) {
                isAsyncActive = true;
                sendMessage(ASYNC);
            }
        }
    }

    /**
     * Add a async post to Handler pool
     *
     * @param runnable Runnable
     */
    void sync(Runnable runnable) {
        synchronized (mSyncPool) {
            mSyncPool.offer(runnable);
            if (!isSyncActive) {
                isSyncActive = true;
                sendMessage(SYNC);
            }
        }
    }

    /**
     * Run in main thread
     *
     * @param msg call messages
     */
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == ASYNC) {
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    Runnable runnable = poolAsyncPost();
                    if (runnable == null) {
                        synchronized (mAsyncPool) {
                            // Check again, this time in synchronized
                            runnable = poolAsyncPost();
                            if (runnable == null) {
                                isAsyncActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= mMaxMillisInsideHandleMessage) {
                        sendMessage(ASYNC);
                        rescheduled = true;
                        return;
                    }
                }
            } finally {
                isAsyncActive = rescheduled;
            }
        } else if (msg.what == SYNC) {
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    Runnable runnable = poolSyncPost();
                    if (runnable == null) {
                        synchronized (mSyncPool) {
                            // Check again, this time in synchronized
                            runnable = poolSyncPost();
                            if (runnable == null) {
                                isSyncActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= mMaxMillisInsideHandleMessage) {
                        sendMessage(SYNC);
                        rescheduled = true;
                        return;
                    }
                }
            } finally {
                isSyncActive = rescheduled;
            }
        } else super.handleMessage(msg);
    }

    /**
     * pool a Runnable form AsyncPool
     *
     * @return Runnable
     */
    private Runnable poolAsyncPost() {
        synchronized (mAsyncPool) {
            try {
                return mAsyncPool.poll();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * pool a Runnable form SyncPool
     *
     * @return Runnable
     */
    private Runnable poolSyncPost() {
        synchronized (mSyncPool) {
            try {
                return mSyncPool.poll();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Send a message to this Handler
     *
     * @param what This what is SYNC or ASYNC
     */
    private void sendMessage(int what) {
        if (!sendMessage(obtainMessage(what))) {
            throw new RuntimeException("Could not send handler message");
        }
    }
}