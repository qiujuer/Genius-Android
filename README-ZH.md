![License](https://img.shields.io/github/license/qiujuer/Genius-Android.svg)
![License](https://img.shields.io/github/stars/qiujuer/Genius-Android.svg)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Genius--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1463)
[![Download](https://api.bintray.com/packages/qiujuer/maven/net.qiujuer.genius%3Aui/images/download.svg)](https://bintray.com/qiujuer/maven/net.qiujuer.genius%3Aui/_latestVersion)

[`GitHub`](https://github.com/qiujuer/Genius-Android) [`OSChina`](http://git.oschina.net/qiujuer/Genius-Android) [`中文`](README-ZH.md) [`English`](README.md)


## Genius-Android

![branchs](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/branchs.png)

**Genius-Android** ：是 `Material Design` 控件和一些常用类库组合而成。从2015年开始，划分为两个分支：`STEADY` 和 `CAPRICE`。
* `STEADY` [END](https://github.com/qiujuer/Genius-Android/tree/steady)

* `CAPRICE` 一个新的分支，在该分支中包含 `Ui` 与 `Kit` 库
  > * **Resource Lib** 包含：`Material Design` 的颜色与布局大小
	> * **Ui Lib** 包含`Material Design`风格的主要控件，最低适配API-15 (4.0.3)
	> * **Kit Lib** 包含：`command`、`handler`、`reflect`  等包
	> * **Graphics Lib** 包含：`StackBlr` 图片模糊处理

**`请注意：` `STEADY` 与 `CAPRICE` 库并不兼容，是完全独立的两个分支！**

![GeniusUI](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/pic_ui.png)


## Sample APK

*  [`sample.apk`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/sample.apk)


## Video

*  [`video.mp4`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/video.mp4)


## 添加项目

`Ui` `Resource` `Graphics` `Kit`

```groovy
dependencies {
  // ui module
  api 'net.qiujuer.genius:ui:2.2.0'
  api 'net.qiujuer.genius:res:2.2.0'
  
  // use to bitmap blur and more
  api 'net.qiujuer.genius:graphics:2.2.0'
  
  // ping/telnet/tracert/dns and run cmd
  api 'net.qiujuer.genius:kit-cmd:2.2.0'
  // shuttle between ui-thread and child-thread
  api 'net.qiujuer.genius:kit-handler:2.2.0'
  // calss reflect
  api 'net.qiujuer.genius:kit-reflect:2.2.0'
}
```


*  **提示：每个模块都是独立的，可单独依赖使用。**



## 更多说明

*  [`http://genius.qiujuer.net`](http://genius.qiujuer.net)
*  [`Wiki`](https://github.com/qiujuer/Genius-Android/wiki)



## 反馈

你可以通过如下方式反馈给我:

* 项目：[`提交Bug或想法`](https://github.com/qiujuer/Genius-Android/issues)
* 邮件：[`qiujuer@live.cn`](mailto:qiujuer@live.cn)
* QQ： `756069544`
* QQ群：[`387403637`](http://shang.qq.com/wpa/qunwpa?idkey=3f1ed8e41ed84b07775ca593032c5d956fbd8c3320ce94817bace00549d58a8f)
* Weibo： [`@qiujuer`](http://weibo.com/qiujuer)
* 网站：[`www.qiujuer.net`](http://www.qiujuer.net)



## 关于我

```javascript
  var info = {
    nickName  : "qiujuer",
    site : "http://www.qiujuer.net"
  }
```



License
--------

    Copyright 2014-2021 Qiujuer.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
