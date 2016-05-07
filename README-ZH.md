[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Genius--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1463)

[`GitHub`](https://github.com/qiujuer/Genius-Android) [`OSChina`](http://git.oschina.net/qiujuer/Genius-Android) [`中文`](README-ZH.md) [`English`](README.md)


## Genius-Android

![branchs](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/branchs.png)

**Genius-Android** ：由 `Material Design` 控件和一些常用类库组合而成。从2015年开始，划分为两个分支：`STEADY` 和 `CAPRICE`。
* `STEADY` [END](https://github.com/qiujuer/Genius-Android/tree/steady)

* `CAPRICE` 一个新的分支，在该分支中包含 `Ui` 与 `Kit` 库
	> * **Ui Lib** 主打颜色驱动`Material Design`风格进行研发
	> * **Kit Lib** 包含：`command`、`net`、`util` 等包
	> * **Blur Lib** 包含：`StackBlr` 对图片进行模糊
    > * **Resource Lib** 包含：`Material Design` 的颜色与布局大小

**`请注意：` `STEADY` 与 `CAPRICE` 库并不兼容，是完全独立的两个分支！**

![GeniusUI](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/pic_ui.png)


## Sample APK

*  [`sample.apk`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/sample.apk)


## Video

*  [`video.mp4`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/video.mp4)


## 添加项目

`Ui` `Blur` `Resource` `Kit`

```groovy
dependencies {
  // ui module
  compile 'net.qiujuer.genius:ui:2.0.0-beta2'
  compile 'net.qiujuer.genius:res:2.0.0-beta2'
  
  // blur module
  compile 'net.qiujuer.genius:blur:2.0.0-beta2'
  
  // kit module
  compile 'net.qiujuer.genius:kit:2.0.0-beta2'
  // or
  compile 'net.qiujuer.genius:kit-cmd:2.0.0-beta2'
  compile 'net.qiujuer.genius:kit-handler:2.0.0-beta2'
  compile 'net.qiujuer.genius:kit-reflect:2.0.0-beta2'
}
```


*  **提示：Kit 包含了 kit-cmd、kit-handler、kit-reflect。**
*  **提示：每个模块都是独立的，可单独依赖使用。**



## 更多说明

*  [`http://genius.qiujuer.net`](http://genius.qiujuer.net)
*  [`Wiki`](https://github.com/qiujuer/Genius-Android/wiki)



## 反馈

在使用中有任何问题，欢迎能及时反馈给我，可以用以下联系方式跟我交流

* 项目：[`提交Bug或想法`](https://github.com/qiujuer/Genius-Android/issues)
* 邮件：[`qiujuer@live.cn`](mailto:qiujuer@live.cn)
* QQ： `756069544`
* QQ群：[`387403637`](http://shang.qq.com/wpa/qunwpa?idkey=3f1ed8e41ed84b07775ca593032c5d956fbd8c3320ce94817bace00549d58a8f)
* Weibo： [`@qiujuer`](http://weibo.com/qiujuer)
* 网站：[`www.qiujuer.net`](http://www.qiujuer.net)



## 捐助我

有兴趣的情况下、写一个`免费`的东西，有欣喜，也还有汗水，希望你喜欢我的作品，同时也能支持一下。
当然，有钱捧个钱场（支付宝: `qiujuer@live.cn` ），没钱捧个人场，谢谢！



## 关于我

```javascript
  var info = {
    nickName  : "qiujuer",
    site : "http://www.qiujuer.net"
  }
```



License
--------

    Copyright 2014-2016 Qiujuer.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
