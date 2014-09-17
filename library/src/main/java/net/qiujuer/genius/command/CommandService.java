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
 * Created by QiuJu
 * on 2014/9/17.
 */
class CommandService extends Service {
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


    private class CommandServiceImpl extends ICommandInterface.Stub {
        private Map<String, CommandExecutor> commandExecutorMap = new HashMap<String, CommandExecutor>();
        private Lock lock = new ReentrantLock();
        private Thread thread;

        public CommandServiceImpl() {
            //init
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
         * kill Service
         *
         * @throws RemoteException
         */
        @Override
        public void killService() throws RemoteException {
            stopSelf();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        /**
         * Run Command
         *
         * @param params params
         * @return result
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
         * cancel command
         *
         * @param id command.id
         * @throws RemoteException
         */
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

        /**
         * destroy
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


    }
}
