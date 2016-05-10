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

import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.kit.handler.runable.Func;

/**
 * ActionSyncRunnable use to {@link Action} and {@link Runnable}
 * <p/>
 * See {@link Run}
 */
final class ActionSyncRunnable implements Action, Runnable {
    private final Action mAction;
    private boolean mDone = false;


    ActionSyncRunnable(Action action) {
        this.mAction = action;
    }

    /**
     * In this we call cal the {@link Func}
     * and check should run it
     */
    @Override
    public void call() {
        if (!mDone) {
            synchronized (this) {
                if (!mDone) {
                    mAction.call();
                    mDone = true;
                    try {
                        this.notifyAll();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
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
     */
    public void waitRun() {
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
    }

    /**
     * Wait for a period of time to run end
     *
     * @param waitMillis wait milliseconds time
     * @param waitNanos  wait nanoseconds time
     * @param cancel     when wait end cancel the run
     */
    public void waitRun(long waitMillis, int waitNanos, boolean cancel) {
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
    }
}
