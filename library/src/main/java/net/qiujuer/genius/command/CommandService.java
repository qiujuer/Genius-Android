package net.qiujuer.genius.command;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/13.
 * 命令行执行服务
 */
public class CommandService extends Service {
    private CommandServiceImpl mImpl;

    public CommandService() {
        mImpl = new CommandServiceImpl();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mImpl == null)
            mImpl = new CommandServiceImpl();
        return mImpl;
    }


    @Override
    public void onDestroy() {
        if (mImpl != null) {
            try {
                mImpl.destroy();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mImpl = null;
        }
        super.onDestroy();
    }

    /**
     * 继承自ICommandInterface接口的内部类
     */
    private class CommandServiceImpl extends ICommandInterface.Stub {
        //数据队列锁
        private Map<String, CommandExecutor> commandExecutorMap = new HashMap<String, CommandExecutor>();
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
                        if (commandExecutorMap != null && commandExecutorMap.size() > 0) {
                            lock.lock();
                            Collection<CommandExecutor> commandExecutors = commandExecutorMap.values();
                            for (CommandExecutor executor : commandExecutors) {
                                if (executor.isTimeOut())
                                    try {
                                        killService();
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
         * 杀掉自己
         *
         * @throws RemoteException
         */
        @Override
        public void killService() throws RemoteException {
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
        public String command(String id, String params) throws RemoteException {
            CommandExecutor executor = CommandExecutor.create(params);
            lock.lock();
            commandExecutorMap.put(id, executor);
            lock.unlock();
            String result = executor.getResult();
            lock.lock();
            commandExecutorMap.remove(id);
            lock.unlock();
            return result;
        }

        /**
         * 销毁操作
         */
        @Override
        public void destroy() throws RemoteException {
            lock.lock();
            commandExecutorMap.clear();
            lock.lock();
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
            mImpl = null;
        }

        @Override
        public void cancel(String id) throws RemoteException {
            CommandExecutor executor = commandExecutorMap.get(id);
            if (executor != null) {
                lock.lock();
                commandExecutorMap.remove(id);
                lock.unlock();
                executor.destroy();
            }
        }

    }
}
