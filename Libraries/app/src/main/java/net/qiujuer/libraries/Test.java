package net.qiujuer.libraries;

import android.content.Context;

import net.qiujuer.libraries.genius.command.CommandModel;
import net.qiujuer.libraries.genius.journal.Logs;
import net.qiujuer.libraries.genius.journal.LogsData;
import net.qiujuer.libraries.genius.journal.LogsInterface;
import net.qiujuer.libraries.genius.methods.StaticValues;

import java.util.Date;
import java.util.List;

/**
 * Created by Genius on 2014/8/13.
 * 测试单元
 */
public class Test {
    public Test(Context context) {
        //初始化全局Context
        StaticValues.setContext(context);
    }

    /**
     * 测试命令行执行
     */
    public void testCommand() {
        Thread thread = new Thread() {
            public void run() {
                //执行命令，后台服务自动控制
                //调用方式与ProcessBuilder传参方式一样
                CommandModel processModel = new CommandModel("/system/bin/ping",
                        "-c", "4", "-s", "100",
                        "www.baidu.com");
                String res = CommandModel.command(processModel);

                Logs.i(Test.class.getName(), "Ping 测试结果：" + res);
            }
        };
        thread.start();
    }

    /**
     * 日志测试
     */
    public void testLogs() {
        //是否调用Android Log类
        Logs.IsTriggerSystem = true;

        //是否开启写入文件
        Logs.IsWriteFile = false;

        //设置是否监听外部存储插入操作
        //开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
        //此操作依赖于是否开启写入文件功能，未开启则此方法无效
        Logs.setWriteExternalStorage(false);

        //添加回调
        Logs.addLogsInterface(new LogsInterface() {
            @Override
            public void OnShowLogListener(LogsData data) {
                //有日志写来了
            }

            @Override
            public void OnAddedLogListener(List<LogsData> dataList) {
                //添加监听后自动读取缓存
            }
        });

        //设置日志等级
        //VERBOSE为5到ERROR为1依次递减
        Logs.setLevel(3);

        Logs.e(Test.class.getName(), "测试日志 ERROR 级别。");
        Logs.w(Test.class.getName(), "测试日志 WARN 级别。");
        Logs.i(Test.class.getName(), "测试日志 INFO 级别。");
        Logs.d(Test.class.getName(), "测试日志 DEBUG 级别。");
        Logs.v(Test.class.getName(), "测试日志 VERBOSE 级别。");

        Logs.i(new LogsData(3, Test.class.getName(), "测试日志 直接传入LogsData类 自动获取当前时间。"));
        Logs.i(new LogsData(new Date(), 2, Test.class.getName(), "测试日志 直接传入LogsData类 传入时间。"));

        //单独开启VERBOSE级别，其他亦可
        Logs.VERBOSE = true;

        Logs.e(Test.class.getName(), "二次测试日志 ERROR 级别。");
        Logs.w(Test.class.getName(), "二次测试日志 WARN 级别。");
        Logs.i(Test.class.getName(), "二次测试日志 INFO 级别。");
        Logs.d(Test.class.getName(), "二次测试日志 DEBUG 级别。");
        Logs.v(Test.class.getName(), "二次测试日志 VERBOSE 级别。");
    }
}
