## Version 2.1.0 Guide

[`中文`](README-ZH.md) [`English`](README.md) [`Guides`](/docs/guides/GuideCatalog.md) [`Sample`](/release/sample.apk)

## Genius-Android是什么?

![icon](art/launcher.png)

**Genius-Android** 是 **Android** 中一些常用的的方法集合, **Genius** 提供6个基本板块：

`app`（**Ui**）`animation`（**动画**）`widget`（**Material控件**） `command`（**命令行**） `net tool`（**Ping、Dns...**） `util`（**常用方法,类**）


## 截图

##### GeniusUI

###### CheckBox
![GeniusUI](art/ui_checkbox.gif)

###### Button
![GeniusUI](art/ui_button.gif)

###### All
![GeniusUI](art/ui_all.png)

##### BlurKit
![BlurKit](art/blur_kit.png)

##### ThemeColors
![ThemeColors](art/theme_colors.png)


## 功能模块

* `app`
  > *  `UIKit` 支持子线程`同步`、`异步`切换到主线程操作
  > *  `BlurKit` 支持`Java`、`Jni`使用`StackBlur`算法模糊图片

* `animation`
  > *  `TouchEffectAnimator` 支持快速响应点击特效
  > *  `TouchEffectEnum` Press, Move, Ripple, Ease, None

* `widget`
  > *  字体 `opensans` `roboto`
  > *  颜色 `none` `dark` `light`
  > *  控件 `GeniusButton` `GeniusCheckBox` `GeniusTextView`

* `command`
  > *  独立服务进程执行命令行工作
  > *  与`ProcessBuilder`操作类似
  > *  智能修正运行错误，解决运行故障
  > *  一键化的启动与取消操作，自由控制
  > *  可同步与异步方式执行，可回调事件

* `net tool`
  > *  一键`Ping` `DNS` `TelNet` `TraceRoute`
  > *  可控制，可取消；不必关心细节问题
  > *  并发的路由任务，可在40s左右测试完成

* `util`
  > *  `AppContext` 全局、存取方便快捷
  > *  `HashUtils`  字符串与文件`MD5`获取
  > *  `Tools` `ID` `SN` 确定设备唯一标识
  > *  `Log` 如系统Log一样使用简单，一键开关
  > *  `Log` 可存储日志到文件，方便分析差错
  > *  `Log` 可添加事件监听，方便界面显示日志信息
  > *  `FixedList` 定长队列，自动弹出，保持队列数量


## 获取库

* `Star` 和 `Fork` 项目。
* `MavenCentral` 远程导入 :

```gradle
// 在项目 "build.gradle" 中添加
dependencies {
  compile 'com.github.qiujuer:genius:2.1.0'
}

```


## 更新日志

* 版本：`2.1.0`
* 日期：`2015-01-14`
* 日志：[`更新日志`](docs/NOTES.md)


## 使用方法

##### 初始化与销毁

```java
Genius.initialize(Application application);
Genius.dispose();

```


##### `widget` 模块

```xml
// 首先在根容器中指定：
<LinearLayout
    ...
    xmlns:genius="http://schemas.android.com/apk/res-auto"/>

// 主题样式：见截图Colors
// 提供字体：`opensans` `roboto`
// 字体粗细：`bold` `extrabold` `extralight` `light` `regular`

// ==================全局属性==================
<net.qiujuer.genius.widget.all
    ...
    genius:g_textAppearance="light"
    genius:g_fontFamily="opensans"
    genius:g_fontWeight="bold"
    genius:g_fontExtension="ttf"
    genius:g_cornerRadius="5dp"
    genius:g_borderWidth="5dp"
    genius:g_theme="@array/StrawberryIce" />

// `g_textAppearance`: 指定字体颜色，默认为 `none`
// `g_fontFamily`: 指定两种字体中的一种字体
// `g_fontWeight`: 指定字体粗细
// `g_fontExtension`: 字体扩展名
// `g_cornerRadius`: 控件边缘圆角半径,默认为 `0`
// `g_borderWidth`: 描边宽度
// `g_theme`: 指定主题样式，17种任意选

// ==================GeniusButton==================
<net.qiujuer.genius.widget.GeniusButton
    ...
    genius:g_touchEffect="move"
    genius:g_touchEffectColor="#ff4181ff"
    genius:g_blockButtonEffectHeight="10dp" />

// `g_touchEffect`: press, move, ease, ripple, none
// `g_touchEffectColor`: 扩散效果颜色, `g_touchEffect`为"None"则无效
// `g_blockButtonEffectHeight`: 底部阴影高度

// ==================GeniusCheckBox==================
<net.qiujuer.genius.widget.GeniusCheckBox
    ...
    genius:g_ringWidth="2dp"
    genius:g_circleRadius="22dp"
    genius:g_checked="true"
    genius:g_enabled="true" />

// `g_ringWidth`: 圆环宽度
// `g_circleRadius`: 圆心半径
// `g_checked`: 是否选中
// `g_enabled`: 是否可点击

// ==================GeniusTextView==================
<net.qiujuer.genius.widget.GeniusTextView
    ...
    genius:g_textColor="light"
    genius:g_backgroundColor="dark"
    genius:g_customBackgroundColor="#FFFFFF" />

// `g_textColor`: 字体颜色类型
// `g_backgroundColor`: 背景颜色类型
// `g_customBackgroundColor`: 背景颜色

```


