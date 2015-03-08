/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/17/2014
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
package net.qiujuer.genius.kit.command;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import net.qiujuer.genius.kit.util.Tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by QiuJu
 * on 2014/9/17.
 */
public class CommandService extends Service {
    private CommandServiceImpl mImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        mImpl = new CommandServiceImpl();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mImpl == null)
            mImpl = new CommandServiceImpl();
        return mImpl;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        stopSelf();
        return false;
    }

    @Override
    public void onDestroy() {
        if (mImpl != null) {
            mImpl.destroy();
            mImpl = null;
        }
        super.onDestroy();
        // Kill process
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private class CommandServiceImpl extends ICommandInterface.Stub {
        private Map<String, CommandExecutor> mCommandExecutorMap = new HashMap<String, CommandExecutor>();
        private Lock mMapLock = new ReentrantLock();
        private Thread mTimeoutThread;

        public CommandServiceImpl() {
            // Init
            mTimeoutThread = new Thread(CommandServiceImpl.class.getName()) {
                @Override
                public void run() {
                    // When thread is not destroy
                    while (mTimeoutThread == this && !this.isInterrupted()) {
                        if (mCommandExecutorMap != null && mCommandExecutorMap.size() > 0) {
                            try {
                                mMapLock.lock();
                                Collection<CommandExecutor> commandExecutors = mCommandExecutorMap.values();
                                for (CommandExecutor executor : commandExecutors) {
                                    // Kill Service Process
                                    if (executor.isTimeOut())
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    if (mTimeoutThread != this && this.isInterrupted())
                                        break;
                                }
                            } finally {
                                mMapLock.unlock();
                            }
                        }
                        // Sleep 10 Second
                        Tools.sleepIgnoreInterrupt(10000);
                    }
                }
            };
            mTimeoutThread.setDaemon(true);
            mTimeoutThread.start();
        }

        /**
         * Destroy
         */
        protected void destroy() {
            if (mTimeoutThread != null) {
                mTimeoutThread.interrupt();
                mTimeoutThread = null;
            }
            try {
                mMapLock.lock();
                mCommandExecutorMap.clear();
                mCommandExecutorMap = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mMapLock.unlock();
            }
        }


        /**
         * Run Command
         *
         * @param params params
         * @return result
         * @throws android.os.RemoteException
         */
        @Override
        public String command(String id, int timeout, String params) throws RemoteException {
            CommandExecutor executor = mCommandExecutorMap.get(id);
            if (executor == null) {
                try {
                    mMapLock.lock();
                    executor = mCommandExecutorMap.get(id);
                    if (executor == null) {
                        executor = CommandExecutor.create(timeout, params);
                        mCommandExecutorMap.put(id, executor);
                    }
                } finally {
                    mMapLock.unlock();
                }
            }

            // Get Result
            String result = executor.getResult();

            try {
                mMapLock.lock();
                mCommandExecutorMap.remove(id);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mMapLock.unlock();
            }
            return result;
        }

        /**
         * Cancel command
         *
         * @param id command.id
         * @throws android.os.RemoteException
         */
        @Override
        public void cancel(String id) throws RemoteException {
            CommandExecutor executor = mCommandExecutorMap.get(id);
            if (executor != null) {
                try {
                    mMapLock.lock();
                    mCommandExecutorMap.remove(id);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mMapLock.unlock();
                }
                executor.destroy();
            }
        }

        /**
         * Get Task Count
         *
         * @return Map Count
         * @throws android.os.RemoteException
         */
        @Override
        public int getTaskCount() throws RemoteException {
            if (mCommandExecutorMap == null)
                return 0;
            return mCommandExecutorMap.size();
        }
    }
}
