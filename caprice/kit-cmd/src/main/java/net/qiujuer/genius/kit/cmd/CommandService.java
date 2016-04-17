/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/25/2014
 * Changed 04/17/2016
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
package net.qiujuer.genius.kit.cmd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Command Service
 * Command run in process of independent
 */
public class CommandService extends Service {
    private CommandServiceImpl mImpl;

    /**
     * On create we new {@link CommandServiceImpl}
     */
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

    /**
     * Kill process when destroy
     */
    @Override
    public void onDestroy() {
        CommandServiceImpl impl = mImpl;
        if (impl != null) {
            mImpl = null;
            impl.destroy();
        }
        super.onDestroy();
        // Kill process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * CommandServiceImpl extends {@link ICommandInterface.Stub} with a aidl Interface
     */
    private class CommandServiceImpl extends ICommandInterface.Stub {
        private final Map<String, CommandExecutor> mCommandExecutorMap = new HashMap<String, CommandExecutor>();
        private Thread mTimeoutThread;

        public CommandServiceImpl() {
            // Cmd
            Thread thread = new Thread(CommandServiceImpl.class.getName()) {
                @Override
                public void run() {
                    // When thread is not destroy
                    while (mTimeoutThread == this && !this.isInterrupted()) {
                        if (mCommandExecutorMap.size() > 0) {
                            synchronized (mCommandExecutorMap) {
                                Collection<CommandExecutor> commandExecutors = mCommandExecutorMap.values();
                                for (CommandExecutor executor : commandExecutors) {
                                    // Kill Service Process
                                    if (executor.isTimeOut())
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    if (mTimeoutThread != this && this.isInterrupted())
                                        break;
                                }
                            }
                        }
                        // Sleep 10 Second
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.setDaemon(true);
            mTimeoutThread = thread;
            thread.start();
        }

        /**
         * Destroy
         */
        void destroy() {
            // dispose thread
            Thread thread = mTimeoutThread;
            if (thread != null) {
                mTimeoutThread = null;
                thread.interrupt();
            }
            // clear the map
            synchronized (mCommandExecutorMap) {
                mCommandExecutorMap.clear();
            }

            try {
                CommandExecutor.EXECUTORSERVICE.shutdown();
                CommandExecutor.EXECUTORSERVICE.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Run Command
         *
         * @param params params
         * @return result
         * @throws RemoteException
         */
        @Override
        public String command(String id, int timeout, String params) throws RemoteException {
            CommandExecutor executor = mCommandExecutorMap.get(id);
            if (executor == null) {
                synchronized (mCommandExecutorMap) {
                    executor = mCommandExecutorMap.get(id);
                    if (executor == null) {
                        executor = CommandExecutor.create(timeout, params);
                        if (executor != null) {
                            mCommandExecutorMap.put(id, executor);
                        }
                    }
                }
            }

            // Get Result
            String result = executor != null ? executor.getResult() : null;

            synchronized (mCommandExecutorMap) {
                try {
                    mCommandExecutorMap.remove(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        /**
         * Cancel command
         *
         * @param id command.id
         * @throws RemoteException
         */
        @Override
        public void cancel(String id) throws RemoteException {
            CommandExecutor executor = mCommandExecutorMap.get(id);
            if (executor != null) {
                synchronized (mCommandExecutorMap) {
                    try {
                        mCommandExecutorMap.remove(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                executor.destroy();
            }
        }

        /**
         * Get Task Count
         *
         * @return Map Count
         * @throws RemoteException
         */
        @Override
        public int getTaskCount() throws RemoteException {
            return mCommandExecutorMap.size();
        }
    }
}
