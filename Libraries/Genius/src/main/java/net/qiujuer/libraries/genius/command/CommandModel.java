package net.qiujuer.libraries.genius.command;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import net.qiujuer.libraries.genius.journal.LogUtil;
import net.qiujuer.libraries.genius.utils.GlobalValue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/13.
 * 命令执行Model
 */
public class CommandModel {
    private static final String TAG = CommandModel.class.getName();
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
                    e.printStackTrace();
                }
            } else
                bindService();
            iLock.unlock();
            LogUtil.i(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
            LogUtil.i(TAG, "onServiceDisconnected");
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
    public CommandModel(String... params) {
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
    public static String command(CommandModel model) {
        //检测是否取消测试
        if (model.isCancel)
            return null;
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

        String result;
        try {
            result = iService.command(model.parameter);
        } catch (Exception e) {
            e.printStackTrace();
            bindService();
            result = command(model);
        }
        return result;
    }

    /**
     * 启动并绑定服务
     */
    private static void bindService() {
        Context context = GlobalValue.getContext();
        Intent intent = new Intent(context, CommandService.class);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
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