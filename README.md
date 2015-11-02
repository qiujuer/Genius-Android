[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Genius--Android-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1463)

[`GitHub`](https://github.com/qiujuer/Genius-Android) [`OSChina`](http://git.oschina.net/qiujuer/Genius-Android) [`中文`](README-ZH.md) [`English`](README.md)


## Genius-Android

![branchs](https://raw.githubusercontent.com/qiujuer/Genius-Android/resource/images/global/06C15426.png)

**Genius-Android:** by `Material Design` style and some commonly used packages.  Starting in 2015, The divided into two branches: `STEADY` and `CAPRICE`.
* `STEADY` will continue to research and development in a `Theme-Drive` style
	> *  **app:** UiKit、BlurKit
	> * **animation:** TouchEffectAnimator
	> * **drawable:** Material Design Draw
	> * **widget:** Material Design
	> * **command:** Run Command In Process
	> * **net tool:** Ping、Dns、Telnet、Tracert
	> * **util:** Log、Hash、Tools、FixedList

* `CAPRICE` a new branch in the branch, containing `Ui` and `Kit` libraries
	> * **Ui Lib:** research and development in `Color-Drive` and `Material Design` style
	> * **Kit Lib:** Include `command`、`net`、`util`
	> * **Blur Lib:** Include `blur`
  > * **Resource Lib** Include `Material Design` Color and Lay Size

**`Note:` `STEADY` with `CAPRICE` libraries are not compatible, is completely independent branch!**

![GeniusUI](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/pic_ui.png)

## Sample APK

*  [`STEADY`](https://raw.githubusercontent.com/qiujuer/Genius-Android/resource/release/simple-steady_2.4.0.apk)
*  [`CAPRICE`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/sample.apk)


## Video

*  [`CAPRICE`](https://raw.githubusercontent.com/qiujuer/Genius-Android/master/caprice/release/video.mp4)


## Add Library To Your Project

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
  compile 'net.qiujuer.genius:ui:1.1.1'
  compile 'net.qiujuer.genius:blur:1.0.1'
  compile 'net.qiujuer.genius:res:1.0.0'
  compile 'net.qiujuer.genius:kit:1.0.0'
}
```

**Note: Each module is independent and can be used independently to dependency.**



## More Documents And Develop

*  [`http://genius.qiujuer.net`](http://genius.qiujuer.net)
*  [`Wiki`](https://github.com/qiujuer/Genius-Android/wiki)


## About me

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
