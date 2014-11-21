package net.qiujuer.genius.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import net.qiujuer.genius.Genius;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by QiuJu
 * on 2014/9/16.
 */
public final class Log {

    public static final int ALL = 0;
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    /**
     * Format
     */
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat FORMATTER_SIMPLE = new SimpleDateFormat("HH:mm:ss");

    private static boolean IsCallLog = false;
    private static int Level = ALL;
    private static LogWriter Writer;
    private static List<LogCallbackListener> callbackListeners;
    private static Handler handler = null;

    /**
     * *********************************************************************************************
     * init this class
     * *********************************************************************************************
     */
    static {
        callbackListeners = new ArrayList<LogCallbackListener>();
        Writer = null;

        initAsyncHandler();
    }

    private static void initAsyncHandler() {
        Thread thread = new Thread(Log.class.getName()) {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        Log log = (Log) msg.obj;
                        if (log != null) {
                            for (LogCallbackListener i : callbackListeners) {
                                try {
                                    i.onLogArrived(log);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                };
                Looper.loop();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * *********************************************************************************************
     * Set this class
     * *********************************************************************************************
     */
    /**
     * Set Level:
     * NOTHING = 6
     * ERROR = 5
     * WARN = 4
     * INFO = 3
     * DEBUG = 3
     * VERBOSE = 1
     * ALL = 0
     *
     * @param level Level
     */
    public static void setLevel(int level) {
        Level = level;
    }

    /**
     * Set call android.util.Log
     *
     * @param isCall Call
     */
    public static void setCallLog(boolean isCall) {
        IsCallLog = isCall;
    }


    /**
     * Set whether to open the log storage;
     * open the log will be stored in the software directory
     *
     * @param isOpen    Open
     * @param fileCount File Count,Default 10 Size
     * @param fileSize  One File Size,Default 2Mb
     */
    public static void setSaveLog(boolean isOpen, int fileCount, float fileSize) {
        if (Genius.getApplication() == null)
            throw new NullPointerException("Application is not null.Please Genius.initialize(Application)");

        if (isOpen) {
            if (Writer == null)
                Writer = new LogWriter(fileCount, fileSize, LogWriter.getDefaultLogPath());

        } else if (Writer != null) {
            Writer.unRegisterBroadCast();
            Writer.done();
            Writer = null;
        }
    }

    /**
     * set open or close to monitor external storage device status;
     * insert under open external storage device will automatically copy storage after the logs to the external devices
     * this setting depends on whether to open the log storage
     *
     * @param isCopy   Copy
     * @param filePath Copy of the storage folder, NULL using the default, closing to NULL;Default: SD/Genius/Logs
     */
    public static void setCopyExternalStorage(boolean isCopy, String filePath) {
        if (Writer != null) {
            if (isCopy)
                Writer.registerBroadCast(filePath);
            else
                Writer.unRegisterBroadCast();
        }
    }

    /**
     * dispose
     */
    public static void dispose() {
        if (Writer != null) {
            Writer.unRegisterBroadCast();
            Writer.done();
            Writer = null;
        }
        callbackListeners.clear();
    }

    /**
     * *********************************************************************************************
     * Public methods
     * *********************************************************************************************
     */

    /**
     * Copy log to ExternalStorage
     * This method depends on whether to open the log storage
     * The address can be integrated setCopyExternalStorage() method of the parameters on filePath is Null
     *
     * @param filePath Copy of the storage folder, NULL using the default, closing to NULL;Default: SD/Genius/Logs
     */
    public static void copyToExternalStorage(String filePath) {
        if (Writer != null)
            Writer.copyLogFile(filePath);
    }

    /**
     * Clear Log File
     */
    public static void clearLogFile() {
        if (Writer != null)
            Writer.clearLogFile();
    }

    /**
     * Add Listener
     *
     * @param listener OnLogCallbackListener
     */
    public static void addCallbackListener(LogCallbackListener listener) {
        callbackListeners.add(listener);
    }

    /**
     * Remove Listener
     *
     * @param listener OnLogCallbackListener
     */
    public static void removeCallbackListener(LogCallbackListener listener) {
        callbackListeners.remove(listener);
    }


    /**
     * *********************************************************************************************
     * Public Log methods
     * *********************************************************************************************
     */
    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return status
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
     * @return status
     */
    public static int v(String tag, String msg, Throwable tr) {
        if (Level <= VERBOSE) {

            if (tr != null)
                msg = msg + '\n' + android.util.Log.getStackTraceString(tr);

            Log log = new Log(VERBOSE, tag, msg);
            saveFile(log);
            arriveLog(log);
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
     * @return status
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
     * @return status
     */
    public static int d(String tag, String msg, Throwable tr) {
        if (Level <= DEBUG) {

            if (tr != null)
                msg = msg + '\n' + android.util.Log.getStackTraceString(tr);

            Log log = new Log(DEBUG, tag, msg);
            saveFile(log);
            arriveLog(log);
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
     * @return status
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
     * @return status
     */
    public static int i(String tag, String msg, Throwable tr) {
        if (Level <= INFO) {

            if (tr != null)
                msg = msg + '\n' + android.util.Log.getStackTraceString(tr);

            Log log = new Log(INFO, tag, msg);
            saveFile(log);
            arriveLog(log);
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
     * @return status
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
     * @return status
     */
    public static int w(String tag, String msg, Throwable tr) {
        if (Level <= WARN) {

            if (tr != null)
                msg = msg + '\n' + android.util.Log.getStackTraceString(tr);

            Log log = new Log(WARN, tag, msg);
            saveFile(log);
            arriveLog(log);
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
     * @return status
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
     * @return status
     */
    public static int e(String tag, String msg, Throwable tr) {
        if (Level <= ERROR) {

            if (tr != null)
                msg = msg + '\n' + android.util.Log.getStackTraceString(tr);

            Log log = new Log(ERROR, tag, msg);
            saveFile(log);
            arriveLog(log);
            return callLog(log);

        }
        return 0;
    }

    /**
     * *********************************************************************************************
     * Private methods
     * *********************************************************************************************
     */
    /**
     * Save File
     *
     * @param log Log
     */
    private static void saveFile(Log log) {
        if (Writer != null) try {
            Writer.addLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Call Android Log
     *
     * @param log Log
     * @return LogStatus
     */
    private static int callLog(Log log) {
        if (IsCallLog)
            return android.util.Log.println(log.getLevel(), log.getTag(), log.getMsg());
        else
            return 0;
    }

    /**
     * Call Back Log
     *
     * @param log Log
     */
    private static void arriveLog(Log log) {
        if (handler != null)
            handler.sendMessage(handler.obtainMessage(0x1, log));
    }

    /**
     * Class
     */
    private java.util.Date mDate;
    private int mLevel;
    private String mTag;
    private String mMsg;

    /**
     * Get a Log,Auto time
     *
     * @param level Level
     * @param tag   Tag
     * @param msg   Msg
     */
    public Log(int level, String tag, String msg) {
        this(new java.util.Date(), level, tag, msg);
    }

    /**
     * Get a Log
     *
     * @param date  Time
     * @param level Level
     * @param tag   Tag
     * @param msg   Msg
     */
    public Log(Date date, int level, String tag, String msg) {
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
        return (new java.util.Formatter().format("[%s][%s] %s:%s \r\n", FORMATTER.format(mDate), Level, mTag, mMsg)).toString();
    }

    /**
     * [11:42:21][2] Tag:Message
     *
     * @return [11:42:21][2] Tag:Message
     */
    public String toStringSimple() {
        return (new java.util.Formatter().format("[%s][%s] %s:%s \r\n", FORMATTER_SIMPLE.format(mDate), mLevel, mTag, mMsg)).toString();
    }

    /**
     * Get Time
     *
     * @return Date
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * Get Level
     *
     * @return Level
     */
    public int getLevel() {
        return mLevel;
    }

    /**
     * Get Tag
     *
     * @return Tag
     */
    public String getTag() {
        return mTag;
    }

    /**
     * Get Message
     *
     * @return Message
     */
    public String getMsg() {
        return mMsg;
    }

    /**
     * Interface
     */
    public interface LogCallbackListener {
        /**
         * On Log Arrived
         *
         * @param log GLog
         */
        public void onLogArrived(Log log);
    }
}



