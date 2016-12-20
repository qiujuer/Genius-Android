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

import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.Queue;

/**
 * ActionAsyncTask use to {@link Action} and {@link Runnable}
 * <p/>
 * See {@link Run}
 */
class ActionAsyncTask implements Action, Task {
    private final Action mAction;
    private boolean mDone = false;
    private Queue<Task> mPool = null;

    ActionAsyncTask(Action action) {
        mAction = action;
    }

    ActionAsyncTask(Action action, boolean isDone) {
        mAction = action;
        mDone = isDone;
    }

    @Override
    public void run() {
        if (!mDone) {
            synchronized (this) {
                if (!mDone) {
                    call();
                    mDone = true;
                }
            }
        }
    }

    @Override
    public void call() {
        // Cleanup reference the pool
        mPool = null;
        // Doing
        mAction.call();
    }

    @Override
    public boolean isDone() {
        return mDone;
    }


    @Override
    public void setPool(Queue<Task> pool) {
        mPool = pool;
    }

    @Override
    public void cancel() {
        if (!mDone) {
            synchronized (this) {
                mDone = true;
                // clear the task form pool
                if (mPool != null) {
                    //noinspection SynchronizeOnNonFinalField
                    synchronized (mPool) {
                        if (mPool != null) {
                            try {
                                mPool.remove(this);
                            } catch (Exception e) {
                                e.getStackTrace();
                            } finally {
                                mPool = null;
                            }
                        }
                    }
                }
            }
        }
    }
}
