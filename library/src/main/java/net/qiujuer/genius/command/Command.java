package net.qiujuer.genius.command;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import net.qiujuer.genius.util.GLog;
import net.qiujuer.genius.util.GlobalValue;

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
    //服务链接类，用于实例化服务接口
    private static ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iLock.lock();
            iService = ICommandInterface.Stub.asInterface(service);
            if (iService != null) {
                try {
                    iCondition.signalAll();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } else
                bindService();
            iLock.unlock();
            GLog.i(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
            GLog.i(TAG, "onServiceDisconnected");
        }
    };
    //锁
    private static Lock iLock = new ReentrantLock();
    //等待与唤醒
    private static Condition iCondition = iLock.newCondition();

    //执行参数
    private String parameter;
    //是否取消测试
    private boolean isCancel;

    /**
     * 实例化
     *
     * @param params @param params 命令参数 eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"
     */
    public Command(String... params) {
        //check params
        if (params == null)
            throw new NullPointerException();
        //run
        StringBuilder sb = new StringBuilder();
        for (String str : params) {
            sb.append(str);
            sb.append(" ");
        }
        this.parameter = sb.toString();
    }

    /**
     * 执行测试
     *
     * @param model ProcessModel
     * @return 结果
     */
    public static String command(Command model) {
        //检测是否取消测试
        if (model.isCancel)
            return null;
        //check Service
        if (iService == null) {
            iLock.lock();
            try {
                iCondition.await();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            iLock.unlock();
        }

        String result;
        try {
            result = iService.command(model.parameter);
        } catch (Exception e) {
            //e.printStackTrace();
            bindService();
            result = command(model);
        }
        return result;
    }

    /**
     * 启动并绑定服务
     */
    private static void bindService() {
        destroy();
        Context context = GlobalValue.getContext();
        Intent intent = new Intent(context, CommandService.class);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 销毁，取消服务绑定
     */
    public static void destroy() {
        try {
            GlobalValue.getContext().unbindService(conn);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        if (iService != null) {
            try {
                iService.killSelf();
            } catch (RemoteException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 取消测试
     */
    public void cancel() {
        isCancel = true;
    }

    /**
     * 静态初始化
     */
    static {
        bindService();
    }

}