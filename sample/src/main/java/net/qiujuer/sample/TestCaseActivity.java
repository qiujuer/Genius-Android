package net.qiujuer.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.app.ToolKit;
import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.nettool.DnsResolve;
import net.qiujuer.genius.nettool.Ping;
import net.qiujuer.genius.nettool.Telnet;
import net.qiujuer.genius.nettool.TraceRoute;
import net.qiujuer.genius.util.FixedList;
import net.qiujuer.genius.util.HashUtils;
import net.qiujuer.genius.util.Log;
import net.qiujuer.genius.util.Log.LogCallbackListener;
import net.qiujuer.genius.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QiuJu
 * on 2014/11/25.
 * <p/>
 * 测试用例界面
 */
public class TestCaseActivity extends Activity {
    private static final String TAG = TestCaseActivity.class.getSimpleName();
    TextView mText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_case);

        mText = (TextView) findViewById(R.id.text);

        //添加回调
        Log.addCallbackListener(new LogCallbackListener() {
            @Override
            public void onLogArrived(final Log data) {
                //异步显示到界面
                ToolKit.runOnMainThreadAsync(new Runnable() {
                    @Override
                    public void run() {
                        if (mText != null)
                            mText.append("\n" + data.getMsg());
                    }
                });
            }
        });

        //开始测试
        testLog();
        testToolKit();
        testHashUtils();
        testTools();
        testFixedList();
        testNetTool();
        testCommand();
    }

    @Override
    protected void onDestroy() {
        mText = null;
        super.onDestroy();
    }

    /**
     * 测试 App 工具包
     */
    void testToolKit() {
        // 同步模式一般用于更新界面同时等待界面更新完成后才能继续往下走的情况
        // 异步模式一般用于线程操作完成后统一更新主界面的情况
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 测试运行在主线程中的情况，所以建立子线程进行调用
                // 方法中的操作将会切换到主线程中执行
                String msg = "ToolKit:";
                long start = System.currentTimeMillis();
                // 测试同步模式，在该模式下
                // 子线程将会等待其执行完成
                // 在主线程中调用该方法后才能继续往下走
                // 该方法首先会将要执行的命令放到队列中，等待主线程执行
                ToolKit.runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        Tools.sleepIgnoreInterrupt(20);
                    }
                });
                msg += "同步时间:" + (System.currentTimeMillis() - start) + ", ";

                start = System.currentTimeMillis();
                // 测试异步模式，在该模式下
                // 子线程调用该方法后既可继续往下走，并不会阻塞
                ToolKit.runOnMainThreadAsync(new Runnable() {
                    @Override
                    public void run() {
                        Tools.sleepIgnoreInterrupt(20);
                    }
                });
                msg += "异步时间:" + (System.currentTimeMillis() - start) + " ";
                Log.v(TAG, msg);
            }
        });
        thread.start();
    }

    /**
     * 日志测试
     */
    public void testLog() {
        //是否调用系统Android Log，发布时可设置为false
        Log.setCallLog(true);

        //清理存储的文件
        Log.clearLogFile();

        //是否开启写入文件，存储最大文件数量，单个文件大小（Mb）
        Log.setSaveLog(true, 10, 1);

        //设置是否监听外部存储插入操作
        //开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
        //此操作依赖于是否开启写入文件功能，未开启则此方法无效
        //是否开启，SD卡目录
        Log.setCopyExternalStorage(true, "Test/Logs");

        //设置日志等级
        //VERBOSE为5到ERROR为1依次递减
        Log.setLevel(Log.ALL);

        Log.v(TAG, "测试日志 VERBOSE 级别。");
        Log.d(TAG, "测试日志 DEBUG 级别。");
        Log.i(TAG, "测试日志 INFO 级别。");
        Log.w(TAG, "测试日志 WARN 级别。");
        Log.e(TAG, "测试日志 ERROR 级别。");

        Log.setLevel(Log.INFO);
        Log.v(TAG, "二次测试日志 VERBOSE 级别。");
        Log.d(TAG, "二次测试日志 DEBUG 级别。");
        Log.i(TAG, "二次测试日志 INFO 级别。");
        Log.w(TAG, "二次测试日志 WARN 级别。");
        Log.e(TAG, "二次测试日志 ERROR 级别。");

        Log.setLevel(Log.ALL);
    }

    /**
     * 测试MD5
     */
    public void testHashUtils() {
        Log.i(TAG, "HashUtils：QIUJUER的MD5值为：" + HashUtils.getStringMd5("QIUJUER"));
        //文件MD5不做演示，传入file类即可
    }

    /**
     * 测试工具类
     */
    public void testTools() {
        Log.i(TAG, "Tools：getAndroidId：" + Tools.getAndroidId(Genius.getApplication()));
        Log.i(TAG, "Tools：getSerialNumber：" + Tools.getSerialNumber());
    }

    /**
     * 测试固定长度队列
     */
    public void testFixedList() {
        //初始化最大长度为5
        FixedList<Integer> list = new FixedList<Integer>(5);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        //添加4个元素
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        //继续追加2个
        list.add(5);
        list.add(6);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        //调整最大长度
        list.setMaxSize(6);
        list.add(7);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        list.add(8);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        //缩小长度，自动删除前面多余部分
        list.setMaxSize(3);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        list.add(9);
        Log.i(TAG, "FixedList:" + list.size() + " ," + list.getMaxSize());
        //添加一个列表进去，自动删除多余部分
        List<Integer> addList = new ArrayList<Integer>();
        addList.add(10);
        addList.add(11);
        addList.add(12);
        addList.add(13);
        list.addAll(addList);
        Log.i(TAG, "FixedList:AddList:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());
        //采用poll方式弹出元素
        Log.i(TAG, "FixedList:Poll:[" + list.poll() + "] " + list.size() + " ," + list.getMaxSize());
        Log.i(TAG, "FixedList:Poll:[" + list.poll() + "] " + list.size() + " ," + list.getMaxSize());
        Log.i(TAG, "FixedList:Poll:[" + list.poll() + "] " + list.size() + " ," + list.getMaxSize());
        //末尾插入元素与add一样
        list.addLast(14);
        list.addLast(15);
        list.addLast(16);
        list.addLast(17);
        list.addLast(18);
        Log.i(TAG, "FixedList:AddLast:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());
        //从头部插入，默认删除尾部超出部分
        list.addFirst(19);
        list.addFirst(20);
        Log.i(TAG, "FixedList:AddFirst:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());
        //Remove与poll类似不过不返回删除元素，只会删除一个
        list.remove();
        Log.i(TAG, "FixedList:Remove:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());
        //清空操作
        list.clear();
        Log.i(TAG, "FixedList:Clear:" + list.toString() + " " + list.size() + " ," + list.getMaxSize());

        //使用List操作,最大长度2
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
     * 测试命令行执行
     */
    public void testCommand() {
        //同步
        Thread thread = new Thread() {
            public void run() {
                //调用方式与ProcessBuilder传参方式一样
                Command command = new Command(Command.TIMEOUT, "/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");
                //同步方式执行
                String res = Command.command(command);
                Log.i(TAG, "\n\nCommand 同步：" + res);
            }
        };
        thread.setDaemon(true);
        thread.start();

        //异步
        Command command = new Command("/system/bin/ping",
                "-c", "4", "-s", "100",
                "www.baidu.com");

        //异步方式执行
        //采用回调方式，无需自己建立线程
        //传入回调后自动采用此种方式
        Command.command(command, new Command.CommandListener() {
            @Override
            public void onCompleted(String str) {
                Log.i(TAG, "\n\nCommand 异步 onCompleted：\n" + str);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "\n\nCommand 异步 onCancel");
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "\n\nCommand 异步 onError:" + (e != null ? e.toString() : "null"));
            }
        });
    }


    /**
     * 基本网络功能测试
     */
    public void testNetTool() {
        //所有目标都可为IP地址
        Thread thread = new Thread() {
            public void run() {
                //包数，包大小，目标，是否解析IP
                Ping ping = new Ping(4, 32, "www.baidu.com", true);
                ping.start();
                Log.i(TAG, "Ping：" + ping.toString());
                //目标，可指定解析服务器
                DnsResolve dns = new DnsResolve("www.baidu.com");
                dns.start();
                Log.i(TAG, "DnsResolve：" + dns.toString());
                //目标，端口
                Telnet telnet = new Telnet("www.baidu.com", 80);
                telnet.start();
                Log.i(TAG, "Telnet：" + telnet.toString());
                //目标
                TraceRoute traceRoute = new TraceRoute("www.baidu.com");
                traceRoute.start();
                Log.i(TAG, "\n\nTraceRoute：" + traceRoute.toString());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}