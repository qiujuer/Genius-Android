## Genius-Android是什么?

Genius-Android是一个我在日常使用中把经常使用的方法集合。

Genius库现在提供了5个基本板块：

`app`（Ui），`material`（Material控件），`command`（命令行），`net tool`（Ping，DNS...），`util`（常用方法或者类）。

在这里向我参考过的开源库作者致敬

`FlatUI`，`blurring`

## Screenshots

##### MaterialButton
![Animated gif][1]

##### Themes
![Themes][2]


## Genius-Android库有哪些功能？

* `app`
  > *  可在子线程`同步`切换到主线程
  > *  可在子线程`异步`切换到主线程

* `material`
  > *  内置字体 `opensans` `roboto`
  > *  字体颜色 `none` `dark` `light`
  > *  含有五种字体粗细样式切换
  > *  含有十四种主题颜色搭配
  > *  `MaterialButton` 点击动画特效

* `command`
  > *  独立服务进程控制进程创建销毁
  > *  智能的进程管理服务
  > *  超高的并发效率，不担心缓冲区问题
  > *  简单的操作，与ProcessBuilder操作类似
  > *  智能的监听与自杀方式，保证进程不阻塞
  > *  子进程失败后自动重新调用执行，重复10次
  > *  保证语句正常执行不疏漏
  > *  一键化的启动与取消操作，自由控制
  > *  可同步与异步方式执行，异步事件通知

* `net tool`
  > *  一键Ping操作，无需命令行，无需Root
  > *  一键DNS域名解析，可指定解析服务器
  > *  一键TelNet功能，对指定IP Port测试
  > *  一键TraceRoute，记录每一跳丢包与延时
  > *  一键的测速工具，对指定文件下载测试
  > *  控制随心，取消随心；不用关心细节问题
  > *  高并发的路由测试，可在40s左右测试完成

* `util`
  > *  全局AppContext属性获取
  > *  方便的MD5运算，包括字符串与文件
  > *  线程休眠无需多加try catch模块
  > *  获取设备标识`ID`，`SN`，`DeviceId`
  > *  可检测是否安装指定软件（包名）
  > *  `Log`：使用方式与Android日志Log一样
  > *  `Log`：一键设置是否调用系统Log类
  > *  `Log`：可一键设置日志级别，解决发布的烦恼
  > *  `Log`：可实现将日志写入文件中保存以便查看分析
  > *  `Log`：可实现将日志一键拷贝到外部存储设备中
  > *  `Log`：可添加事件监听，方便界面显示日志信息
  > *  `FixedList`：定长队列，自动弹出，保持队列数量


## 获取库

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
      compile(name:'genius_0.6.5', ext:'aar')
  }
  
  ```

* MavenCentral方式，无需下载，Android Studio软件中直接使用：

```javascript
dependencies {
    repositories {
        mavenCentral()
    }
    compile 'com.github.qiujuer:genius:0.6.5'
}

```



## 使用方法

##### 初始化与销毁

```javascript
// Command 使用模块必须初始化
// Log 类如进行存储则需要初始化
// 只使用控件与 net tool 模块可不初始化
Genius.initialize(Application application);
Genius.dispose();

```


##### `app` 模块

```javascript
// ‘UiModel‘ 类实现其中 ‘doUi()’ 方法
// ‘doUi()’ 运行在主线程中，可在其中进行控件操作
// 同步进入 ‘Activity‘ 主线程
UiTool.syncRunOnUiThread(Activity activity, UiModel ui);

// 异步进入 ‘Activity‘ 主线程
UiTool.syncRunOnUiThread(Activity activity, UiModel ui);

```


##### `material` 模块

```javascript
// 首先需要在根容器中指定：
<LinearLayout
    ...
    xmlns:material="http://schemas.android.com/apk/res-auto"/>

// 提供14种主题样式，见截图
// 提供2种字体：`opensans` `roboto`
// 字体粗细：`bold` `extrabold` `extralight` `light` `regular`

// ==================MaterialButton==================
<net.qiujuer.genius.material.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="80dp"
    android:text="MaterialButton"
    material:gm_textAppearance="light"
    material:gm_fontFamily="opensans"
    material:gm_fontWeight="bold"
    material:gm_isMaterial="true"
    material:gm_theme="@array/grass" />

// `gm_textAppearance`: 指定字体颜色，默认为 `none`
// `gm_fontFamily`: 指定两种字体中的一种字体
// `gm_fontWeight`: 指定字体粗细
// `gm_isMaterial`: 是否打开 Material 动画，默认 `true`
// `gm_theme`: 指定主题样式，14种任意选

```


##### `command` 模块

```javascript
// 执行命令，后台服务自动控制
// 调用方式与ProcessBuilder传参方式一样
// 同步方式
// 完成后结果直接返回
Command command = new Command("/system/bin/ping",
        "-c", "4", "-s", "100",
        "www.baidu.com");
String res = Command.command(command);
Log.i(TAG, "Ping 测试结果：" + res);

// 异步方式
// 结果以事件回调方式返回
Command command = new Command("/system/bin/ping",
        "-c", "4", "-s", "100",
        "www.baidu.com");
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

// 销毁
// 可调用 ‘Genius.dispose()’ 方法统一销毁
Command.dispose();

