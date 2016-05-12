[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Genius--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1463)

[`GitHub`](https://github.com/qiujuer/Genius-Android) [`OSChina`](http://git.oschina.net/qiujuer/Genius-Android) [`中文`](README-ZH.md) [`English`](README.md)


## Genius-Android

![branchs](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/branchs.png)

**Genius-Android:** by `Material Design` style and some commonly used packages.  Starting in 2015, The divided into two branches: `STEADY` and `CAPRICE`.
* `STEADY` [END](https://github.com/qiujuer/Genius-Android/tree/steady)

* `CAPRICE` a new branch in the branch, containing `Ui` and `Kit` libraries
	> * **Ui Lib:** research and development in `Color-Drive` and `Material Design` style
	> * **Kit Lib:** Include `command`、`net`、`util`
	> * **Blur Lib:** Include `blur`
    > * **Resource Lib** Include `Material Design` Color and Lay Size

**`Note:` `STEADY` with `CAPRICE` libraries are not compatible, is completely independent branch!**

![GeniusUI](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/pic_ui.png)

## Sample APK

*  [`sample.apk`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/sample.apk)


## Video

*  [`video.mp4`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/video.mp4)


## Add Library To Your Project

`Ui` `Blur` `Resource` `Kit`

```groovy
dependencies {
  // ui module
  compile 'net.qiujuer.genius:ui:2.0.0-beta3'
  compile 'net.qiujuer.genius:res:2.0.0-beta3'
  
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


*  **Note: Kit module included kit-cmd, kit-handler, kit-reflect modules.**
*  **Note: Each module is independent and can be used independently to dependency.**



## More Documents And Develop

*  [`http://genius.qiujuer.net`](http://genius.qiujuer.net)
*  [`Wiki`](https://github.com/qiujuer/Genius-Android/wiki)



## Feedback

You in use if you have any question, please timely feedback to me, you can use the following contact information to communicate with me

* Project: [`Submit Bug or Idea`](https://github.com/qiujuer/Genius-Android/issues)
* Email: [`qiujuer@live.cn`](mailto:qiujuer@live.cn)
* QQ: `756069544`
* QQ Group: [`387403637`](http://shang.qq.com/wpa/qunwpa?idkey=3f1ed8e41ed84b07775ca593032c5d956fbd8c3320ce94817bace00549d58a8f)
* WeiBo: [`@qiujuer`](http://weibo.com/qiujuer)
* WebSit:[`www.qiujuer.net`](http://www.qiujuer.net)


## Giving developers

Are interested in and write a `free`, have joy, also there is sweat, I hope you like my work, but also can support it.
Of course, rich holds a money (AliPay: `qiujuer@live.cn`); No money holds personal field, thank you.



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
