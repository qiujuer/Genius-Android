package net.qiujuer.genius.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import net.qiujuer.genius.Genius;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by QiuJu
 * on 2014/9/2.
 */
class LogWriter extends Thread {
    private static long FileSize = 2 * 1024 * 1024;
    private static int FileCount = 10;

    private final Lock WriteLock = new ReentrantLock();
    private final Lock ListLock = new ReentrantLock();
    private final Condition ListNotify = ListLock.newCondition();

    private String filePath = null;
    private String externalStoragePath = "Genius" + File.separator + "Logs";

    private String logName = null;
    private String logPathFileName = null;

    private SimpleDateFormat sdf = null;

    private List<Log> logList = null;
    private FileWriter fileWriter = null;

    private boolean isDone = false;
    private boolean isReceiverUsb = false;

    private BroadcastReceiver mUsbBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                copyLogFile(externalStoragePath);
            }
        }
    };

    /**
     * LogFile
     *
     * @param count file count
     * @param size  file size
     * @param path  save path
     */
    protected LogWriter(int count, float size, String path) {
        logList = new ArrayList<Log>();
        sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

        FileSize = (long) size * 1024 * 1024;
        FileCount = count;
        filePath = path;

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
            File file = new File(filePath);
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
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileWriter = null;
        }
        //init File
        try {
            File file = new File(filePath);
            if (file.listFiles() != null && file.listFiles().length > 0) {
                File[] allFiles = file.listFiles();
                Arrays.sort(allFiles, new FileComparator());
                File endFile = allFiles[allFiles.length - 1];
                logName = endFile.getName();
                logPathFileName = endFile.getAbsolutePath();
                bFlag = true;
            } else {
                bFlag = createNewLogFile();
            }
            //init fileWriter
            try {
                fileWriter = new FileWriter(logPathFileName, true);
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
        logName = sdf.format(new Date()) + ".log";
        File file = new File(filePath, logName);
        try {
            if (file.createNewFile()) {
                logPathFileName = file.getAbsolutePath();
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
        if (filePath == null) return false;
        boolean bFlag = false;
        try {
            File file = new File(filePath);
            if (file.isDirectory() && file.listFiles() != null) {
                int count = file.listFiles().length - FileCount;
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
        File file = new File(logPathFileName);
        if (file.length() >= FileSize) {
            createNewLogFile();
            initLogNameSize();
            deleteOldLogFile();
        }
    }

    /**
     * appendLogsTo File
     *
     * @param data Log
     */
    private void appendLogs(Log data) {
        if (fileWriter != null) {
            try {
                WriteLock.lock();
                fileWriter.append(data.toString());
                fileWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
                initLogNameSize();
                deleteOldLogFile();
            } finally {
                WriteLock.unlock();
            }
            checkLogLength();
        } else {
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
     * register Usb BroadCast
     */
    protected void registerBroadCast(String path) {
        if (path != null && path.length() > 0)
            externalStoragePath = path;
        unRegisterBroadCast();

        IntentFilter iFilter = new IntentFilter();
        //iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        //iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        iFilter.setPriority(1000);
        Genius.getApplication().registerReceiver(mUsbBroadCastReceiver, iFilter);
        isReceiverUsb = true;
    }

    /**
     * unRegister Usb BroadCast
     */
    protected void unRegisterBroadCast() {
        if (isReceiverUsb) {
            Genius.getApplication().unregisterReceiver(mUsbBroadCastReceiver);
        }
    }

    /**
     * add Log
     *
     * @param data Log
     */
    protected void addLog(Log data) {
        ListLock.lock();
        logList.add(data);
        try {
            ListNotify.signalAll();
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        }
        ListLock.unlock();
    }

    /**
     * clearLogFile
     */
    protected boolean clearLogFile() {
        if (filePath == null) return false;
        boolean bFlag = false;
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                bFlag = logFile.delete();
            }
        }
        return bFlag;
    }

    /**
     * get log file path
     */
    protected static String getDefaultLogPath() {
        return Genius.getApplication().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "Genius" + File.separator + "Logs";
    }

    /**
     * Copy log to ExternalStorage
     */
    protected void copyLogFile(String path) {
        if (Environment.getExternalStorageState() == null || !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        String sdFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (path == null ? externalStoragePath : path);
        File file = new File(sdFilePath);
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                return;
            }
        }

        file = new File(filePath);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                WriteLock.lock();
                ToolUtils.copyFile(logFile, new File(sdFilePath + File.separator + fileName));
                WriteLock.unlock();
            }
        }
    }

    /**
     * done
     */
    public void done() {
        isDone = true;
    }

    /**
     * Thread
     */
    @Override
    public void run() {
        while (!isDone) {
            ListLock.lock();
            for (Log data : logList) {
                appendLogs(data);
            }
            logList.clear();
            try {
                ListNotify.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ListLock.unlock();
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
                Date create1 = sdf.parse(createInfo1);
                Date create2 = sdf.parse(createInfo2);
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