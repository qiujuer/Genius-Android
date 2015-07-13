/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/02/2014
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import net.qiujuer.genius.kit.GeniusKit;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This Log class {@link net.qiujuer.genius.kit.util.Log} to writer file class
 */
class LogWriter extends Thread {
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    private final Lock mWriteLock = new ReentrantLock();
    private final Lock mQueueLock = new ReentrantLock();
    private final Condition mQueueNotify = mQueueLock.newCondition();

    // Save File info
    private long mFileSize = 2 * 1024 * 1024;
    private int mFileCount = 10;

    private String mFilePath = null;
    private String mExternalStoragePath = "Genius" + File.separator + "Logs";

    private String mLogName = null;
    private String mLogFilePathName = null;

    private Queue<Log> mLogs = null;
    private FileWriter mFileWriter = null;

    private BroadcastReceiver mUsbBroadCastReceiver = null;

    private boolean isDone = false;


    /**
     * LogFile
     *
     * @param count file count
     * @param size  file size
     * @param path  save path
     */
    protected LogWriter(int count, float size, String path) {
        mLogs = new LinkedList<>();

        mFileSize = (long) size * 1024 * 1024;
        mFileCount = count;
        mFilePath = path;

        init();

        this.setName(LogWriter.class.getName());
        this.setDaemon(true);
        this.start();
    }


    /**
     * *********************************************************************************************
     * private methods
     * *********************************************************************************************
     */

    /**
     * get log file path
     */
    protected static String getDefaultLogPath() {
        return GeniusKit.getApplication().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "Genius" + File.separator + "Logs";
    }

    /**
     * init
     *
     * @return status
     */
    private boolean init() {
        boolean bFlag = false;

        if (initFilePath() && initLogNameSize()) {
            deleteOldLogFile();
            bFlag = true;
        }

        return bFlag;
    }

    /**
     * init FilePath
     *
     * @return status
     */
    private boolean initFilePath() {
        boolean bFlag;
        try {
            File file = new File(mFilePath);
            bFlag = file.isDirectory() || file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            bFlag = false;
        }
        return bFlag;
    }

