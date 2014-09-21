package net.qiujuer.sample;

import android.os.Handler;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.util.Log;
import net.qiujuer.genius.util.ToolUtils;


/**
 * Created by Qiujuer on 2014/9/2.
 * 测试单元
 */
public class TestCase {
    private static final String TAG = TestCase.class.getName();
    private Handler handler;

    public TestCase(Handler handler) {
        //初始化全局Context
        this.handler = handler;
    }

    /**
     * 测试命令行执行
     */
    public void testCommand() {
        Thread thread = new Thread() {
            public void run() {
                //执行命令，后台服务自动控制
                //调用方式与ProcessBuilder传参方式一样
                Command command = new Command("/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");
                //同步方式执行
                String res = Command.command(command);
                Log.i(TAG, "Ping 测试结果：" + res);
            }
        };
        thread.start();

        thread = new Thread() {
            public void run() {
                //60s后执行
                ToolUtils.sleepIgnoreInterrupt(60000);

                //执行命令，后台服务自动控制
                //调用方式与ProcessBuilder传参方式一样
                Command command = new Command("/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");
                //同步方式执行
                String res = Command.command(command);
                Log.i(TAG, "Ping 测试结果：" + res);
            }
        };
        thread.start();


        Command command = new Command("/system/bin/ping",
                "-c", "4", "-s", "100",
                "www.baidu.com");

        //异步方式执行
        //采用回调方式，无需自己建立线程
        //传入回调后自动采用此种方式
        Command.command(command, new Command.CommandListener() {
            @Override
            public void onCompleted(String str) {
                Log.i(TAG, "onCompleted：\n" + str);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError");
            }
        });
    }

    /**
     * 日志测试
     */
    public void testGLog() {
        //是否调用Android Log类
        Log.setCallLog(true);

        //是否开启写入文件
        Log.setSaveLog(Genius.getApplication(), true, 10, 1, null);

        //设置是否监听外部存储插入操作
        //开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
        //此操作依赖于是否开启写入文件功能，未开启则此方法无效
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
}