##### `app` 模块

```java
// "Runnable" 实现其中 "run()" 方法
// "run()" 运行在主线程中，可在其中进行界面操作
// 同步进入主线程,等待主线程处理完成后继续执行子线程
UIKit.runOnMainThreadSync(Runnable runnable);
// 异步进入主线程,无需等待
UIKit.runOnMainThreadAsync(Runnable runnable);
// 同步但是子线程只等待指定时间
// @param runnable Runnable 接口
// @param waitTime 子线程等待时长
// @param cancel   等待时间到时是否取消主线程执行该任务
UIKit.runOnMainThreadSync(Runnable runnable, int waitTime, boolean cancel)

// "bitmap" 待处理的图片
// "radius" 图片模糊半径
// "canReuseInBitmap" 是否直接使用 "bitmap" 中进行模糊,
// "false" 情况下将拷贝 "bitmap" 的副本进行模糊
// 在"Java"中实现图片模糊
BlurKit.blur(Bitmap bitmap, int radius, boolean canReuseInBitmap);
// 在"Jni"中实现图片模糊,传给"Jni"的是图片类"Bitmap"
BlurKit.blurNatively(Bitmap bitmap, int radius, boolean canReuseInBitmap);
// 在"Jni"中实现图片模糊,传给"Jni"的是图片 "像素集合"
BlurKit.blurNativelyPixels(Bitmap bitmap, int radius, boolean canReuseInBitmap);

```


##### `animation` 模块

```java
// TouchEffectAnimator 允许给你的控件添加点击特效
// 特效类型：Press, Move, Ease, Ripple, None
public class GeniusButton extends Button {
    private TouchEffectAnimator touchEffectAnimator = null;
    // 在你的控件中初始化动画效果类
    public void initTouchEffect(TouchEffect touchEffect) {
        touchEffectAnimator = new TouchEffectAnimator(this);
        // 动画模式
        touchEffectAnimator.setTouchEffect(touchEffect);
        // 动画颜色
        touchEffectAnimator.setEffectColor("color");
        // 边缘圆弧半径
        touchEffectAnimator.setClipRadius(20);
    }
    // 用于初始化高宽等数据
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (touchEffectAnimator != null)
            touchEffectAnimator.onMeasure();
    }
    // 回调绘制方法
    @Override
    protected void onDraw(Canvas canvas) {
        if (touchEffectAnimator != null)
            touchEffectAnimator.onDraw(canvas);
        super.onDraw(canvas);
    }
    // 触发点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchEffectAnimator != null)
            touchEffectAnimator.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}

```


##### `command` 模块

```java
// 执行命令，后台服务自动控制
// 调用方式与ProcessBuilder传参方式一样
// timeout：任务超时值,可选参数
// params：执行参数，如："/system/bin/ping","-c", "4", "-s", "100","www.baidu.com"
Command command = new Command(int timeout, String... params);

// 同步方式
// 完成后结果直接返回
String result = Command.command(new Command(Command.TIMEOUT, "..."));

// 异步方式
// 结果以事件回调方式返回
Command command = new Command("...");
Command.command(command, new Command.CommandListener() {
    @Override
    public void onCompleted(String str) {
    }
    @Override
    public void onCancel() {
    }
    @Override
    public void onError(Exception e) {
    }
});

// 取消一个命令任务
Command.cancel(Command command);

// 重启 Command 服务
Command.restart();

// 销毁
// 调用 ‘Genius.dispose()’ 方法时默认调用
Command.dispose();

```


