/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/16/2014
 * Changed 03/08/2015
 * Version 3.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.genius.kit.util;

import net.qiujuer.genius.kit.GeniusKit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is Log
 * Using methods like the system Log class
 * <p/>
 * Have {@link #Level} to show custom
 * Have {@link #IsCallLog} to call system log class
 * <p/>
 * Can callback to interface {@link net.qiujuer.genius.kit.util.Log.LogCallbackListener}
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
    private static CallBackManager callBackManager;

    /**
     * *********************************************************************************************
     * init this class
     * *********************************************************************************************
     */
    static {
        callbackListeners = new ArrayList<>();
        Writer = null;
    }

    /**
     * *********************************************************************************************
     * Set this class
     * *********************************************************************************************
     */

    /**
     * Class
     */
    private Date mDate;
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
        this(new Date(), level, tag, msg);
    }

    /**
     * *********************************************************************************************
     * Public methods
     * *********************************************************************************************
     */

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
        if (GeniusKit.getApplication() == null)
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
     * *********************************************************************************************
     * Public Log methods
     * *********************************************************************************************
     */

    /**
     * dispose
     */
    public static void dispose() {
        if (Writer != null) {
            Writer.unRegisterBroadCast();
            Writer.done();
            Writer = null;
        }
        removeCallbackListener(null);
    }

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
        if (callBackManager == null)
            callBackManager = new CallBackManager();
    }

    /**
     * Remove Listener
     *
     * @param listener OnLogCallbackListener
     */
    public static void removeCallbackListener(LogCallbackListener listener) {
        if (listener == null)
            callbackListeners.clear();
        else
            callbackListeners.remove(listener);

        // destroy
        if (callbackListeners.size() <= 0 && callBackManager != null) {
            CallBackManager manager = callBackManager;
            callBackManager = null;
            manager.dispose();
        }
    }

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
     * *********************************************************************************************
     * Private methods
     * *********************************************************************************************
     */

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
        if (callBackManager != null)
            callBackManager.notifyLog(log);
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
    public static interface LogCallbackListener {
        /**
         * On Log Arrived
         *
         * @param log GLog
         */
        public void onLogArrived(Log log);
    }

    /**
     * CallBack Manager class
     */
    static class CallBackManager extends Thread {
        private final Lock queueLock;
        private final Condition queueNotify;
        private Queue<Log> logQueue;
        private boolean isActive;

        public CallBackManager() {
            logQueue = new LinkedList<>();
            queueLock = new ReentrantLock();
            queueNotify = queueLock.newCondition();

            this.setDaemon(true);
            this.start();
        }

        public void notifyLog(Log log) {
            if (logQueue == null)
                return;
            try {
                queueLock.lock();
                logQueue.offer(log);
                if (!isActive) {
                    isActive = true;
                    queueNotify.signalAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                queueLock.unlock();
            }
        }

        public void dispose() {
            try {
                queueLock.lock();
                if (logQueue != null) {
                    logQueue.clear();
                    logQueue = null;
                }
                queueNotify.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                queueLock.unlock();
            }
        }

        @Override
        public void run() {
            while (logQueue != null) {
                Log log;
                try {
                    log = logQueue.poll();
                } catch (NoSuchElementException e) {
                    log = null;
                }
                if (log == null) {
                    try {
                        queueLock.lock();
                        log = logQueue.poll();
                        if (log == null) {
                            isActive = false;
                            queueNotify.await();
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        queueLock.unlock();
                    }
                }

                // notify
                for (LogCallbackListener i : callbackListeners) {
                    try {
                        i.onLogArrived(log);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}



