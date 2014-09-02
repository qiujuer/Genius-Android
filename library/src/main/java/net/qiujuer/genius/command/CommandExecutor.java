package net.qiujuer.genius.command;

import net.qiujuer.genius.util.GLog;
import net.qiujuer.genius.util.ToolUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Genius on 2014/8/13.
 * 命令行执行命令
 */
class CommandExecutor {
    private static final String TAG = CommandExecutor.class.getName();
    //换行符
    private static final String BREAK_LINE;
    //错误缓冲
    private static final byte[] BUFFER;
    //缓冲区大小
    private static final int BUFFER_LENGTH;
    //创建进程时需要互斥进行
    private static final Lock LOCK = new ReentrantLock();
    //不能超过1分钟
    private static final long TIMEOUT = 60000;
    //ProcessBuilder
    private static ProcessBuilder PRC;

    final private Process process;
    final private InputStream in;
    final private InputStream err;
    final private OutputStream out;
    final private StringBuilder sbReader;

    private BufferedReader bInReader = null;
    private InputStreamReader isInReader = null;
    private boolean isDone;
    private long startTime;

    /**
     * 静态变量初始化
     */
    static {
        BREAK_LINE = "\n";
        BUFFER_LENGTH = 128;
        BUFFER = new byte[BUFFER_LENGTH];

        LOCK.lock();
        PRC = new ProcessBuilder();
        LOCK.unlock();
    }


    /**
     * 实例化一个CommandExecutor
     *
     * @param process Process
     */
    private CommandExecutor(Process process) {
        //init
        this.startTime = System.currentTimeMillis();
        this.process = process;
        //get
        out = process.getOutputStream();
        in = process.getInputStream();
        err = process.getErrorStream();

        //in
        if (in != null) {
            isInReader = new InputStreamReader(in);
            bInReader = new BufferedReader(isInReader, BUFFER_LENGTH);
        }

        sbReader = new StringBuilder();

        //start read thread
        Thread processThread = new Thread(TAG) {
            @Override
            public void run() {
                startRead();
            }
        };
        processThread.setDaemon(true);
        processThread.start();
    }

    /**
     * 执行命令
     *
     * @param param 命令参数 eg: "/system/bin/ping -c 4 -s 100 www.qiujuer.net"
     */
    protected static CommandExecutor create(String param) {
        String[] params = param.split(" ");
        CommandExecutor processModel = null;
        try {
            LOCK.lock();
            Process process = PRC.command(params)
                    .redirectErrorStream(true)
                    .start();
            processModel = new CommandExecutor(process);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //sleep 100
            ToolUtils.sleepIgnoreInterrupt(100);
            LOCK.unlock();
        }
        return processModel;
    }


    /**
     * 获取是否超时
     *
     * @return 是否超时
     */
    protected boolean isTimeOut() {
        return ((System.currentTimeMillis() - startTime) >= TIMEOUT);
    }

    //读取结果
    private void read() {
        String str;
        //read In
        try {
            while ((str = bInReader.readLine()) != null) {
                sbReader.append(str);
                sbReader.append(BREAK_LINE);
            }
        } catch (Exception e) {
            String err = e.getMessage();
            if (err != null && err.length() > 0) {
                GLog.e(TAG, "Read Exception:" + err);
            }
        }
    }

    /**
     * 启动线程进行异步读取结果
     */
    private void startRead() {
        //while to end
        while (true) {
            try {
                process.exitValue();
                //read last
                read();
                break;
            } catch (IllegalThreadStateException e) {
                read();
            }
            ToolUtils.sleepIgnoreInterrupt(50);
        }

        //read end
        int len;
        if (in != null) {
            try {
                while ((len = in.read(BUFFER)) > 0) {
                    GLog.d(TAG, "Read End:" + len);
                }
            } catch (IOException e) {
                String err = e.getMessage();
                if (err != null && err.length() > 0)
                    GLog.e(TAG, "Read Thread IOException:" + err);
            }
        }

        //close
        close();
        //destroy
        destroy();
        //done
        isDone = true;

    }

    /**
     * 获取执行结果
     *
     * @return 结果
     */
    protected String getResult() {
        //until startRead en
        while (!isDone) {
            ToolUtils.sleepIgnoreInterrupt(200);
        }

        //return
        if (sbReader.length() == 0)
            return null;
        else
            return sbReader.toString();
    }

    /**
     * 关闭所有流
     */
    private void close() {
        //close out
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //err
        if (err != null) {
            try {
                err.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //in
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isInReader != null) {
            try {
                isInReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bInReader != null) {
            try {
                bInReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 销毁
     */
    private void destroy() {
        String str = process.toString();
        try {
            int i = str.indexOf("=") + 1;
            int j = str.indexOf("]");
            str = str.substring(i, j);
            int pid = Integer.parseInt(str);
            try {
                android.os.Process.killProcess(pid);
            } catch (Exception e) {
                try {
                    process.destroy();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}