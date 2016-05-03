/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 11/24/2014
 * Changed 04/21/2016
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
 * In class have two Runner with {@link #mAsyncRunner,#mSyncRunner}
 */
final class RunHandler extends Handler {
    private static final int ASYNC = 0x1;
    private static final int SYNC = 0x2;
    private static int MAX_MILLIS_INSIDE_HANDLE_MESSAGE = 16;
    private final Runner mAsyncRunner;
    private final Runner mSyncRunner;

    /**
     * Init this
     *
     * @param looper                       Handler Looper
     * @param maxMillisInsideHandleMessage The maximum time occupied the main thread each cycle
     */
    RunHandler(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        // inside time
        MAX_MILLIS_INSIDE_HANDLE_MESSAGE = maxMillisInsideHandleMessage;

        // async runner
        mAsyncRunner = new Runner(new LinkedList<Runnable>(),
                new Runner.IPoster() {
                    @Override
                    public void sendMessage() {
                        RunHandler.this.sendMessage(ASYNC);
                    }
                });

        // sync runner
        mSyncRunner = new Runner(new LinkedList<Runnable>(),
                new Runner.IPoster() {
                    @Override
                    public void sendMessage() {
                        RunHandler.this.sendMessage(SYNC);
                    }
                });

    }

    /**
     * Pool clear
     */
    void dispose() {
        this.removeCallbacksAndMessages(null);
        this.mAsyncRunner.dispose();
        this.mSyncRunner.dispose();
    }

    /**
     * Add a async post to Handler pool
     *
     * @param runnable Runnable
     */
    void async(Runnable runnable) {
        mAsyncRunner.offer(runnable);
    }

    /**
     * Add a async post to Handler pool
     *
     * @param runnable Runnable
     */
    void sync(Runnable runnable) {
        mSyncRunner.offer(runnable);
    }

    /**
     * Run in main thread
     *
     * @param msg call messages
     */
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == ASYNC) {
            mAsyncRunner.dispatch();
        } else if (msg.what == SYNC) {
            mSyncRunner.dispatch();
        } else super.handleMessage(msg);
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


    /**
     * This's main Runner
     */
    static class Runner {
        private final Queue<Runnable> mPool;
        private IPoster mPoster;
        private boolean isActive;

        public Runner(Queue<Runnable> pool, IPoster poster) {
            mPool = pool;
            mPoster = poster;
        }

        /**
         * offer to {@link #mPool}
         *
         * @param runnable Runnable
         */
        public void offer(Runnable runnable) {
            synchronized (mPool) {
                mPool.offer(runnable);
                if (!isActive) {
                    isActive = true;
                    // send again message
                    IPoster poster = mPoster;
                    if (poster != null)
                        poster.sendMessage();
                }
            }
        }

        /**
         * dispatch form {@link #mPool}
         */
        public void dispatch() {
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    Runnable runnable = poll();
                    if (runnable == null) {
                        synchronized (mPool) {
                            // Check again, this time in synchronized
                            runnable = poll();
                            if (runnable == null) {
                                isActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= MAX_MILLIS_INSIDE_HANDLE_MESSAGE) {
                        // send again message
                        IPoster poster = mPoster;
                        if (poster != null)
                            poster.sendMessage();

                        // rescheduled is true
                        rescheduled = true;
                        return;
                    }
                }
            } finally {
                isActive = rescheduled;
            }
        }

        /**
         * dispose the Runner on your no't need use
         */
        public void dispose() {
            mPool.clear();
            mPoster = null;
        }

        /**
         * poll a Runnable form {@link #mPool}
         *
         * @return Runnable
         */
        private Runnable poll() {
            synchronized (mPool) {
                try {
                    return mPool.poll();
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        /**
         * This's poster can to send refresh message
         */
        interface IPoster {
            void sendMessage();
        }
    }
}