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

import net.qiujuer.genius.kit.handler.runable.Func;

/**
 * FuncSyncRunnable use to {@link Func} and {@link Runnable}
 * <p/>
 * See {@link Run} call this
 */
@SuppressWarnings("unused")
final class FuncSyncRunnable<T> implements Func<T>, Runnable {
    private final Func<T> mFunc;
    private T mResult;
    private boolean mDone = false;


    FuncSyncRunnable(Func<T> func) {
        this.mFunc = func;
    }

    /**
     * In this we call cal the {@link Func}
     * and check should run it
     *
     * @return T
     */
    @Override
    public T call() {
        if (!mDone) {
            synchronized (this) {
                if (!mDone) {
                    mResult = mFunc.call();
                    mDone = true;
                    try {
                        this.notifyAll();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return mResult;
    }

    /**
     * Run to doing something
     */
    @Override
    public void run() {
        call();
    }

    /**
     * Wait to run end
     *
     * @return T
     */
    public T waitRun() {
        if (!mDone) {
            synchronized (this) {
                while (!mDone) {
                    try {
                        this.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
        return mResult;
    }

    /**
     * Wait for a period of time to run end
     *
     * @param waitMillis wait milliseconds time
     * @param waitNanos  wait nanoseconds time
     * @param cancel     True if when wait end cancel the run
     * @return T
     */
    public T waitRun(long waitMillis, int waitNanos, boolean cancel) {
        if (!mDone) {
            synchronized (this) {
                if (!mDone) {
                    try {
                        this.wait(waitMillis, waitNanos);
                    } catch (InterruptedException ignored) {
                    } finally {
                        if (!mDone && cancel)
                            mDone = true;
                    }
                }
            }
        }
        return mResult;
    }
}
