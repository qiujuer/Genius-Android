package net.qiujuer.sample;

import android.content.Context;
import android.widget.Toast;

import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.util.GLog;
import net.qiujuer.genius.util.GlobalValue;

/**
 * Created by Qiujuer on 2014/9/2.
 * 测试单元
 */
public class TestCase {
    private static final String TAG = TestCase.class.getName();

    public TestCase(Context context) {
        //初始化全局Context
        GlobalValue.setContext(context);
    }

    /**
     * 测试命令行执行
     */
    public void testCommand() {
        Thread thread = new Thread() {
            public void run() {
                //执行命令，后台服务自动控制
                //调用方式与ProcessBuilder传参方式一样
                Command processModel = new Command("/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");
                String res = Command.command(processModel);

                GLog.i(TAG, "Ping 测试结果：" + res);
            }
        };
        thread.start();
    }

    /**
     * 日志测试
     */
    public void testGLog() {
        //是否调用Android Log类
        GLog.setCallLog(true);

        //是否开启写入文件
        GLog.setSaveLog(GlobalValue.getContext(), true, 10, 1, null);

        //设置是否监听外部存储插入操作
        //开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
        //此操作依赖于是否开启写入文件功能，未开启则此方法无效
        GLog.setCopyExternalStorage(GlobalValue.getContext(), true, "Test/Logs");

        //添加回调
        GLog.addCallbackListener(new GLog.OnLogCallbackListener() {
            @Override
            public void onLogArrived(GLog data) {
                //有日志写来了
                Toast.makeText(GlobalValue.getContext(), data.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onListenerAdded(GLog[] dataArray) {
                //添加监听后自动读取缓存
                if (dataArray != null)
                    for (GLog data : dataArray) {
                        onLogArrived(data);
                    }
            }
        });

        //设置日志等级
        //VERBOSE为5到ERROR为1依次递减
        GLog.setLevel(GLog.ALL);


        GLog.v(TAG, "测试日志 VERBOSE 级别。");
        GLog.d(TAG, "测试日志 DEBUG 级别。");
        GLog.i(TAG, "测试日志 INFO 级别。");
        GLog.w(TAG, "测试日志 WARN 级别。");
        GLog.e(TAG, "测试日志 ERROR 级别。");

        GLog.setLevel(GLog.INFO);
        GLog.v(TAG, "二次测试日志 VERBOSE 级别。");
        GLog.d(TAG, "二次测试日志 DEBUG 级别。");
        GLog.i(TAG, "二次测试日志 INFO 级别。");
        GLog.w(TAG, "二次测试日志 WARN 级别。");
        GLog.e(TAG, "二次测试日志 ERROR 级别。");
    }
}
