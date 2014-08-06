import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Create By Qiujuer
 * 2014-08-05
 * <p/>
 * 执行命令行语句进程管理封装
 */
public class ProcessModel {
    private static final String TAG = "ProcessModel";
    private static final int MAX_READ_ERROR = 10;
    //换行符
    private static final String BREAK_LINE;
    //错误缓冲
    private static final byte[] BUFFER;
    //缓冲区大小
    private static final int BUFFER_LENGTH;
    //创建进程时需要互斥进行
    private static final Lock LOCK = new ReentrantLock();
    //ProcessBuilder
    private static final ProcessBuilder PRC;

    final private Process process;
    final private InputStream in;
    final private InputStream err;
    final private OutputStream out;
    final private StringBuilder sbReader;

    private BufferedReader bInReader = null;
    private InputStreamReader isInReader = null;
    private boolean isDone;
    private boolean isDestroy;
    private int errorCount;


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
     * 实例化一个ProcessModel
     *
     * @param process Process
     */
    private ProcessModel(Process process) {
        //init
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
        readThread();
    }

    /**
     * 执行命令
     *
     * @param params 命令参数 eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"
     */
    public static ProcessModel create(String... params) {
        Process process = null;
        try {
            LOCK.lock();
            process = PRC.command(params)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //sleep 100
            StaticFunction.sleepIgnoreInterrupt(100);
            LOCK.unlock();
        }
        if (process == null)
            return null;
        return new ProcessModel(process);
    }

    /**
     * 通过Android底层实现进程关闭
     *
     * @param process 进程
     */
    public static void kill(Process process) {
        int pid = getProcessId(process);
        if (pid != 0) {
            try {
                android.os.Process.killProcess(pid);
            } catch (Exception e) {
                try {
                    process.destroy();
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取进程的ID
     *
     * @param process 进程
     * @return id
     */
    public static int getProcessId(Process process) {
        String str = process.toString();
        try {
            int i = str.indexOf("=") + 1;
            int j = str.indexOf("]");
            str = str.substring(i, j);
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
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
            if (err != null && err.length() > 0)
                Logs.e(TAG, "Read Exception:" + err);
            errorCount++;
        }
    }

    /**
     * 启动线程进行异步读取结果
     */
    private void readThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //while to end
                while (true) {
                    try {
                        process.exitValue();
                        //read last
                        read();
                        break;
                    } catch (IllegalThreadStateException e) {
                        read();
                        if (isDestroy && errorCount > MAX_READ_ERROR){
                            break;
                        }
                    }
                    StaticFunction.sleepIgnoreInterrupt(300);
                }

                //read end
                int len;
                if (in != null) {
                    try {
                        while ((len = in.read(BUFFER)) > 0) {
                            Logs.d(TAG, "Read End:" + len);
                        }
                    } catch (IOException e) {
                        String err = e.getMessage();
                        if (err != null && err.length() > 0)
                            Logs.e(TAG, "Read Thread IOException:" + err);
                    }
                }

                //close
                close();

                //done
                isDone = true;
            }
        });

        thread.setName("DroidTestAgent.Test.TestModel.ProcessModel:ReadThread");
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * 获取执行结果
     *
     * @return 结果
     */
    public String getResult() {
        //waite process setValue
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            String err = e.getMessage();
            if (err != null && err.length() > 0)
                Logs.e(TAG, "GetResult WaitFor InterruptedException:" + err);
        }

        //until startRead en
        while (true) {
            if (isDone)
                break;
            StaticFunction.sleepIgnoreInterrupt(100);
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
    public void destroy() {
        //process
        try {
            process.destroy();
        } catch (Exception e) {
            kill(process);
        }
        isDestroy = true;
    }
}