##### `net tool` 模块

```java
// Ping
// 传入域名或者IP
// 结果：是否执行成功、延时、丢包
Ping ping = new Ping("www.baidu.com");
// 开始
ping.start();
// 返回
if (ping.getError() == NetModel.SUCCEED) {}
else {}
...
其他操作与Ping类似
...

```

##### `util` 模块

```java
// ===================FixedList===================
// 固定长度队列
// 可指定长度，使用方法与普通队列类似
// 当加入元素数量达到指定数量时将弹出元素
// 头部插入尾部弹出，尾部插入头部弹出

// 初始化最大长度为5
FixedList<Integer> list = new FixedList<Integer>(5);

// 获取最大容量
list.getMaxSize();
// 调整最大长度；缩小长度时将自动删除头部多余元素
list.setMaxSize(3);

// 可使用List操作
List<Integer> list = new FixedList<Integer>(2);


// ====================HashUtils==================
// 哈希计算（Md5）
// 可计算字符串与文件Md5值

// 获取字符串MD5
String hash = HashUtils.getMD5String(String str);
// 获取文件MD5
String hash = HashUtils.getMD5String(File file);


// ======================Log======================
// 日志类
// 调用方法与使用Android Log方法一样
// 可设置其是否存储日志信息
// 可拷贝日志信息到SD卡
// 可在主界面添加事件回调，界面实时显示日志

// 添加回调
// 回调类
Log.LogCallbackListener listener = new LogCallbackListener() {
    @Override
    public void onLogArrived(Log data) {
        ...
    }
};
// 添加
Log.addCallbackListener(listener);

// 是否调用系统Android Log，可控制是否显示
Log.setCallLog(true);
// 是否开启写入文件，文件数量，单个文件大小（Mb）
// 默认存储在程序目录/Genius/Logs
Log.setSaveLog(true, 10, 1);
// 设置是否监听外部存储插入操作
// 开启：插入外部设备（SD）时，将拷贝日志文件到外部存储
// 此操作依赖于是否开启写入文件功能，未开启则此方法无效
Log.setCopyExternalStorage(true, "Test/Logs");

// 拷贝内部存储的日志文件到外部存储（SD）
// 此操作依赖于是否开启写入文件功能，未开启则此方法无效
Log.copyToExternalStorage("Test/Logs");

// 设置日志等级
// ALL(全部显示)，VERBOSE到ERROR依次递减
Log.setLevel(Log.ALL);

// 添加日志
Log.d(TAG, "DEBUG ");


// ====================Tools====================
// 常用工具包
// 全部为静态方法，以后会持续添加完善

// 休眠
Tools.sleepIgnoreInterrupt(long time);
// 拷贝文件
Tools.copyFile(File source, File target);
// AndroidId
Tools.getAndroidId(Context context);
// SN编号
Tools.getSerialNumber();

```


## 配置权限

```xml
    <!-- 网络 权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- 日志写文件 权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- getDeviceId 权限 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>

```


## 你是开发者

下载本项目,项目可导入到 `Android Studio`，Android Studio >= 1.0

项目中含有一个库以及一个测试项目，可将库导入到自己的项目中使用。

`Eclipse` 中无法直接导入项目，请先建立一个项目按照对应目录拷贝到项目中。


## 反馈

在使用中有任何问题，欢迎能及时反馈给我，可以用以下联系方式跟我交流

* 项目：[`提交Bug或想法`](https://github.com/qiujuer/Genius-Android/issues)
* 邮件：[`qiujuer@live.cn`](mailto:qiujuer@live.cn)
* QQ： `756069544`
* QQ群：[`387403637`](http://shang.qq.com/wpa/qunwpa?idkey=3f1ed8e41ed84b07775ca593032c5d956fbd8c3320ce94817bace00549d58a8f)
* Weibo： [`@qiujuer`](http://weibo.com/qiujuer)
* 网站：[`www.qiujuer.net`](http://www.qiujuer.net)


## 捐助开发者

有兴趣、写一个`免费`的东西，有欣喜，也还有汗水，希望你喜欢我的作品，同时也能支持一下。
当然，有钱捧个钱场（支付宝: `qiujuer@live.cn` ）；没钱捧个人场，谢谢各位。


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

