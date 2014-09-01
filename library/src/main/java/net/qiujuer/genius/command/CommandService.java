package net.qiujuer.genius.command;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import net.qiujuer.genius.journal.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/13.
 * 命令行执行服务
 */
public class CommandService extends Service {
    private static final String TAG = CommandService.class.getName();
    private CommandServiceImpl mImpl;

    public CommandService() {
        mImpl = new CommandServiceImpl();
    }


    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(TAG, "Binding CommandService");
        return mImpl;
    }


    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "Release CommandService");
        if (mImpl != null) {
            mImpl.onDestroy();
            mImpl = null;
        }
        super.onDestroy();
    }

    /**
     * 继承自ICommandInterface接口的内部类
     */
    private class CommandServiceImpl extends ICommandInterface.Stub {
        //数据队列锁
        private List<CommandExecutor> commandExecutors = new ArrayList<CommandExecutor>();
        // 锁
        private Lock lock = new ReentrantLock();
        //守护线程
        private Thread thread;

        public CommandServiceImpl() {
            //线程初始化
            thread = new Thread(CommandServiceImpl.class.getName()) {
                @Override
                public void run() {
                    while (thread == this && !this.isInterrupted()) {
                        if (commandExecutors != null && commandExecutors.size() > 0) {
                            lock.lock();
                            LogUtils.i(TAG, "Executors Size:" + commandExecutors.size());
                            for (CommandExecutor executor : commandExecutors) {
                                if (executor.isTimeOut())
                                    try {
                                        killSelf();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                if (thread != this && this.isInterrupted())
                                    break;
                            }
                            lock.unlock();
                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * 销毁操作
         */
        protected void onDestroy() {
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
        }

        /**
         * 杀掉自己
         *
         * @throws RemoteException
         */
        @Override
        public void killSelf() throws RemoteException {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        /**
         * 执行命令
         *
         * @param params 命令
         * @return 结果
         * @throws RemoteException
         */
        @Override
        public String command(String params) throws RemoteException {
            CommandExecutor executor = CommandExecutor.create(params);
            lock.lock();
            commandExecutors.add(executor);
            lock.unlock();
            String result = executor.getResult();
            lock.lock();
            commandExecutors.remove(executor);
            lock.unlock();
            return result;
        }
    }
}
