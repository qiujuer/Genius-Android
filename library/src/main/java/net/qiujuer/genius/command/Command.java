package net.qiujuer.genius.command;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import net.qiujuer.genius.util.GLog;
import net.qiujuer.genius.util.GlobalValue;
import net.qiujuer.genius.util.ToolUtils;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/13.
 * 命令执行Model
 */
public class Command {
    private static final String TAG = Command.class.getName();
    //调用服务接口
    private static ICommandInterface iService = null;
    //Intent
    private static Intent intent = null;
    //服务链接类，用于实例化服务接口
    private static ServiceConnection conn = null;
    //锁
    private static Lock iLock = new ReentrantLock();
    //等待与唤醒
    private static Condition iCondition = iLock.newCondition();

    /**
     * 静态初始化
     */
    static {
        bindService();
    }

    /**
     * 执行测试
     *
     * @param command Command
     * @return 结果
     */
    public static String command(final Command command, CommandListener listener) {
        if (listener == null) {
            return command(command);
        } else {
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
        return null;
    }

    /**
     * 执行任务
     *
     * @param command Command
     * @return 结果
     */
    private static String command(Command command) {
        //check Service
        if (iService == null) {
            iLock.lock();
            try {
                iCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iLock.unlock();
        }
        int count = 10;
        while (count > 0) {
            if (command.isCancel) {
                if (command.listener != null)
                    command.listener.onCancel();
                return command.result;
            }
            try {
                command.result = iService.command(command.id, command.parameter);
                if (command.listener != null)
                    command.listener.onCompleted(command.result);
                return command.result;
            } catch (Exception e) {
                count--;
                ToolUtils.sleepIgnoreInterrupt(10000);
            }
        }
        if (command.listener != null)
            command.listener.onError();
        bindService();
        return command.result;
    }


    /**
     * 启动并绑定服务
     */
    private static void bindService() {
        Context context = GlobalValue.getApplicationContext();
        if (context == null) {
            throw new NullPointerException("ApplicationContext is not null.Please setApplicationContext()");
        } else {
            destroy();
            if (intent == null)
                intent = new Intent(context, CommandService.class);
            if (conn == null)
                initConnection();
            context.startService(intent);
            context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    private static void initConnection() {
        conn = new ServiceConnection() {
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
                GLog.i(TAG, "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                iService = null;
                GLog.i(TAG, "onServiceDisconnected");
            }
        };
    }

    /**
     * 销毁，取消服务绑定
     */
    public static void destroy() {
        if (iService != null) {
            try {
                iService.destroy();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            iService = null;
        }
        Context context = GlobalValue.getApplicationContext();
        if (context != null) {
            if (conn != null) {
                try {
                    context.unbindService(conn);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            if (intent != null) {
                context.stopService(intent);
                intent = null;
            }
        }
    }

    /**
     * 取消测试
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

    //ID
    private String id;
    //执行参数
    private String parameter = null;
    //是否取消测试
    private boolean isCancel = false;
    //回调事件
    private CommandListener listener = null;
    //结果
    private String result = null;

    /**
     * 实例化
     *
     * @param params @param params 命令参数 eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"
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
     * 执行结果回调
     */
    public interface CommandListener {
        public void onCompleted(String str);

        public void onCancel();

        public void onError();
    }
}