    /**
     * init LogNameSize
     *
     * @return status
     */
    private boolean initLogNameSize() {
        boolean bFlag;
        //close fileWriter
        if (mFileWriter != null) {
            try {
                mFileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFileWriter = null;
        }
        //init File
        try {
            File file = new File(mFilePath);
            if (file.listFiles() != null && file.listFiles().length > 0) {
                File[] allFiles = file.listFiles();
                Arrays.sort(allFiles, new FileComparator());
                File endFile = allFiles[allFiles.length - 1];
                mLogName = endFile.getName();
                mLogFilePathName = endFile.getAbsolutePath();
                bFlag = true;
            } else {
                bFlag = createNewLogFile();
            }
            //init fileWriter
            try {
                mFileWriter = new FileWriter(mLogFilePathName, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            bFlag = false;
        }
        return bFlag;
    }

    /**
     * Create Log
     *
     * @return status
     */
    private boolean createNewLogFile() {
        mLogName = SDF.format(new Date()) + ".log";
        File file = new File(mFilePath, mLogName);
        try {
            if (file.createNewFile()) {
                mLogFilePathName = file.getAbsolutePath();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * delete OldLogFile
     *
     * @return status
     */
    private boolean deleteOldLogFile() {
        if (mFilePath == null) return false;
        boolean bFlag = false;
        try {
            File file = new File(mFilePath);
            if (file.isDirectory() && file.listFiles() != null) {
                int count = file.listFiles().length - mFileCount;
                if (count > 0) {
                    File[] files = file.listFiles();
                    Arrays.sort(files, new FileComparator());
                    for (int i = 0; i < count; i++) {
                        bFlag = files[i].delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bFlag;
    }

    /**
     * check Log Length
     */
    private void checkLogLength() {
        File file = new File(mLogFilePathName);
        if (file.length() >= mFileSize) {
            createNewLogFile();
            initLogNameSize();
            deleteOldLogFile();
        }
    }


    /**
     * *********************************************************************************************
     * Public methods
     * *********************************************************************************************
     */

    /**
     * appendLogsTo File
     *
     * @param data Log
     */
    private void appendLogs(Log data) {
        if (isDone)
            return;
        if (mFileWriter != null) {
            try {
                mWriteLock.lock();
                mFileWriter.append(data.toString());
                mFileWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
                initLogNameSize();
                deleteOldLogFile();
            } finally {
                mWriteLock.unlock();
            }
            checkLogLength();
        } else {
            initLogNameSize();
            deleteOldLogFile();
        }
    }

    /**
     * register Usb BroadCast
     */
    protected void registerBroadCast(String path) {
        if (path != null && path.length() > 0)
            mExternalStoragePath = path;
        unRegisterBroadCast();

        IntentFilter iFilter = new IntentFilter();
        //iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        //iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        iFilter.setPriority(1000);

        mUsbBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    copyLogFile(mExternalStoragePath);
                }
            }
        };

        GeniusKit.getApplication().registerReceiver(mUsbBroadCastReceiver, iFilter);
    }

    /**
     * unRegister Usb BroadCast
     */
    protected void unRegisterBroadCast() {
        if (mUsbBroadCastReceiver != null) {
            GeniusKit.getApplication().unregisterReceiver(mUsbBroadCastReceiver);
            mUsbBroadCastReceiver = null;
        }
    }

    /**
     * add Log
     *
     * @param data Log
     */
    protected void addLog(Log data) {
        try {
            mQueueLock.lock();
            mLogs.offer(data);
            mQueueNotify.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mQueueLock.unlock();
        }
    }

    /**
     * clearLogFile
     */
    protected boolean clearLogFile() {
        if (mFilePath == null) return false;
        boolean bFlag = false;
        File file = new File(mFilePath);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                bFlag = logFile.delete();
            }
        }
        return bFlag;
    }

    /**
     * Copy log to ExternalStorage
     */
    protected void copyLogFile(String path) {
        if (Environment.getExternalStorageState() == null || !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        String sdFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (path == null ? mExternalStoragePath : path);
        File file = new File(sdFilePath);
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                return;
            }
        }

        file = new File(mFilePath);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                mWriteLock.lock();
                Tools.copyFile(logFile, new File(sdFilePath + File.separator + fileName));
                mWriteLock.unlock();
            }
        }
    }

    /**
     * done
     */
    public void done() {
        isDone = true;

        // notify all
        try {
            mQueueLock.lock();
            mQueueNotify.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mQueueLock.unlock();
        }

        // close fileWriter
        if (mFileWriter != null) {
            try {
                mFileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFileWriter = null;
        }
    }

    /**
     * Thread
     */
    @Override
    public void run() {
        while (!isDone) {
            try {
                while (true) {
                    Log data = mLogs.poll();
                    if (data == null) {
                        try {
                            mQueueLock.lock();
                            // Check again, this time in synchronized
                            data = mLogs.poll();
                            if (data == null) {
                                // Await the log arrive
                                if (!isDone)
                                    mQueueNotify.await();
                                return;
                            }
                        } finally {
                            mQueueLock.unlock();
                        }
                    }
                    appendLogs(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * FileComparator
     */
    class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            String createInfo1 = getFileNameWithoutExtension(file1.getName());
            String createInfo2 = getFileNameWithoutExtension(file2.getName());
            try {
                Date create1 = SDF.parse(createInfo1);
                Date create2 = SDF.parse(createInfo2);
                if (create1.before(create2)) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (ParseException e) {
                return 0;
            }
        }

        /**
         * Remove the file extension type. (log)
         *
         * @param fileName fileName
         * @return Name
         */
        private String getFileNameWithoutExtension(String fileName) {
            return fileName.substring(0, fileName.indexOf("."));
        }
    }
}