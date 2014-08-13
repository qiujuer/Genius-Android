package net.qiujuer.libraries.genius.journal;

import android.content.Context;
import android.util.Log;

import net.qiujuer.libraries.genius.methods.StaticValues;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/13.
 * 自定义日志类，使用方法与系统日志Log类似
 * 可添加接口到界面显示日志
 * 可写入文件，默认写入到内存中，插入SD卡时拷贝到SD卡中
 */
public class Logs {
    //保留在内存中最近日志数
    public final static int SAVE_LENGTH = 25;

    public static boolean VERBOSE = true;
    public static boolean DEBUG = true;
    public static boolean INFO = true;
    public static boolean WARN = true;
    public static boolean ERROR = true;
    public static boolean IsTriggerSystem = false;
    public static boolean IsWriteFile = false;

    //写入文件类
    private static LogsFile mLogsFile;
    //接口列表
    private static List<LogsInterface> LogsInterfaces;
    //内存日志列表
    private static List<LogsData> LogsDataList;
    //操作内存日志列表锁
    private static Lock LogsDataListLock = new ReentrantLock();

    //初始化静态变量
    static {
        LogsInterfaces = new ArrayList<LogsInterface>();
        LogsDataList = new ArrayList<LogsData>(SAVE_LENGTH);
        //设置等级
        //setLevel(4);
        //是否调用系统Log类
        IsTriggerSystem = true;
        //是否到写入文件中
        //IsWriteFile = true;
    }

    /**
     * 设置日志级别，可单独设置 eg:DEBUG = true
     *
     * @param level 级别VERBOSE（5）~ERROR（1）
     */
    public static void setLevel(int level) {
        VERBOSE = DEBUG = INFO = WARN = ERROR = true;
        if (level < 5)
            VERBOSE = false;
        if (level < 4)
            DEBUG = false;
        if (level < 3)
            INFO = false;
        if (level < 2)
            WARN = false;
        if (level < 1)
            ERROR = false;
    }

    /**
     * 设置是否启动监听SD卡状态，已便拷贝日志到SD卡，开启状态下插入SD时将拷贝日志到SD卡
     *
     * @param isWrite 是否监听并实现拷贝
     */
    public static void setWriteExternalStorage(boolean isWrite) {
        Context context = StaticValues.getContext();
        if (context == null || mLogsFile == null)
            return;

        if (isWrite)
            mLogsFile.registerBroadCast(context);
        else
            mLogsFile.unRegisterBroadCast(context);
    }

    /**
     * 添加接口，当有日志时将通知
     *
     * @param face LogsInterface
     */
    public static void addLogsInterface(LogsInterface face) {
        LogsInterfaces.add(face);
        addedLogsInterface(face);
    }

    /**
     * 删除指定的日子接口
     *
     * @param face LogsInterface
     */
    public static void removeLogsInterface(LogsInterface face) {
        LogsInterfaces.remove(face);
    }

    public static void v(String tag, String msg) {
        LogsData data = new LogsData(LogsData.VerBose, tag, msg);
        v(data);
    }

    public static void d(String tag, String msg) {
        LogsData data = new LogsData(LogsData.EnumDebug, tag, msg);
        d(data);
    }

    public static void i(String tag, String msg) {
        LogsData data = new LogsData(LogsData.EnumInfo, tag, msg);
        i(data);
    }

    public static void w(String tag, String msg) {
        LogsData data = new LogsData(LogsData.EnumWarn, tag, msg);
        w(data);
    }

    public static void e(String tag, String msg) {
        LogsData data = new LogsData(LogsData.EnumError, tag, msg);
        e(data);
    }


    public static void v(LogsData data) {
        writeLogsFile(data);

        if (VERBOSE) {
            saveInMemory(data);
            if (IsTriggerSystem)
                Log.v(data.getTag(), data.getMsg());

            for (LogsInterface i : LogsInterfaces) {
                notifyShowLogListener(i, data);
            }
        }
    }

    public static void d(LogsData data) {
        writeLogsFile(data);

        if (DEBUG) {
            saveInMemory(data);
            if (IsTriggerSystem)
                Log.d(data.getTag(), data.getMsg());

            for (LogsInterface i : LogsInterfaces) {
                notifyShowLogListener(i, data);
            }
        }
    }

    public static void i(LogsData data) {
        writeLogsFile(data);

        if (INFO) {
            saveInMemory(data);
            if (IsTriggerSystem)
                Log.d(data.getTag(), data.getMsg());

            for (LogsInterface i : LogsInterfaces) {
                notifyShowLogListener(i, data);
            }
        }
    }

    public static void w(LogsData data) {
        writeLogsFile(data);

        if (WARN) {
            saveInMemory(data);
            if (IsTriggerSystem)
                Log.d(data.getTag(), data.getMsg());

            for (LogsInterface i : LogsInterfaces) {
                notifyShowLogListener(i, data);
            }
        }
    }

    public static void e(LogsData data) {
        writeLogsFile(data);

        if (ERROR) {
            saveInMemory(data);
            if (IsTriggerSystem)
                Log.d(data.getTag(), data.getMsg());

            for (LogsInterface i : LogsInterfaces) {
                notifyShowLogListener(i, data);
            }
        }
    }

    /**
     * 将日志写入文件
     *
     * @param data LogsData
     */
    private static void writeLogsFile(LogsData data) {
        if (!IsWriteFile)
            return;

        if (mLogsFile == null) {
            synchronized (Logs.class) {
                if (mLogsFile == null)
                    mLogsFile = new LogsFile(StaticValues.getContext(), false);
            }
        }
        try {
            mLogsFile.addLog(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将日志保存在内存中
     *
     * @param data LogsData
     */
    private static void saveInMemory(LogsData data) {
        LogsDataListLock.lock();
        if (LogsDataList.size() >= SAVE_LENGTH) {
            LogsDataList.remove(0);
        }
        LogsDataList.add(data);
        LogsDataListLock.unlock();
    }

    /**
     * 通知接口日志
     *
     * @param i    接口
     * @param data LogsData
     */
    private static void notifyShowLogListener(LogsInterface i, LogsData data) {
        try {
            i.OnShowLogListener(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加接口后异步操作：通知接口显示内存中的日志
     *
     * @param face LogsInterface
     */
    private static void addedLogsInterface(final LogsInterface face) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<LogsData> noteList;
                    LogsDataListLock.lock();
                    noteList = new ArrayList<LogsData>(LogsDataList);
                    LogsDataListLock.unlock();
                    face.OnAddedLogListener(noteList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
