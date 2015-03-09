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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by QiuJu
 * on 2014/11/24.
 */
final class UiKitHandlerPoster extends Handler {
    private static final int ASYNC = 0x1;
    private static final int SYNC = 0x2;
    private final Queue<Runnable> mAsyncPool;
    private final Queue<UiKitSyncPost> mSyncPool;
    private final int mMaxMillisInsideHandleMessage;
    private boolean isAsyncActive;
    private boolean isSyncActive;

    UiKitHandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.mMaxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        mAsyncPool = new LinkedList<>();
        mSyncPool = new LinkedList<>();
    }

    void dispose() {
        this.removeCallbacksAndMessages(null);
        this.mAsyncPool.clear();
        this.mSyncPool.clear();
    }

    void async(Runnable runnable) {
        synchronized (mAsyncPool) {
            mAsyncPool.offer(runnable);
            if (!isAsyncActive) {
                isAsyncActive = true;
                if (!sendMessage(obtainMessage(ASYNC))) {
                    throw new GeniusException("Could not send handler message");
                }
            }
        }
    }

    void sync(UiKitSyncPost post) {
        synchronized (mSyncPool) {
            mSyncPool.offer(post);
            if (!isSyncActive) {
                isSyncActive = true;
                if (!sendMessage(obtainMessage(SYNC))) {
                    throw new GeniusException("Could not send handler message");
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == ASYNC) {
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    Runnable runnable = mAsyncPool.poll();
                    if (runnable == null) {
                        synchronized (mAsyncPool) {
                            // Check again, this time in synchronized
                            runnable = mAsyncPool.poll();
                            if (runnable == null) {
                                isAsyncActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= mMaxMillisInsideHandleMessage) {
                        if (!sendMessage(obtainMessage(ASYNC))) {
                            throw new GeniusException("Could not send handler message");
                        }
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
                    UiKitSyncPost post = mSyncPool.poll();
                    if (post == null) {
                        synchronized (mSyncPool) {
                            // Check again, this time in synchronized
                            post = mSyncPool.poll();
                            if (post == null) {
                                isSyncActive = false;
                                return;
                            }
                        }
                    }
                    post.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= mMaxMillisInsideHandleMessage) {
                        if (!sendMessage(obtainMessage(SYNC))) {
                            throw new GeniusException("Could not send handler message");
                        }
                        rescheduled = true;
                        return;
                    }
                }
            } finally {
                isSyncActive = rescheduled;
            }
        } else super.handleMessage(msg);
    }
}