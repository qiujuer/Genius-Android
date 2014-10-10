package net.qiujuer.genius.command;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.util.ToolUtils;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by QiuJu
 * on 2014/8/13.
 */
public final class Command {
    private static final String TAG = Command.class.getSimpleName();
    //ICommandInterface
    private static ICommandInterface iService = null;
    //Service link class, used to instantiate the service interface
    private static ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iLock.lock();
            iService = ICommandInterface.Stub.asInterface(service);
            if (iService != null) {
                try {
                    iCondition.signalAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                bindService();
            }
            iLock.unlock();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
        }
    };
    //Lock
    private static Lock iLock = new ReentrantLock();
    private static Condition iCondition = iLock.newCondition();
    //Mark If Bind Service
    private static boolean isBindService = false;
    //Context
    private static Context mContext;


    /**
     * start bind Service
     */
    private synchronized static void bindService() {
        dispose();
        mContext = Genius.getApplication();
        if (mContext == null) {
            throw new NullPointerException("Application is not null.Please Genius.initialize(Application)");
        } else {
            mContext.bindService(new Intent(mContext, CommandService.class), conn, Context.BIND_AUTO_CREATE);
            isBindService = true;
        }
    }

    /**
     * *********************************************************************************************
     * Static public
     * *********************************************************************************************
     */

    /**
     * Command the test
     *
     * @param command Command
     * @return Results
     */
    public static String command(Command command) {
        //check Service
        if (!isBindService)
            bindService();
        if (iService == null) {
            iLock.lock();
            try {
                iCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iLock.unlock();
        }
        //Get result
        int count = 10;
        while (count > 0) {
            if (command.isCancel) {
                if (command.listener != null)
                    command.listener.onCancel();
                break;
            }
            try {
                command.result = iService.command(command.id, command.parameter);
                if (command.listener != null)
                    command.listener.onCompleted(command.result);
                break;
            } catch (Exception e) {
                count--;
                ToolUtils.sleepIgnoreInterrupt(3000);
            }
        }
        //Check is Error
        if (count <= 0) {
            bindService();
            if (command.listener != null)
                command.listener.onError();
        }
        command.listener = null;
        //Check is end
        try {
            if (iService.getTaskCount() <= 0)
                dispose();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //Return
        return command.result;
    }

    /**
     * Command the test
     *
     * @param command Command
     */
    public static void command(final Command command, CommandListener listener) {
        command.listener = listener;
        Thread thread = new Thread() {
            @Override
            public void run() {
                command(command);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * cancel Test
     */
    public static void cancel(Command command) {
        command.isCancel = true;
        if (iService != null)
            try {
                iService.cancel(command.id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }

    /**
     * dispose unbindService stopService
     */
    public static void dispose() {
        if (isBindService) {
            if (iService != null) {
                try {
                    iService.dispose();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                iService = null;
            }
            mContext.unbindService(conn);
            mContext = null;
            isBindService = false;
        }
    }

    /**
     * *********************************************************************************************
     * Class
     * *********************************************************************************************
     */
    private String id;
    private String parameter = null;
    private boolean isCancel = false;
    private CommandListener listener = null;
    private String result = null;

    /**
     * Get a Command
     *
     * @param params params eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"
     */
    public Command(String... params) {
        //check params
        if (params == null)
            throw new NullPointerException("params is not null.");
        //run
        StringBuilder sb = new StringBuilder();
        for (String str : params) {
            sb.append(str);
            sb.append(" ");
        }
        this.parameter = sb.toString();
        this.id = UUID.randomUUID().toString();
    }

    /**
     * CommandListener
     */
    public interface CommandListener {
        public void onCompleted(String str);

        public void onCancel();

        public void onError();
    }
}