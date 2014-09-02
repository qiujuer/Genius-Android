package net.qiujuer.genius.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class GLog {
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

    /**
     * 格式化输出
     */
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化输出
     */
    private static final SimpleDateFormat FORMATTER_SIMPLE = new SimpleDateFormat("HH:mm:ss");

    //内存中存储数量
    private static int MemorySize = 20;
    //是否调用Log
    private static boolean IsCallLog = false;
    //是否保存文件
    private static boolean IsSaveLog = false;
    //日志级别
    private static int Level = 1;
    //操作内存日志列表锁
    private static Lock LogListLock = new ReentrantLock();
    //写入文件类
    private static GLogWriter GLogWriter;
    //内存日志列表
    private static List<GLog> logList;
    //接口列表
    private static List<OnLogCallbackListener> callbackListeners;


    //初始化静态变量
    static {
        callbackListeners = new ArrayList<OnLogCallbackListener>();
        logList = new ArrayList<GLog>(MemorySize);
        GLogWriter = null;
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
            if (GLogWriter == null)
                GLogWriter = new GLogWriter(fileCount, fileSize,
                        context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + (filePath == null ? "Logs" : filePath));
        } else if (GLogWriter != null) {
            GLogWriter.unRegisterBroadCast(context);
            GLogWriter.done();
            GLogWriter = null;
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
            GLogWriter.registerBroadCast(context, filePath);
        else
            GLogWriter.unRegisterBroadCast(context);
    }

    /**
     * 清空日志文件
     */
    public static void clearLogFile() {
        if (GLogWriter != null)
            GLogWriter.clearLogFile();
    }

    /**
     * 添加接口，当有日志时将通知
     *
     * @param listener LogsInterface
     */
    public static void addLogCallbackListener(OnLogCallbackListener listener) {
        callbackListeners.add(listener);
        onListenerAdded(listener);
    }

    /**
     * 删除指定的日子接口
     *
     * @param listener LogsInterface
     */
    public static void removeLogsInterface(OnLogCallbackListener listener) {
        callbackListeners.remove(listener);
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

            GLog log = new GLog(VERBOSE, tag, msg);
            saveMemory(log);
            saveFile(log);
            onLogArrived(log);
            return callLog(log);

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

            GLog log = new GLog(DEBUG, tag, msg);
            saveMemory(log);
            saveFile(log);
            onLogArrived(log);
            return callLog(log);

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

            GLog log = new GLog(INFO, tag, msg);
            saveMemory(log);
            saveFile(log);
            onLogArrived(log);
            return callLog(log);

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

            GLog log = new GLog(WARN, tag, msg);
            saveMemory(log);
            saveFile(log);
            onLogArrived(log);
            return callLog(log);

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

            GLog log = new GLog(ERROR, tag, msg);
            saveMemory(log);
            saveFile(log);
            onLogArrived(log);
            return callLog(log);

        }
        return 0;
    }


    //写入文件
    private static void saveFile(GLog log) {
        if (!IsSaveLog)
            return;

        if (GLogWriter != null) try {
            GLogWriter.addLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //保存到内存
    private static void saveMemory(GLog log) {
        LogListLock.lock();
        if (logList.size() >= MemorySize) {
            logList.remove(0);
        }
        logList.add(log);
        LogListLock.unlock();
    }

    //调用Android Log
    private static int callLog(GLog log) {
        if (IsCallLog)
            return Log.println(log.getLevel(), log.getTag(), log.getMsg());
        else
            return 0;
    }

    //回调onLogArrived
    private static void onLogArrived(GLog log) {
        for (OnLogCallbackListener i : callbackListeners) {
            try {
                i.onLogArrived(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //回调onListenerAdded
    private static void onListenerAdded(final OnLogCallbackListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GLog[] logArray = new GLog[logList.size()];
                    LogListLock.lock();
                    logList.toArray(logArray);
                    listener.onListenerAdded(logArray);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LogListLock.unlock();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    //toString
    private String mFormatterStr = null;
    //toString SimpleStr
    private String mFormatterSimpleStr = null;
    //时间
    private java.util.Date mDate;
    //级别
    private int mLevel;
    //Tag
    private String mTag;
    //内容
    private String mMsg;

    /**
     * 实例化GLog，自动获取时间
     *
     * @param level 等级
     * @param tag   Tag
     * @param msg   Msg
     */
    public GLog(int level, String tag, String msg) {
        this(new java.util.Date(), level, tag, msg);
    }

    /**
     * 实例化GLog
     *
     * @param date  时间
     * @param level 等级
     * @param tag   Tag
     * @param msg   Msg
     */
    public GLog(Date date, int level, String tag, String msg) {
        mDate = date;
        mLevel = level;
        mTag = tag;
        mMsg = msg;
    }

    /**
     * [2014-09-02 11:42:21][2] Tag:Message
     *
     * @return [2014-09-02 11:42:21][2] Tag:Message
     */
    public String toString() {
        if (mFormatterStr == null)
            mFormatterStr = (new java.util.Formatter().format("[%s][%s] %s:%s \r\n", FORMATTER.format(mDate), Level, mTag, mMsg)).toString();
        return mFormatterStr;
    }

    /**
     * [11:42:21][2] Tag:Message
     *
     * @return [11:42:21][2] Tag:Message
     */
    public String toStringSimple() {
        if (mFormatterSimpleStr == null)
            mFormatterSimpleStr = (new java.util.Formatter().format("[%s][%s] %s:%s \r\n", FORMATTER_SIMPLE.format(mDate), mLevel, mTag, mMsg)).toString();
        return mFormatterSimpleStr;
    }

    /**
     * 获取时间
     *
     * @return 时间
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * 获取等级
     *
     * @return 等级
     */
    public int getLevel() {
        return mLevel;
    }

    /**
     * 获取Tag
     *
     * @return Tag
     */
    public String getTag() {
        return mTag;
    }

    /**
     * 获取主体
     *
     * @return Message
     */
    public String getMsg() {
        return mMsg;
    }

    /**
     * Created by Genius on 2014/9/2.
     * 程序界面接口
     * 实现日志的界面显示
     * 当有日志来临时显示日志信息
     */
    public interface OnLogCallbackListener {
        /**
         * 日志到达时触发
         *
         * @param log GLog
         */
        public void onLogArrived(GLog log);

        /**
         * 添加一个Listener后触发
         *
         * @param logArray GLog Array
         */
        public void onListenerAdded(GLog[] logArray);
    }
}



