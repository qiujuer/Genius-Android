/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/25/2014
 * Changed 04/17/2016
 * Version 2.0.0
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
package net.qiujuer.genius.kit.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Command line run executor
 * Executed command line and return the final result
 */
class CommandExecutor implements Runnable {
    // Threads
    private static final ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() > 0 ?
                    Runtime.getRuntime().availableProcessors() : 1);
    // TAG
    private static final String TAG = CommandExecutor.class.getSimpleName();
    // Final
    private static final String BREAK_LINE = "\n";
    private static final int BUFFER_LENGTH = 128;
    private static final byte[] BUFFER = new byte[BUFFER_LENGTH];
    // ProcessBuilder
    private static final ProcessBuilder PRC = new ProcessBuilder();

    // Class value
    private final Process mProcess;
    private final int mTimeout;
    private final long mStartTime;

    // Result
    private final StringBuilder mResult;

    // Stream
    private InputStream mInStream;
    private InputStream mErrStream;
    private OutputStream mOutStream;
    private BufferedReader mInStreamBuffer = null;

    // Is end
    private boolean isDone;


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
    private CommandExecutor(Process process, int timeout) throws IOException {
        // Cmd
        this.mTimeout = timeout;
        this.mStartTime = System.currentTimeMillis();
        this.mProcess = process;
        // Get
        mOutStream = process.getOutputStream();
        mInStream = process.getInputStream();
        mErrStream = process.getErrorStream();

        // In
        if (mInStream != null) {
            mInStreamBuffer = new BufferedReader(
                    new InputStreamReader(mInStream),
                    BUFFER_LENGTH);
        }

        if (mInStream != null) {
            // Start read thread
            mResult = new StringBuilder();
            EXECUTORSERVICE.execute(this);
        } else {
            // if init error we can close the stream
            close();
            destroy();
            throw new IOException("The Process.getInputStream() is null.");
        }
    }

    /**
     * Run
     *
     * @param param param eg: "/system/bin/ping -c 4 -s 100 www.qiujuer.net"
     */
    protected static CommandExecutor create(int timeout, String param) {
        String[] params = param.split(" ");
        CommandExecutor processModel = null;
        synchronized (PRC) {
            try {
                Process process = PRC.command(params)
                        .redirectErrorStream(true)
                        .start();
                processModel = new CommandExecutor(process, timeout);
                // Sleep 10 to create next
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return processModel;
    }

    /**
     * Read
     */
    private void read() {
        String str;
        // Read data
        try {
            while ((str = mInStreamBuffer.readLine()) != null) {
                mResult.append(str);
                mResult.append(BREAK_LINE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * *********************************************************************************************
     * protected methods
     * *********************************************************************************************
     */

    /**
     * Close
     */
    private void close() {
        // Out
        Cmd.closeIO(mOutStream);
        mOutStream = null;
        // Err
        Cmd.closeIO(mErrStream);
        mErrStream = null;
        // In
        Cmd.closeIO(mInStreamBuffer);
        mInStreamBuffer = null;
        Cmd.closeIO(mInStream);
        mInStream = null;
    }

    /**
     * Get is Time Out
     *
     * @return Time Out
     */
    protected boolean isTimeOut() {
        return ((System.currentTimeMillis() - mStartTime) >= mTimeout);
    }

    /**
     * Get Result
     *
     * @return Result
     */
    protected String getResult() {
        // Until read end
        while (!isDone) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Get return value
        if (mResult.length() == 0)
            return null;
        else
            return mResult.toString();
    }

    /**
     * Destroy
     */
    protected void destroy() {
        String str = mProcess.toString();
        try {
            int i = str.indexOf("=") + 1;
            int j = str.indexOf("]");
            str = str.substring(i, j);
            int pid = Integer.parseInt(str);
            try {
                android.os.Process.killProcess(pid);
            } catch (Exception e) {
                try {
                    mProcess.destroy();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // While to end
        while (true) {
            try {
                mProcess.exitValue();
                //read last
                read();
                break;
            } catch (IllegalThreadStateException e) {
                read();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Read end
        int len;
        if (mInStream != null) {
            try {
                while (true) {
                    len = mInStream.read(BUFFER);
                    if (len <= 0)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close destroy and done the read
        close();
        destroy();

        isDone = true;

    }
}