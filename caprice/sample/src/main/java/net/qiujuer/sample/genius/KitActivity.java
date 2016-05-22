package net.qiujuer.sample.genius;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.qiujuer.genius.kit.cmd.Cmd;
import net.qiujuer.genius.kit.cmd.Command;
import net.qiujuer.genius.kit.cmd.DnsResolve;
import net.qiujuer.genius.kit.cmd.Ping;
import net.qiujuer.genius.kit.cmd.Telnet;
import net.qiujuer.genius.kit.cmd.TraceRoute;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.kit.handler.runable.Func;
import net.qiujuer.genius.ui.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class KitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = KitActivity.class.getSimpleName();
    private TextView mText = null;
    private Button mAsync;
    private Button mSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        // init bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mAsync = (Button) findViewById(R.id.btn_async);
        mSync = (Button) findViewById(R.id.btn_sync);
        mText = (TextView) findViewById(R.id.text);

        mAsync.setOnClickListener(this);
        mSync.setOnClickListener(this);

        // Start
        init();
        testToolKit();
        testCommand();
        testNetTool();
    }

    private void init() {
        //Application kitApp = Kit.getApplication();
        Application application = getApplication();
        //showLog(TAG, "Kit.getApplication() eq getApplication() is:" + (kitApp == application));
        Cmd.init(application);
    }

    private void showLog(String tag, final String msg) {
        // call ui thread to show
        // 切换到主线程更新
        String ret = Run.onUiSync(new Func<String>() {
            @Override
            public String call() {
                if (mText != null)
                    mText.append("\n" + msg);
                return "LOG " + msg;
            }
        });

        Log.d(tag, ret);
    }

    private static void sleepIgnoreInterrupt(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sleepIgnoreInterrupt(int time, int n) {
        try {
            Thread.sleep(time, n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mText = null;
        mRunAsyncThread = false;
        mRunSyncThread = false;
        // Dispose when you don't use
        Command.dispose();
        Run.dispose();
    }

    /**
     * Test Tool
     */
    private void testToolKit() {
        // Synchronous mode in the main thread when operating the child thread will enter the waiting,
        // until the main thread processing is completed
        // 同步模式在主线程操作时子线程将进入等待，直到主线程处理完成

        // Asynchronous mode the child thread parallel operation with the main thread, don't depend on each other
        // 异步模式子线程与主线程并行操作，不相互依赖等待
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "ToolKit:";
                long start = System.currentTimeMillis();

                // Test synchronization mode,
                // in this mode method first to execute commands on the queue, waiting for the main thread
                // 测试同步模式，在该模式下
                // 该方法首先会将要执行的命令放到队列中，等待主线程执行
                Run.onUiSync(new Action() {
                    @Override
                    public void call() {
                        sleepIgnoreInterrupt(20);
                    }
                });
                msg += "Sync Time:" + (System.currentTimeMillis() - start) + ", ";


                start = System.currentTimeMillis();

                // Test synchronization func return mode
                // in this mode method first to execute commands on the queue, waiting for the main thread
                // 测试同步返回模式，在该模式下
                // 该方法首先会将要执行的命令放到队列中，等待主线程执行, 在执行后会返回执行后的结果
                long time = Run.onUiSync(new Func<Long>() {
                    @Override
                    public Long call() {
                        sleepIgnoreInterrupt(20);
                        return System.currentTimeMillis();
                    }
                });

                msg += "Sync Func Time:" + (time - start) + ", ";


                start = System.currentTimeMillis();

                // Test asynchronous mode,
                // in this mode the child thread calls the method added to the queue, can continue to go down, will not be blocked
                // 测试异步模式，在该模式下
                // 子线程调用该方法加入到队列后，可继续往下走，并不会阻塞
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        sleepIgnoreInterrupt(20);
                    }
                });
                msg += "Async Time:" + (System.currentTimeMillis() - start) + " ";
                showLog(TAG, msg);
            }
        });
        thread.start();
    }

    /**
     * Command
     * 测试命令行执行
     */
    private void testCommand() {
        Thread thread = new Thread() {
            public void run() {
                // The same way call way and the ProcessBuilder mass participation
                // 调用方式与ProcessBuilder传参方式一样
                Command command = new Command(Command.TIMEOUT, "/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");

                String res = null;
                try {
                    res = Command.command(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showLog(TAG, "\n\nCommand Sync: " + res);

                command = new Command("/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");

                // callback by listener
                // 回调方式
                try {
                    Command.command(command, new Command.CommandListener() {
                        @Override
                        public void onCompleted(String str) {
                            showLog(TAG, "\n\nCommand Async onCompleted: \n" + str);
                        }

                        @Override
                        public void onCancel() {
                            Log.i(TAG, "\n\nCommand Async onCancel");
                        }

                        @Override
                        public void onError(Exception e) {
                            showLog(TAG, "\n\nCommand Async onError:" + (e != null ? e.toString() : "null"));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * NetTool
     * 基本网络功能测试
     */
    public void testNetTool() {
        Thread thread = new Thread() {
            public void run() {
                // Packets， Packet size，The target，Whether parsing IP
                // 包数，包大小，目标，是否解析IP
                Ping ping = new Ping(4, 32, "www.baidu.com", true);
                ping.start();
                showLog(TAG, "Ping: " + ping.toString());
                // target
                // 目标，可指定解析服务器
                DnsResolve dns = null;
                try {
                    // Add DNS service
                    // 添加DNS服务器
                    dns = new DnsResolve("www.baidu.com", InetAddress.getByName("202.96.128.166"));
                    dns.start();
                    showLog(TAG, "DnsResolve: " + dns.toString());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                // target port
                // 目标，端口
                Telnet telnet = new Telnet("www.baidu.com", 80);
                telnet.start();
                showLog(TAG, "Telnet: " + telnet.toString());
                // target
                // 目标
                TraceRoute traceRoute = new TraceRoute("www.baidu.com");
                traceRoute.start();
                showLog(TAG, "\n\nTraceRoute: " + traceRoute.toString());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private boolean mRunAsyncThread = false;
    private boolean mRunSyncThread = false;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_async) {
            if (mRunAsyncThread) {
                mRunAsyncThread = false;
                return;
            }

            mRunAsyncThread = true;
            Thread thread = new Thread("ASYNC-ADD-THREAD") {
                long count = 0;

                @Override
                public void run() {
                    super.run();

                    while (mRunAsyncThread && count < 10000) {
                        add();
                        sleepIgnoreInterrupt(0, 500);
                    }
                }

                private void add() {
                    count++;
                    final long cur = count;
                    Run.onUiAsync(new Action() {
                        @Override
                        public void call() {
                            mAsync.setText(cur + "/" + getCount());
                        }
                    });
                }

                public long getCount() {
                    return count;
                }
            };
            thread.start();
        } else if (v.getId() == R.id.btn_sync) {
            if (mRunSyncThread) {
                mRunSyncThread = false;
                return;
            }

            mRunSyncThread = true;

            Thread thread = new Thread("SYNC-ADD-THREAD") {
                long count = 0;

                @Override
                public void run() {
                    super.run();

                    while (mRunSyncThread && count < 10000) {
                        add();
                        sleepIgnoreInterrupt(0, 500);
                    }
                }

                private void add() {
                    count++;
                    final long cur = count;
                    Run.onUiSync(new Action() {
                        @Override
                        public void call() {
                            mSync.setText(cur + "/" + getCount());
                        }
                    });
                }

                public long getCount() {
                    return count;
                }
            };
            thread.start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
