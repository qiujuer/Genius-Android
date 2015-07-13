/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 08/13/2014
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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import net.qiujuer.genius.kit.GeniusKit;
import net.qiujuer.genius.kit.util.Tools;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Command same cmd line
 */
public final class Command {
    // Time Out is 90 seconds
    public static final int TIMEOUT = 90000;
    // IService Lock
    private static final Object I_LOCK = new Object();
    // Threads
    private static ExecutorService EXECUTORSERVICE = null;
    // ICommandInterface
    private static ICommandInterface I_COMMAND = null;
    // Mark If Bind Service
    private static boolean IS_BIND = false;
    // Service link class, used to instantiate the service interface
    private static ServiceConnection I_CONN = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (I_LOCK) {
                I_COMMAND = ICommandInterface.Stub.asInterface(service);
                if (I_COMMAND == null) {
                    restart();
                } else {
                    try {
                        I_LOCK.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            dispose();
        }
    };
    // Destroy Service Thread
    private static Thread DESTROY_THREAD = null;
    /**
     * *********************************************************************************************
     * Class
     * *********************************************************************************************
     */
    private int mTimeout = TIMEOUT;
    private String mId = null;
    private String mParameters = null;
    private String mResult = null;

    /**
     * *********************************************************************************************
     * Static public
     * *********************************************************************************************
     */
    private CommandListener mListener = null;
    private boolean isCancel = false;

    /**
     * Get a Command
     *
     * @param params params eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"
     */
    public Command(String... params) {
        this(TIMEOUT, params);
    }

    /**
     * Get a Command
     *
     * @param timeout set this run timeOut
     * @param params  params eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"
     */
    public Command(int timeout, String... params) {
        // Check params
        if (params == null)
            throw new NullPointerException("params is not null.");

        // Run
        StringBuilder sb = new StringBuilder();
        for (String str : params) {
            sb.append(str);
            sb.append(" ");
        }
        this.mParameters = sb.toString();
        this.mId = UUID.randomUUID().toString();
        this.mTimeout = timeout;
    }

    // Destroy Service After 5 seconds run
    private static void destroyService() {
        if (DESTROY_THREAD == null) {
            DESTROY_THREAD = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(5000);
                        dispose();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    DESTROY_THREAD = null;
                }
            };
            DESTROY_THREAD.setDaemon(true);
            DESTROY_THREAD.start();
        }
    }

    // Cancel Destroy Service
    private static void cancelDestroyService() {
        if (DESTROY_THREAD != null) {
            DESTROY_THREAD.interrupt();
            DESTROY_THREAD = null;
        }
    }

    /**
     * Start bind Service
     */
    private static void bindService() {
        synchronized (Command.class) {
            if (!IS_BIND) {
                Context context = GeniusKit.getApplication();
                if (context == null) {
                    throw new NullPointerException("Application is not null.Please Genius.initialize(Application)");
                } else {
                    // Init service
                    context.bindService(new Intent(context, CommandService.class), I_CONN, Context.BIND_AUTO_CREATE);
                    IS_BIND = true;
                }
            }
        }
    }

    /**
     * Run do Command
     *
     * @param command Command
     * @return Result
     */
    private static String commandRun(Command command) {
        // Wait bind
        if (I_COMMAND == null) {
            synchronized (I_LOCK) {
                if (I_COMMAND == null) {
                    try {
                        I_LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Cancel Destroy Service
        cancelDestroyService();

        // Get result
        int count = 5;
        Exception error = null;
        while (count > 0) {
            if (command.isCancel) {
                if (command.mListener != null)
                    command.mListener.onCancel();
                break;
            }
            try {
                command.mResult = I_COMMAND.command(command.mId, command.mTimeout, command.mParameters);
                if (command.mListener != null)
                    command.mListener.onCompleted(command.mResult);
                break;
            } catch (Exception e) {
                error = e;
                count--;
                Tools.sleepIgnoreInterrupt(3000);
            }
        }

        // Check is Error
        if (count <= 0 && command.mListener != null) {
            command.mListener.onError(error);
        }

        // Check is end and call destroy service
        if (I_COMMAND != null) {
            try {
                if (I_COMMAND.getTaskCount() <= 0)
                    destroyService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Return
        return command.mResult;
    }

    /**
     * Command the test
     *
     * @param command Command
     * @return Results
     */
    public static String command(Command command) {
        // Check Service
        if (!IS_BIND)
            bindService();

        // Return
        return commandRun(command);
    }

    /**
     * Command the test
     *
     * @param command Command
     */
    public static void command(final Command command, CommandListener listener) {
        command.mListener = listener;
        // Check Service
        if (!IS_BIND)
            bindService();

        // Check and init executor
        if (EXECUTORSERVICE == null) {
            synchronized (Command.class) {
                if (EXECUTORSERVICE == null) {
                    // Init threads executor
                    int size = Runtime.getRuntime().availableProcessors();
                    EXECUTORSERVICE = Executors.newFixedThreadPool(size > 0 ? size : 1);
                }
            }
        }

        // Add executorService thread in executor run
        try {
            EXECUTORSERVICE.execute(new Runnable() {
                @Override
                public void run() {
                    commandRun(command);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            restart();
        }
    }

    /**
     * Cancel Test
     */
    public static void cancel(Command command) {
        command.isCancel = true;
        if (I_COMMAND != null)
            try {
                I_COMMAND.cancel(command.mId);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Restart the Command Service
     */
    public static void restart() {
        dispose();
        bindService();
    }

    /**
     * Dispose unbindService stopService
     */
    public static void dispose() {
        synchronized (Command.class) {
            if (EXECUTORSERVICE != null) {
                try {
                    EXECUTORSERVICE.shutdownNow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EXECUTORSERVICE = null;
            }
            if (IS_BIND) {
                Context context = GeniusKit.getApplication();
                if (context != null) {
                    try {
                        context.unbindService(I_CONN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                I_COMMAND = null;
                IS_BIND = false;
            }
        }
    }

    /**
     * Delete the callback CommandListener
     */
    public void removeListener() {
        mListener = null;
    }

    /**
     * CommandListener
     */
    public static interface CommandListener {
        public void onCompleted(String str);

        public void onCancel();

        public void onError(Exception e);
    }
}