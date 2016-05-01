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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.UUID;

/**
 * Command same cmd line
 */
public final class Command {
    // Time Out is 90 seconds
    public static final int TIMEOUT = 90000;
    // IService Lock
    private static final Object I_LOCK = new Object();
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
     * Execute a Command
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
        Thread thread = DESTROY_THREAD;
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    try {
                        // we wait 5000ms, because the service maybe can use
                        Thread.sleep(5000);
                        dispose();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    DESTROY_THREAD = null;
                }
            };
            thread.setDaemon(true);
            DESTROY_THREAD = thread;
            thread.start();
        }
    }

    // Cancel Destroy Service
    private static void cancelDestroyService() {
        Thread thread = DESTROY_THREAD;
        if (thread != null) {
            DESTROY_THREAD = null;
            thread.interrupt();
        }
    }

    /**
     * Start bind Service
     */
    private static void bindService() {
        synchronized (Command.class) {
            if (!IS_BIND) {
                Context context = Cmd.getContext();
                if (context == null) {
                    throw new NullPointerException("Context not should null. Please call Cmd.init(Context)");
                } else {
                    // Cmd service
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
        // In this we should try 5 count get the result
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
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
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
    public static String command(Command command) throws Exception {
        // Check Service
        if (!IS_BIND)
            bindService();

        // Return
        try {
            return commandRun(command);
        } catch (Exception e) {
            e.printStackTrace();
            restart();
            throw e;
        }
    }

    /**
     * Command the test
     *
     * @param command Command
     */
    public static String command(final Command command, CommandListener listener) throws Exception {
        command.mListener = listener;
        return command(command);
    }

    /**
     * Cancel Test
     */
    public static void cancel(Command command) {
        command.isCancel = true;
        if (I_COMMAND != null) {
            try {
                I_COMMAND.cancel(command.mId);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (IS_BIND) {
                Context context = Cmd.getContext();
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
    public interface CommandListener {
        void onCompleted(String str);

        void onCancel();

        void onError(Exception e);
    }
}