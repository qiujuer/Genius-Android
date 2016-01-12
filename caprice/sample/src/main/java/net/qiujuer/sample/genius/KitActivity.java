package net.qiujuer.sample.genius;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.qiujuer.genius.kit.Kit;
import net.qiujuer.genius.kit.command.Command;
import net.qiujuer.genius.kit.net.DnsResolve;
import net.qiujuer.genius.kit.net.Ping;
import net.qiujuer.genius.kit.net.Telnet;
import net.qiujuer.genius.kit.net.TraceRoute;
import net.qiujuer.genius.kit.util.FixedList;
import net.qiujuer.genius.kit.util.HashKit;
import net.qiujuer.genius.kit.util.Log;
import net.qiujuer.genius.kit.util.Tools;
import net.qiujuer.genius.kit.util.UiKit;
import net.qiujuer.genius.ui.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class KitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = KitActivity.class.getSimpleName();
    private TextView mText = null;
    private Button mAsync;
    private Button mSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);


        // Init to use
        // 初始化使用
        Kit.initialize(getApplication());

        mAsync = (Button) findViewById(R.id.btn_async);
        mSync = (Button) findViewById(R.id.btn_sync);
        mText = (TextView) findViewById(R.id.text);

        mAsync.setOnClickListener(this);
        mSync.setOnClickListener(this);

        // Add callback
        // 添加回调显示
        Log.addCallbackListener(new Log.LogCallbackListener() {
            @Override
            public void onLogArrived(final Log data) {
                try {
                    // Async to show
                    // 异步显示到界面
                    UiKit.runOnMainThreadAsync(new Runnable() {
                        @Override
                        public void run() {
                            if (mText != null)
                                mText.append("\n" + data.getMsg());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Start
        testLog();
        testTool();
        testToolKit();
        testHashUtils();
        testFixedList();
        testNetTool();
        testCommand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mText = null;
        mRunAsyncThread = false;
        mRunSyncThread = false;
        // Dispose when you don't use
        Kit.dispose();
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
                UiKit.runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        Tools.sleepIgnoreInterrupt(20);
                    }
                });
                msg += "Sync Time:" + (System.currentTimeMillis() - start) + ", ";

                start = System.currentTimeMillis();

                // Test asynchronous mode,
                // in this mode the child thread calls the method added to the queue, can continue to go down, will not be blocked
                // 测试异步模式，在该模式下
                // 子线程调用该方法加入到队列后，可继续往下走，并不会阻塞
                UiKit.runOnMainThreadAsync(new Runnable() {
                    @Override
                    public void run() {
                        Tools.sleepIgnoreInterrupt(20);
                    }
                });
                msg += "Async Time:" + (System.currentTimeMillis() - start) + " ";
                Log.v(TAG, msg);
            }
        });
        thread.start();
    }

    private void testTool() {
        Log.i(TAG, "ToolKit: getAndroidId: " + Tools.getAndroidId(Kit.getApplication()));
        Log.i(TAG, "ToolKit: getSerialNumber: " + Tools.getSerialNumber());
    }

    /**
     * Log
     */
    private void testLog() {
        // Whether to call system Android Log, release can be set to false
        // 是否调用系统Android Log，发布时可设置为false
        Log.setCallLog(true);

        // Clear the storage file
        // 清理存储的文件
        Log.clearLogFile();

        // Whether open is written to the file, stored maximum number of files, a single file size (Mb)
        // 是否开启写入文件，存储最大文件数量，单个文件大小（Mb）
        Log.setSaveLog(true, 10, 1);

        // Set whether to monitor external storage inserts
        // 设置是否监听外部存储插入操作
        // Open when inserting an external device (SD) to store the log file copy to external storage devices
        // 开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
        // This operation depends on whether written to the file open function, the method is invalid when not open
        // 此操作依赖于是否开启写入文件功能，未开启则此方法无效
        // Parameters: whether open, SD card catalog
        // 参数: 是否开启，SD卡目录
        Log.setCopyExternalStorage(true, "Test/Logs");

        // Set show log level
        // 设置显示日志等级
        // VERBOSE: 5  ERROR:1, decline in turn
        // VERBOSE为5到ERROR为1依次递减
        Log.setLevel(Log.ALL);

        Log.v(TAG, "LOG VERBOSE Level");
        Log.d(TAG, "LOG DEBUG Level");
        Log.i(TAG, "LOG INFO Level");
        Log.w(TAG, "LOG WARN Level");
        Log.e(TAG, "LOG ERROR Level");

        Log.setLevel(Log.INFO);
        Log.v(TAG, "LOG VERBOSE Level");
        Log.d(TAG, "LOG DEBUG Level");
        Log.i(TAG, "LOG INFO Level");
        Log.w(TAG, "LOG WARN Level");
        Log.e(TAG, "LOG ERROR Level");

        Log.setLevel(Log.ALL);
    }

    /**
     * MD5
     */
    private void testHashUtils() {
        Log.i(TAG, "HashUtils: QIUJUER's MD5: " + HashKit.getMD5String("QIUJUER"));
        //HashUtils.getMD5String(new File("FilePath"));
    }

    /**
     * FixedList
     * 固定长度队列
     */
    private void testFixedList() {
        // Init max length 5
        // 初始化最大长度为5
        FixedList<Integer> list = new FixedList<Integer>(5);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        // Add
        // 添加4个元素
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());

        // 继续追加2个
        list.add(5);
        list.add(6);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        // Changed MaxSize
        // 调整最大长度
        list.setMaxSize(6);
        list.add(7);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        list.add(8);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        // Narrow length, automatically deletes the spare parts
        // 缩小长度，自动删除前面多余部分
        list.setMaxSize(3);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        list.add(9);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        // Add a list, automatically remove unwanted parts
        // 添加一个列表进去，自动删除多余部分
        List<Integer> addList = new ArrayList<Integer>();
        addList.add(10);
        addList.add(11);
        addList.add(12);
        addList.add(13);
        list.addAll(addList);
        Log.i(TAG, "FixedList:AddList:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());
        // Using poll pop-up elements
        // 采用poll方式弹出元素
        Log.i(TAG, "FixedList:Poll:[" + list.poll() + "] " + list.size() + " ," + list.getMaxSize());
        Log.i(TAG, "FixedList:Poll:[" + list.poll() + "] " + list.size() + " ," + list.getMaxSize());
        Log.i(TAG, "FixedList:Poll:[" + list.poll() + "] " + list.size() + " ," + list.getMaxSize());
        // Add to end insert
        // 末尾插入元素与add一样
        list.addLast(14);
        list.addLast(15);
        list.addLast(16);
        list.addLast(17);
        list.addLast(18);
        Log.i(TAG, "FixedList:AddLast:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());
        // From the head insert, delete the default tail beyond part
        // 从头部插入，默认删除尾部超出部分
        list.addFirst(19);
        list.addFirst(20);
        Log.i(TAG, "FixedList:AddFirst:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());

        // Clear
        // 清空操作
        list.clear();
        Log.i(TAG, "FixedList:Clear:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());

        // List
        // 使用List操作,最大长度2
        List<Integer> list1 = new FixedList<Integer>(2);
        list1.add(1);
        list1.add(2);
        Log.i(TAG, "FixedList:List:" + " " + list1.size() + " ," + list1.toString());
        list1.add(3);
        Log.i(TAG, "FixedList:List:" + " " + list1.size() + " ," + list1.toString());
        list1.add(4);
        Log.i(TAG, "FixedList:List:" + " " + list1.size() + " ," + list1.toString());
        list1.clear();
        Log.i(TAG, "FixedList:List:" + " " + list1.size() + " ," + list1.toString());
    }

    /**
     * Command
     * 测试命令行执行
     */
    private void testCommand() {
        // Sync
        // 同步
        Thread thread = new Thread() {
            public void run() {
                // The same way call way and the ProcessBuilder mass participation
                // 调用方式与ProcessBuilder传参方式一样
                Command command = new Command(Command.TIMEOUT, "/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");

                String res = Command.command(command);
                Log.i(TAG, "\n\nCommand Sync: " + res);
            }
        };
        thread.setDaemon(true);
        thread.start();

        // Async
        // 异步
        Command command = new Command("/system/bin/ping",
                "-c", "4", "-s", "100",
                "www.baidu.com");

        // Asynchronous execution using callback methods,
        // do not need to build a thread
        // callback by listener
        // 异步方式执行
        // 采用回调方式，无需自己建立线程
        // 传入回调后自动采用此种方式
        Command.command(command, new Command.CommandListener() {
            @Override
            public void onCompleted(String str) {
                Log.i(TAG, "\n\nCommand Async onCompleted: \n" + str);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "\n\nCommand Async onCancel");
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "\n\nCommand Async onError:" + (e != null ? e.toString() : "null"));
            }
        });
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
                Log.i(TAG, "Ping: " + ping.toString());
                // target
                // 目标，可指定解析服务器
                DnsResolve dns = null;
                try {
                    // Add DNS service
                    // 添加DNS服务器
                    dns = new DnsResolve("www.baidu.com", InetAddress.getByName("202.96.128.166"));
                    dns.start();
                    Log.i(TAG, "DnsResolve: " + dns.toString());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                // target port
                // 目标，端口
                Telnet telnet = new Telnet("www.baidu.com", 80);
                telnet.start();
                Log.i(TAG, "Telnet: " + telnet.toString());
                // target
                // 目标
                TraceRoute traceRoute = new TraceRoute("www.baidu.com");
                traceRoute.start();
                Log.i(TAG, "\n\nTraceRoute: " + traceRoute.toString());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        Tools.sleepIgnoreInterrupt(0, 500);
                    }
                }

                private void add() {
                    count++;
                    final long cur = count;
                    UiKit.runOnMainThreadAsync(new Runnable() {
                        @Override
                        public void run() {
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
                        Tools.sleepIgnoreInterrupt(0, 500);
                    }
                }

                private void add() {
                    count++;
                    final long cur = count;
                    UiKit.runOnMainThreadSync(new Runnable() {
                        @Override
                        public void run() {
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


}
