package net.qiujuer.libraries.genius.journal;

import android.content.Context;
import android.util.Log;

import net.qiujuer.libraries.genius.utils.GlobalValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/16.
 * 自定义日志类，使用方法与系统日志Log类似
 * 可添加接口到界面显示日志
 * 可写入文件，默认写入到内存中，插入SD卡时拷贝到SD卡中
 * 可设置日志文件大小以及数量，可清空日志文件
 */
public class LogUtil {
    /**
     * Priority constant for the println method ALL.
     */
    public static final int ALL = 1;

    /**
     * Priority constant for the println method; use LogUtil.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use LogUtil.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use LogUtil.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use LogUtil.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use LogUtil.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    /**
     * Priority constant for the println method NOTHING.
     */
    public static final int NOTHING = 8;

    //内存中存储数量
    private static int MemorySize = 20;
    //是否调用Log
    private static boolean IsCallLog = false;
    //是否保存文件
    private static boolean IsSaveLog = false;
    //日志级别
    private static int Level = 1;
    //写入文件类
    private static LogFile logFile;
    //接口列表
    private static List<LogCallbackListener> logCallbackListeners;
    //内存日志列表
    private static List<LogData> logDataList;
    //操作内存日志列表锁
    private static Lock LogsDataListLock = new ReentrantLock();

    //初始化静态变量
    static {
        logCallbackListeners = new ArrayList<LogCallbackListener>();
        logDataList = new ArrayList<LogData>(MemorySize);
        logFile = null;
    }

    /**
     * 设置日志级别；分布如下
     * NOTHING = 8
     * ASSERT = 7
     * ERROR = 6
     * WARN = 5
     * INFO = 4
     * DEBUG = 3
     * VERBOSE = 2
     * ALL = 1
     * 打开所有设置（ALL），关闭所有（NOTHING），开启ERROR，WARN（WARN），开启ERROR，WARN，INFO（INFO）
     *
     * @param level 级别
     */
    public static void setLevel(int level) {
        Level = level;
    }

    /**
     * 设置是否开启调用Android Log
     *
     * @param isCall 是否开启
     */
    public static void setCallLog(boolean isCall) {
        IsCallLog = isCall;
    }


    /**
     * 设置是否打开日志存储；
     * 打开状态下将在软件目录中存储日志
     *
     * @param context   Context
     * @param isOpen    是否开启
     * @param fileCount 日志文件最大数量;默认10个文件
     * @param fileSize  单个日志文件最大值，单位兆；默认2M
     * @param filePath  日志存储文件夹,NULL采用默认，关闭时为NULL即可；默认：软件安装目录/Logs
     */
    public static void setSaveLog(Context context, boolean isOpen, int fileCount, float fileSize, String filePath) {
        IsSaveLog = isOpen;

        if (context == null && (context = GlobalValue.getContext()) == null)
            return;

        if (IsSaveLog) {
            if (logFile == null)
                logFile = new LogFile(fileCount, fileSize,
                        context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + (filePath == null ? "Logs" : filePath));
        } else if (logFile != null) {
            logFile.unRegisterBroadCast(context);
            logFile.done();
            logFile = null;
        }
    }

    /**
     * 设置打开或关闭监听外部存储设备状态；
     * 开启状态下插入外部存储设备后将自动拷贝存储中的日志到外部设备
     * 此设置依赖于是否开启日志存储
     *
     * @param context  Context
     * @param isCopy   是否开启拷贝到外部存储
     * @param filePath 拷贝的存储文件夹,NULL采用默认,关闭时为NULL即可；默认：SD/Genius/Logs
     */
    public static void setCopyExternalStorage(Context context, boolean isCopy, String filePath) {
        if ((context == null && (context = GlobalValue.getContext()) == null) || !IsSaveLog)
            return;

        if (isCopy)
            logFile.registerBroadCast(context, filePath);
        else
            logFile.unRegisterBroadCast(context);
    }

    /**
     * 清空日志文件
     */
    public static void clearLogFile() {
        if (logFile != null)
            logFile.clearLogFile();
    }

    /**
     * 添加接口，当有日志时将通知
     *
     * @param listener LogsInterface
     */
    public static void addLogCallbackListener(LogCallbackListener listener) {
        logCallbackListeners.add(listener);
        onListenerAdded(listener);
    }

    /**
     * 删除指定的日子接口
     *
     * @param listener LogsInterface
     */
    public static void removeLogsInterface(LogCallbackListener listener) {
        logCallbackListeners.remove(listener);
    }


    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        return v(tag, msg, null);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int v(String tag, String msg, Throwable tr) {
        if (Level <= VERBOSE) {

            if (tr != null)
                msg = msg + '\n' + Log.getStackTraceString(tr);

            LogData data = new LogData(VERBOSE, tag, msg);
            saveMemory(data);
            saveFile(data);
            onLogArrived(data);
            return callLog(data);

        }
        return 0;
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        return d(tag, msg, null);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int d(String tag, String msg, Throwable tr) {
        if (Level <= DEBUG) {

            if (tr != null)
                msg = msg + '\n' + Log.getStackTraceString(tr);

            LogData data = new LogData(DEBUG, tag, msg);
            saveMemory(data);
            saveFile(data);
            onLogArrived(data);
            return callLog(data);

        }
        return 0;
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {
        return i(tag, msg, null);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int i(String tag, String msg, Throwable tr) {
        if (Level <= INFO) {

            if (tr != null)
                msg = msg + '\n' + Log.getStackTraceString(tr);

            LogData data = new LogData(INFO, tag, msg);
            saveMemory(data);
            saveFile(data);
            onLogArrived(data);
            return callLog(data);

        }
        return 0;
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        return w(tag, msg, null);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int w(String tag, String msg, Throwable tr) {
        if (Level <= WARN) {

            if (tr != null)
                msg = msg + '\n' + Log.getStackTraceString(tr);

            LogData data = new LogData(WARN, tag, msg);
            saveMemory(data);
            saveFile(data);
            onLogArrived(data);
            return callLog(data);

        }
        return 0;
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        return e(tag, msg, null);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int e(String tag, String msg, Throwable tr) {
        if (Level <= ERROR) {

            if (tr != null)
                msg = msg + '\n' + Log.getStackTraceString(tr);

            LogData data = new LogData(ERROR, tag, msg);
            saveMemory(data);
            saveFile(data);
            onLogArrived(data);
            return callLog(data);

        }
        return 0;
    }


    //写入文件
    private static void saveFile(LogData data) {
        if (!IsSaveLog)
            return;

        if (logFile != null) try {
            logFile.addLog(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //保存到内存
    private static void saveMemory(LogData data) {
        LogsDataListLock.lock();
        if (logDataList.size() >= MemorySize) {
            logDataList.remove(0);
        }
        logDataList.add(data);
        LogsDataListLock.unlock();
    }

    //调用Android Log
    private static int callLog(LogData data) {
        if (IsCallLog)
            return Log.println(data.getLevel(), data.getTag(), data.getMsg());
        else
            return 0;
    }

    //回调onLogArrived
    private static void onLogArrived(LogData data) {
        for (LogCallbackListener i : logCallbackListeners) {
            try {
                i.OnLogArrived(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //回调onListenerAdded
    private static void onListenerAdded(final LogCallbackListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogData[] dataArray = new LogData[logDataList.size()];
                    LogsDataListLock.lock();
                    logDataList.toArray(dataArray);
                    listener.OnListenerAdded(dataArray);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LogsDataListLock.unlock();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
