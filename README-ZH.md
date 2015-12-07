[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Genius--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1463)

[`GitHub`](https://github.com/qiujuer/Genius-Android) [`OSChina`](http://git.oschina.net/qiujuer/Genius-Android) [`中文`](README-ZH.md) [`English`](README.md)


## Genius-Android

![branchs](https://raw.githubusercontent.com/qiujuer/Genius-Android/resource/images/global/06C15426.png)

**Genius-Android** ：由 `Material Design` 控件和一些常用类库组合而成。从2015年开始，划分为两个分支：`STEADY` 和 `CAPRICE`。
* `STEADY` 延续以主题驱动的风格进行研发
	> *  **app:** UiKit、BlurKit
	> * **animation:** TouchEffectAnimator
	> * **drawable:** Material Design Draw
	> * **widget:** Material Design
	> * **command:** Run Command In Process
	> * **net tool:** Ping、Dns、Telnet、Tracert
	> * **util:** Log、Hash、Tools、FixedList

* `CAPRICE` 一个新的分支，在该分支中包含 `Ui` 与 `Kit` 库
	> * **Ui Lib** 主打颜色驱动`Material Design`风格进行研发
	> * **Kit Lib** 包含：`command`、`net`、`util` 等包
	> * **Blur Lib** 包含：`StackBlr` 对图片进行模糊
  > * **Resource Lib** 包含：`Material Design` 的颜色与布局大小

**`请注意：` `STEADY` 与 `CAPRICE` 库并不兼容，是完全独立的两个分支！**

![GeniusUI](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/pic_ui.png)


## Sample APK

*  [`STEADY`](https://raw.githubusercontent.com/qiujuer/Genius-Android/resource/release/simple-steady_2.4.0.apk)
*  [`CAPRICE`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/sample.apk)


## Video

*  [`CAPRICE`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/video.mp4)


## 添加项目

### `STEADY`

```
dependencies {
  compile 'com.github.qiujuer:genius:2.4.1'
}
```

### `CAPRICE`

`Ui` `Blur` `Resource` `Kit`

```
dependencies {
  compile 'net.qiujuer.genius:ui:1.2.1'
  compile 'net.qiujuer.genius:blur:1.0.1'
  compile 'net.qiujuer.genius:res:1.0.0'
  compile 'net.qiujuer.genius:kit:1.1.0'
}
```

**提示：每个模块都是独立的，可单独依赖使用。**



## 更多说明

*  [`http://genius.qiujuer.net`](http://genius.qiujuer.net)
*  [`Wiki`](https://github.com/qiujuer/Genius-Android/wiki)


## 关于我

```javascript
  var info = {
    nickName  : "qiujuer",
    site : "http://www.qiujuer.net"
  }
```


License
--------

    Copyright 2014-2015 Qiujuer.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
