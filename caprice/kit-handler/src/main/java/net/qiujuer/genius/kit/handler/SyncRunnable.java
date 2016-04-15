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

/**
 * SyncRunnable use to {@link Runnable}
 * <p/>
 * See {@link Run}
 */
final class SyncRunnable implements Runnable {
    private Runnable mRunnable;
    private boolean isEnd = false;


    SyncRunnable(Runnable runnable) {
        this.mRunnable = runnable;
    }

    /**
     * Run to doing something
     */
    @Override
    public void run() {
        if (!isEnd) {
            synchronized (this) {
                if (!isEnd) {
                    mRunnable.run();
                    isEnd = true;
                    try {
                        this.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Wait to run end
     */
    public void waitRun() {
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
    }

    /**
     * Wait for a period of time to run end
     *
     * @param time   wait time
     * @param cancel when wait end cancel the run
     */
    public void waitRun(int time, boolean cancel) {
        if (!isEnd) {
            synchronized (this) {
                if (!isEnd) {
                    try {
                        this.wait(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (!isEnd && cancel)
                            isEnd = true;
                    }
                }
            }
        }
    }
}