```


##### `net tool` 模块

```javascript
// Ping
// 传入域名或者IP
// 结果：是否执行成功、延时、丢包
Ping ping = new Ping("www.baidu.com");
// 开始
ping.start();
if (ping.getError() != NetModel.SUCCEED) {
    Log.i("异常");
} else {
    Log.i(TAG,ping.getDelay() + "ms");
}

// DNS
// 传入域名 + 服务器地址
// 结果：是否执行成功、延时、Ip地址集合
DnsResolve dnsResolve = new DnsResolve("www.baidu.com");
// 开始
dnsResolve.start();
if (dnsResolve.getError() != NetModel.SUCCEED) {
    Log.i("异常");
} else {
    Log.i(TAG,"Size:" + dnsResolve.getAddresses().size() + ", Delay:" + dnsResolve.getDelay() + "ms");
}
...
其他的类似
...

```

##### `util` 模块

```javascript
// ===================FixedList===================
// 固定长度队列
// 可指定长度，使用方法与普通队列类似
// 当加入元素数量达到指定数量时将弹出元素
// 头部插入尾部弹出，尾部插入头部弹出

// 初始化最大长度为5
FixedList<Integer> list = new FixedList<Integer>(5);
// 添加元素
list.add(1);
// 末尾插入元素与add一样
list.addLast(1);
// 从头部插入，默认删除尾部超出元素
list.addFirst(19);
// 添加一个列表
list.addAll(new ArrayList<Integer>());

// 获取最大容量
list.getMaxSize();
// 调整最大长度；缩小长度时将自动删除头部多余元素
list.setMaxSize(3);

// 采用poll方式弹出元素
int i = list.poll();
// remove 与 poll 类似，不过不返回删除元素，仅删除一个元素
list.remove();
// 清空操作
list.clear();

// 可使用List操作
List<Integer> list1 = new FixedList<Integer>(2);
list1.add(1);
list1.clear();


// ====================HashUtils==================
// 哈希计算（Md5）
// 可计算字符串与文件Md5值

// 获取字符串MD5
String hash = HashUtils.getStringMd5(String str);
// 获取文件MD5
String hash = HashUtils.getFileMd5(File file);


// ======================Log======================
// 日志类
// 调用方法与使用Android默认方法一样
// 可设置其是否存储日志信息
// 可拷贝日志信息到SD卡
// 可在主界面添加事件回调，界面实时显示日志

// 添加回调
// 回调类
Log.LogCallbackListener listener = new Log.LogCallbackListener() {
    @Override
    public void onLogArrived(Log data) {
        //日志来了
    }
};
// 添加
Log.addCallbackListener(listener);

// 是否调用系统Android Log，可控制是否显示
Log.setCallLog(true);
// 是否开启写入文件，文件数量，单个文件大小（Mb），重定向地址
Log.setSaveLog(Genius.getApplication(), true, 10, 1, null);
// 设置是否监听外部存储插入操作
// 开启时插入外部设备（SD）时将拷贝存储的日志文件到外部存储设备
// 此操作依赖于是否开启写入文件功能，未开启则此方法无效
Log.setCopyExternalStorage(true, "Test/Logs");

// 设置日志等级
// ALL(全部显示)，VERBOSE到ERROR依次递减
Log.setLevel(Log.ALL);
Log.setLevel(Log.INFO);

// 添加日志
Log.v(TAG, "日志 VERBOSE ");
Log.d(TAG, "日志 DEBUG ");
Log.i(TAG, "日志 INFO ");
Log.w(TAG, "日志 WARN ");
Log.e(TAG, "日志 ERROR ");


// ====================ToolUtils====================
// 常用工具包
// 全部为静态方法，以后会持续添加完善

// 休眠
ToolUtils.sleepIgnoreInterrupt(long time);
// 拷贝文件
ToolUtils.copyFile(File source, File target);
// AndroidId
ToolUtils.getAndroidId(Context context);
// SN编号
ToolUtils.getSerialNumber();
// DeviceId
ToolUtils.getDeviceId(Context context);
// 判断包是否安装
ToolUtils.isAvailablePackage(Context context, String packageName);

```


## 给予权限

```javascript
    <!-- 网络 权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- 日志写文件 权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- getDeviceId 权限 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    
```


## 开发者

在Android Studio中下载并导入本项目即可，Android Studio > 0.8.2

里边含有一个库以及一个测试项目，可将库导入到自己的项目中使用。


## 反馈

在使用中有任何问题，欢迎能及时反馈给我，可以用以下联系方式跟我交流

* 邮件：qiujuer@live.cn
* QQ： 756069544
* Weibo： [@qiujuer](http://weibo.com/qiujuer)


## 捐助开发者

在兴趣的驱动下,写一个`免费`的东西，有欣喜，也还有汗水，希望你喜欢我的作品，同时也能支持一下。
当然，有钱捧个钱场（支付宝:qiujuer@live.cn）；没钱捧个人场，谢谢各位。


## 关于我

```javascript
  var info = {
    nickName  : "qiujuer",
    site : "http://www.qiujuer.net"
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


 [1]: https://raw2.github.com/qiujuer/Genius-Android/master/sample-images/material.gif
 [2]: https://raw2.github.com/qiujuer/Genius-Android/master/sample-images/themes.png
