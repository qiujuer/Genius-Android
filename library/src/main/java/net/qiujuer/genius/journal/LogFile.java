package net.qiujuer.genius.journal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
 * Created by Genius on 2014/8/16.
 * 日志写入文件类
 * 默认写入到内存中，插入SD卡时拷贝到SD卡中
 */
class LogFile extends Thread {
    //日志文件列表最低达到5条时才批量写入文件一次，减少文件操作
    private final static int MIN_LOG_SIZE = 5;
    //每个日志文件：2M
    private static long FileSize = 2 * 1024 * 1024;
    //最多存储10个
    private static int FileCount = 10;
    //锁
    private final Lock WriteLock = new ReentrantLock();
    private final Lock ListLock = new ReentrantLock();
    //锁通讯
    private final Condition ListNotify = ListLock.newCondition();
    //日志存储地址
    private String filePath = null;
    //外部存储路径，用于拷贝日志到指定文件夹
    private String externalStoragePath = "Genius" + File.separator + "Logs";
    //日志文件名
    private String logName = null;
    //日志地址与文件名（全路径）
    private String logPathFileName = null;
    //时间格式化使用，用于生成文件名
    private SimpleDateFormat sdf = null;
    //等待写入文件的日志列表
    private List<LogData> logDataList = null;
    //文件操作类
    private FileWriter fileWriter = null;
    //是否结束写入线程
    private boolean isDone = false;
    /**
     * 广播处理卸载SD
     */
    private BroadcastReceiver mUsbBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                copyLogFile();
            }
        }
    };

    /**
     * 实例化 LogFile
     *
     * @param count 日志数量
     * @param size  日志大小
     * @param path  日志存储路径
     */
    protected LogFile(int count, float size, String path) {
        logDataList = new ArrayList<LogData>();
        sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

        FileSize = (long) size * 1024 * 1024;
        FileCount = count;
        filePath = path;

        init();

        this.setName(LogFile.class.getName());
        this.setDaemon(true);
        this.start();
    }

    /**
     * 退出线程标志
     */
    public void done() {
        isDone = true;
    }

    /**
     * 尝试销毁类时调用
     */
    public void finalize() {
        isDone = true;
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 一键初始化
     *
     * @return 是否初始化成功
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
     * 初始化日志文件夹地址
     *
     * @return 是否成功
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
     * 初始化日志名称和大小以及地址
     *
     * @return 是否成功
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
     * 创建一个日志文件
     *
     * @return 是否成功
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
     * 删除过期日志
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
     * 检测长度是否超出文件大小
     */
    private void checkLength() {
        File file = new File(logPathFileName);
        if (file.length() >= FileSize) {
            createNewLogFile();
            initLogNameSize();
            deleteOldLogFile();
        }
    }


    /**
     * 异步追加一条日志
     *
     * @param data 日志
     */
    protected void addLog(LogData data) {
        ListLock.lock();
        logDataList.add(data);
        //最低3条写入一次
        if (logDataList.size() > MIN_LOG_SIZE) {
            try {
                ListNotify.signalAll();
            } catch (IllegalMonitorStateException e) {
                e.printStackTrace();
            }
        }
        ListLock.unlock();
    }

    /**
     * 追加一条日志
     *
     * @param data 日志
     */
    private void appendLogs(LogData data) {
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
            checkLength();
        } else {
            initLogNameSize();
            deleteOldLogFile();
        }
    }

    /**
     * 异步线程
     */
    @Override
    public void run() {
        while (!isDone) {
            ListLock.lock();
            for (LogData data : logDataList) {
                appendLogs(data);
            }
            logDataList.clear();
            try {
                ListNotify.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ListLock.unlock();
        }
    }


    /**
     * 注册SD卡变化广播
     */
    protected boolean registerBroadCast(Context context, String path) {
        if (path != null && path.length() > 0)
            externalStoragePath = path;
        try {
            IntentFilter iFilter = new IntentFilter();
            //iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            //iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            iFilter.addDataScheme("file");
            iFilter.setPriority(1000);
            context.registerReceiver(mUsbBroadCastReceiver, iFilter);
            return true;
        } catch (ReceiverCallNotAllowedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 卸载SD卡广播
     */
    protected void unRegisterBroadCast(Context context) {
        try {
            context.unregisterReceiver(mUsbBroadCastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空日志
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
     * 将日志文件拷贝到SD卡下面
     */
    protected void copyLogFile() {
        if (Environment.getExternalStorageState() == null || !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        String sdFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + externalStoragePath;
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
                copy(logFile, new File(sdFilePath + File.separator + fileName));
                WriteLock.unlock();
            }
        }
    }

    /**
     * 拷贝文件
     *
     * @param source 文件源
     * @param target 存储目标
     * @return 是否成功
     */
    private boolean copy(File source, File target) {
        boolean bFlag = false;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (!target.exists()) {
                boolean createSuccess = target.createNewFile();
                if (!createSuccess) {
                    return false;
                }
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buffer = new byte[8 * 1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            bFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
            bFlag = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bFlag;
    }

    /**
     * 排序所用类
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
         * 去除文件的扩展类型（.log）
         *
         * @param fileName 文件名
         * @return 去除后缀后的名称
         */
        private String getFileNameWithoutExtension(String fileName) {
            return fileName.substring(0, fileName.indexOf("."));
        }
    }
}