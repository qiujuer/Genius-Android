##Genius-Android是什么?
Genius-Android是一个我在日常使用中把经常使用的方法集合。

Genius库现在提供了5个基本板块：`app`（界面常用方法），`command`（命令行执行），`material`（Android L中的Material控件），`net tool`（Ping，DNS...），`util`（常用方法或者类）。

在这里向我参考过的开源库作者致敬！eg:`FlatUI`，`blurring`


##Genius-Android库有哪些功能？
* 方便的`日志`功能
  > *  使用方式与系统日志Log类一样
  > *  可设置是否调用系统Log类对应方法
  > *  可一键设置日志级别，解决发布的烦恼
  > *  可实现将日志写入文件中保存以便查看分析
  > *  可实现将日志一键拷贝到外部存储设备中
  > *  可设置是否存储文件以及拷贝到外部存储设备
  > *  可添加事件监听，方便界面显示日志信息

* 常用的`方法`集合
  > *  方便的MD5运算，可对字符串与文件进行
  > *  线程休眠无需多加try catch模块
  > *  获取设备ID标识
  > *  获取设备SN标识信息
  > *  获取DeviceId标识信息
  > *  可判断是否安装指定软件（包名）

* 常用的`属性`集合
  > *  全局的AppContext属性设置与获取

* 强大的`命令行`系统
  > *  使用独立服务进程控制进程创建销毁
  > *  智能的进程管理服务
  > *  超高的并发效率，不用担心缓冲区是否满的问题
  > *  简单化的操作，与ProcessBuilder类操作类似
  > *  智能的监听与自杀方式，保证进程不阻塞
  > *  子进程创建失败后自动重新调用执行，默认10次
  > *  保证语句正常执行不疏漏
  > *  一键化的启动命令行与取消操作
  > *  可同步与异步方式执行，异步完成通知

* 傻瓜化的'NetTool'包
  > *  一键Ping操作，无需命令行，无需Root
  > *  一键DNS域名解析，可指定解析服务器
  > *  一键TelNet功能，对指定IP Port测试
  > *  一键Tracert测试，能记录每一跳丢包与延时值
  > *  一键的测速工具，对指定文件下载测试
  > *  全部以类的方式调用，不用关心细节问题
  > *  能对任务执行取消操作
  > *  高并发的路由测试，能在40s左右测试完成
    

* 以后还有更多；并提供MaterialUI控件。。。


##获取库
* `Star`或者`Fork`项目；下载。打开 `release`文件夹中的`*.jar`或者`*.aar`文件可以直接导入到自己项目中。
  *  `*.jar`无法使用控件资源，如R..。
  *  `*.aar`能使用所有的类和控件以及字体等。
  *  `*.aar`本地引入方法：
    ```javascript
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
    dependencies {
        compile(name:'genius_0.4.0', ext:'aar')
    }
    ```
* MavenCentral方式，无需下载，Android Studio软件中直接使用：
```javascript
dependencies {
    repositories {
        mavenCentral()
    }
    compile 'com.github.qiujuer:genius:0.4.0'
}
```


##使用方式
* 初始化与销毁：
```javascript
//Command模块必须
//Log类需要进行存储则需要调用
//只使用控件与nettool模块可不用调用
Genius.initialize(getApplication());
Genius.dispose();
```
* ToolUtils：
```javascript
//休眠
ToolUtils.sleepIgnoreInterrupt(100);
//拷贝文件
ToolUtils.copyFile(source, target);
//AndroidId
ToolUtils.getAndroidId(context);
//SN编号
ToolUtils.getSerialNumber();
//DeviceId
ToolUtils.getDeviceId(context);
//判断包是否安装
ToolUtils.isAvailablePackage(context, packageName);
```
* HashUtils：
```javascript
//获取字符串MD5
HashUtils.getStringMd5(str);
//获取文件MD5
ToolUtils.getFileMd5(file);
```
* Command：
```javascript
//执行命令，后台服务自动控制
//调用方式与ProcessBuilder传参方式一样
//同步方式
Command command = new Command("/system/bin/ping",
        "-c", "4", "-s", "100",
        "www.baidu.com");
//同步方式执行
String res = Command.command(command);
Log.i(TAG, "Ping 测试结果：" + res);
//异步方式
Command command = new Command("/system/bin/ping",
        "-c", "4", "-s", "100",
        "www.baidu.com");
//异步方式执行
//采用回调方式，无需自己建立线程
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
//销毁
Command.dispose();
```
* NetTool：
```javascript
//Ping
Ping ping = new Ping("www.baidu.com");
ping.start();
if (ping.getError() != NetModel.SUCCEED || ping.getDelay() == 0 || ping.getDelay() > 150) {
    Log.i(TAG,ping.getDelay() + "ms" + "异常");
} else {
    Log.i(TAG,ping.getDelay() + "ms" + "正常");
}
//DNS
DnsResolve dnsResolve = new DnsResolve(mTag);
dnsResolve.start();
if (dnsResolve.getError() != NetModel.SUCCEED || dnsResolve.getDelay() > 100 || dnsResolve.getAddresses().size() == 0) {
    Log.i(TAG,"Size:" + dnsResolve.getAddresses().size() + ", Delay:" + dnsResolve.getDelay() + "ms" + "异常");
} else {
    Log.i(TAG,"Size:" + dnsResolve.getAddresses().size() + ", Delay:" + dnsResolve.getDelay() + "ms" + "正常");
}
...
其他类似
...
```
* Log：
```javascript
//添加回调
//回调类
Log.LogCallbackListener listener = new Log.LogCallbackListener() {
    @Override
    public void onLogArrived(Log data) {
        //有日志写来了
        Message msg = mHandler.obtainMessage(0x1, data.getMsg());
        mHandler.sendMessage(msg);
    }
};
//添加
Log.addCallbackListener(listener);
...
//是否调用系统Android Log，可控制是否显示
Log.setCallLog(true);
//是否开启写入文件，文件数量，单个文件大小（Mb），重定向地址
Log.setSaveLog(Genius.getApplication(), true, 10, 1, null);
//设置是否监听外部存储插入操作
//开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
//此操作依赖于是否开启写入文件功能，未开启则此方法无效
Log.setCopyExternalStorage(true, "Test/Logs");
...
//设置日志等级
//VERBOSE到ERROR依次递减
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
```


##给予权限
```javascript
    <!--网络 权限-->
    <uses-permission android:name="android.permission.INTERNET" />
    <!--日志写文件 权限-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!--getDeviceId 权限-->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```


##开发者
在Android Studio中下载并导入本项目即可，Android Studio版本：>0.8.0

里边含有一个库以及一个测试项目，可将库导入到自己的项目中使用。


##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件：qiujuer@live.cn
* QQ： 756069544
* Weibo： [@qiujuer](http://weibo.com/qiujuer)


##捐助开发者
在兴趣的驱动下,写一个`免费`的东西，有欣喜，也还有汗水，希望你喜欢我的作品，同时也能支持一下。
当然，有钱捧个钱场（支付宝:qiujuer@live.cn），没钱捧个人场，谢谢各位。

其实大家喜欢就好

##关于作者

```javascript
  var ihubo = {
    nickName  : "qiujuer",
    site : "http://qiujuer.net"
  }
```

License
--------

    Copyright 2014 CengaLabs.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

