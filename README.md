[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Genius--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1463)

[`GitHub`](https://github.com/qiujuer/Genius-Android) [`OSChina`](http://git.oschina.net/qiujuer/Genius-Android) [`中文`](README-ZH.md) [`English`](README.md)


## Genius-Android

![branchs](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/branchs.png)

**Genius-Android:** by `Material Design` style and some commonly used packages.  Starting in 2015, The divided into two branches: `STEADY` and `CAPRICE`.
* `STEADY` [END](https://github.com/qiujuer/Genius-Android/tree/steady)

* `CAPRICE` is new branch, containing `Ui`, `Graphics`, `Kit` libraries 
  > * **Resource Lib:** Include `Material Design` Color and Dimen
	> * **Ui Lib:** Is `Material Design` style widget, support to api-15 (4.0.3) 
	> * **Kit Lib:** Include `command`、`handler`、`reflect` 
	> * **Graphics Lib:** Include `blur` 


**`Note:` `STEADY` with `CAPRICE` libraries are not compatible, is completely independent branch!**

![GeniusUI](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/pic_ui.png)

## Sample APK

*  [`sample.apk`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/sample.apk)


## Video

*  [`video.mp4`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/video.mp4)


## Add Library To Your Project

`Ui` `Resource` `Graphics` `Kit`

```groovy
dependencies {
  // ui module
  compile 'net.qiujuer.genius:ui:2.0.0'
  compile 'net.qiujuer.genius:res:2.0.0'
  
  // use to bitmap blur and more
  compile 'net.qiujuer.genius:graphics:2.0.0'
  
  // ping/telnet/tracert/dns and run cmd
  compile 'net.qiujuer.genius:kit-cmd:2.0.0'
  // shuttle between ui-thread and child-thread
  compile 'net.qiujuer.genius:kit-handler:2.0.0'
  // calss reflect
  compile 'net.qiujuer.genius:kit-reflect:2.0.0'
}
```

*  **Note: Each module is independent and can be used independently to dependency.**



## More Documents And Develop

*  [`http://genius.qiujuer.net`](http://genius.qiujuer.net)
*  [`Wiki`](https://github.com/qiujuer/Genius-Android/wiki)



## Feedback

If you submit question, please:

* Project: [`Submit Bug or Idea`](https://github.com/qiujuer/Genius-Android/issues)
* Email: [`qiujuer@live.cn`](mailto:qiujuer@live.cn)
* QQ: `756069544`
* QQ Group: [`387403637`](http://shang.qq.com/wpa/qunwpa?idkey=3f1ed8e41ed84b07775ca593032c5d956fbd8c3320ce94817bace00549d58a8f)
* WeiBo: [`@qiujuer`](http://weibo.com/qiujuer)
* WebSit:[`www.qiujuer.net`](http://www.qiujuer.net)


## Giving developers

Used AliPay sponsor me: `qiujuer@live.cn`



## About me

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
