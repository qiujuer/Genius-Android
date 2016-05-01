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

import net.qiujuer.genius.kit.handler.runable.Func;

/**
 * FuncSyncRunnable use to {@link Func} and {@link Runnable}
 * <p/>
 * See {@link Run} call this
 */
@SuppressWarnings("unused")
final class FuncSyncRunnable<T> implements Func<T>, Runnable {
    private Func<T> mFunc;
    private T mResult;
    private boolean isEnd = false;


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
        if (!isEnd) {
            synchronized (this) {
                if (!isEnd) {
                    mResult = mFunc.call();
                    isEnd = true;
                    try {
                        this.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
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
        if (!isEnd) {
            synchronized (this) {
                if (!isEnd) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mResult;
    }

    /**
     * Wait for a period of time to run end
     *
     * @param waitMillis wait millis time
     * @param waitNanos  wait millis time
     * @param cancel     when wait end cancel the run
     * @return T
     */
    public T waitRun(int waitMillis, int waitNanos, boolean cancel) {
        if (!isEnd) {
            synchronized (this) {
                if (!isEnd) {
                    try {
                        this.wait(waitMillis, waitNanos);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (!isEnd && cancel)
                            isEnd = true;
                    }
                }
            }
        }
        return mResult;
    }
}
