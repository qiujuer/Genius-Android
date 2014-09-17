package net.qiujuer.genius.command;

import net.qiujuer.genius.util.ToolUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by QiuJu
 * on 2014/9/17.
 */
class CommandExecutor {
    private static final String TAG = CommandExecutor.class.getName();

    private static final String BREAK_LINE;
    private static final byte[] BUFFER;
    private static final int BUFFER_LENGTH;

    private static final Lock LOCK = new ReentrantLock();
    //Time Out
    private static final long TIMEOUT = 90000;
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
     * init
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
     * *********************************************************************************************
     * private methods
     * *********************************************************************************************
     */
    /**
     * Get CommandExecutor
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
     * read
     */
    private void read() {
        String str;
        //read In
        try {
            while ((str = bInReader.readLine()) != null) {
                sbReader.append(str);
                sbReader.append(BREAK_LINE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * run thread
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
                while (true) {
                    len = in.read(BUFFER);
                    if (len <= 0)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
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
     * close
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
     * *********************************************************************************************
     * protected methods
     * *********************************************************************************************
     */
    /**
     * Run
     *
     * @param param param eg: "/system/bin/ping -c 4 -s 100 www.qiujuer.net"
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
     * Get is Time Out
     *
     * @return Time Out
     */
    protected boolean isTimeOut() {
        return ((System.currentTimeMillis() - startTime) >= TIMEOUT);
    }

    /**
     * Get Result
     *
     * @return Result
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
     * destroy
     */
    protected void destroy() {
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