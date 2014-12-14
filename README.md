[`中文`](README-ZH.md) [`English`](README.md)

##What is Genius-Android?

**Genius-Android** is Some of the commonly used method in **Android** collection, **Genius** library provide five basic plate :

`app`（**Ui**） `material`（**widget**） `command`（**command line**） `net tool`（**Ping、Dns...**） `util`（**common method,class**）


## Screenshots

##### MaterialButton
![MaterialButton](art/material.gif)

##### Themes
![Themes](art/themes.png)

##### BlurKit
![BlurKit](art/blur.png)


## Function modules

* `app`
  > *  `ToolKit` support the child thread `synchronization` `asynchronous` switching to the main thread
  > *  `BlurKit` support `Java` `Jni` use the `StackBlur` algorithm fuzzy images

* `material`
  > *  Fonts `opensans` `roboto`
  > *  Colors `none` `dark` `light`
  > *  Controls `MaterialButton`

* `command`
  > *  Independent service command-line work process execution
  > *  Similar to the `ProcessBuilder` operation
  > *  Intelligent correct operation, solve the operation problem
  > *  One key start and cancel the operation, control of freedom
  > *  Can be synchronous and asynchronous execution, the callback event

* `net tool`
  > *  One key `Ping` `DNS` `TelNet` `TraceRoute`
  > *  Can be controlled, can be cancelled;Don't need to care about the details
  > *  Concurrent routing tasks, can be in around 40 s testing is completed

* `util`
  > *  `AppContext` Global variables, access convenient and quick
  > *  `HashUtils` String with the file `MD5`
  > *  `ID` `SN` Determine the device unique identifier
  > *  `Log` Such as system Log as simple to use, one key switch
  > *  `Log` Can store the log to a file, convenient analysis errors
  > *  `Log` You can add event listeners, convenient interface display log information
  > *  `FixedList` Fixed-length queue, automatic pop-up, keep the queue number


## Get library

* `Star` and `Fork` this project.
* `release` folder with `*.jar` or `*.aar` files can be imported in your project
  *  `*.jar` unable to use a control resources, Such as font and `R..`
  *  `*.aar` can use all of the classes and controls as well as the font, etc
  *  `*.aar` locally introduction methods:
* `Eclipse` [EclipseImport](docs/EclipseImport.md)
* `Android Studio` :
  *  `*.aar` The local import:
  
  ```gradle
  // needing copy "genius_0.9.0.aar" to "libs" contents
  android {
      repositories {
          flatDir { dirs 'libs' }
      }
  }
  dependencies {
      compile (name:'genius_0.9.0', ext:'aar')
  }

  ```

  *  `*.aar` `MavenCentral` remote import:
  
  ```gradle
  // Adding to the project named "build.gradle"
  // Don't need to copy any file, waiting for networking updates finish can be used
  dependencies {
      compile 'com.github.qiujuer:genius:0.9.0'
  }

  ```


## Update Log 

* Version：0.9.0
* Date：2014-11-26 22:40
* Log：[Notes](docs/NOTES.md)


## Method of application

##### Initialization and destruction

```java
Genius.initialize(Application application);
Genius.dispose();

```


##### `app`  module

```java
// "Runnable" implementation method "run()"
// "run()" run in the main thread, the can interface
// Synchronization to enter the main thread, waiting for the main thread processing to continue after the completion of the subprocess
ToolKit.runOnMainThreadSync(Runnable runnable);
// Asynchronous into the main thread, without waiting for
ToolKit.runOnMainThreadAsync(Runnable runnable);

// "bitmap" is to be processed images
// "radius" is picture is fuzzy radius
// "canReuseInBitmap" Whether directly using the "bitmap" fuzzy,
// "false" will copy the "bitmap" to doing fuzzy
// Java blur
BlurKit.blur(Bitmap bitmap, int radius, boolean canReuseInBitmap);
// Jni blur, To the Jni is a kind of Bitmap images
BlurKit.blurNatively(Bitmap bitmap, int radius, boolean canReuseInBitmap);
// Jni blur, To the Jni is image collection "pixel"
BlurKit.blurNativelyPixels(Bitmap bitmap, int radius, boolean canReuseInBitmap);

```


##### `material` module

```xml
// First of all specified in the root container:
<LinearLayout
    ...
    xmlns:material="http://schemas.android.com/apk/res-auto"/>

// The theme style: see screenshot
// Provide the font: `opensans` `roboto`
// The font size: `bold` `extrabold` `extralight` `light` `regular`

// ==================MaterialButton==================
<net.qiujuer.genius.material.MaterialButton
    ...
    material:gm_textAppearance="light"
    material:gm_fontFamily="opensans"
    material:gm_fontWeight="bold"
    material:gm_isMaterial="true"
    material:gm_isAutoMove="true"
    material:gm_theme="@array/grass" />

// `gm_textAppearance`: Specify the font color, the default for ` none `
// `gm_fontFamily`: Specify a font of two kinds of fonts
// `gm_fontWeight`: The specified font weight
// `gm_isMaterial`: Whether to open the Material animation, the default ` true `
// `gm_isAutoMove`: Animation is automatically moved to the center, the default ` true `
// After open the animation will not place spread, click ` XY ` coordinates will be closer to the center
// ` gm_theme ` : specify the subject style, 12 kinds of arbitrary choice

```


##### `command` module

```java
// Execute the command, the background service automatic control
// The same way call way and the ProcessBuilder mass participation
// timeout: Task timeout, optional parameters
// params: executing params,such as: "/system/bin/ping","-c", "4", "-s", "100","www.baidu.com"
Command command = new Command(int timeout, String... params);

// synchronization method
// After the completion of the results returned directly
String result = Command.command(new Command(Command.TIMEOUT, "..."));

// asynchronous mode
// Results to event callback method returns
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

// To cancel a task command
Command.cancel(Command command);

// Restart the Command service
Command.restart();

// destroy
// using 'Genius.dispose()' method is run this destroy
Command.dispose();

```


##### `net tool` module

```java
// Ping
// Introduced to the domain name or IP
// result: Whether successful, delay, packet loss
Ping ping = new Ping("www.baidu.com");
// start
ping.start();
// return
if (ping.getError() == NetModel.SUCCEED) {
    ...
} else {
    ...
}
...
Others similarly
...

```

##### `util` module

```java
// ===================FixedList===================
// Fixed length queue
// Can specify length, using methods similar to ordinary queue
// When join the element number to a specified number elements will pop up
// Insert the tail pop-up head, tail insertion head pops up

// Initialize the maximum length of 5
FixedList<Integer> list = new FixedList<Integer>(5);
// The Queue method is used to add elements
list.offer(0);
// The List method is used to add elements, two ways of operation is the same
list.add(1);
// At the end of the insert element and add the same
list.addLast(1);
// From the head insert, delete the tail beyond element by default
list.addFirst(19);
// Add a list
list.addAll(new ArrayList<Integer>());

// To obtain the maximum capacity
list.getMaxSize();
// Adjust the maximum length; Narrow length will be automatically deleted when the head redundant elements
list.setMaxSize(3);

// Using poll pop-up elements
int i = list.poll();
// Remove a similar poll, but don't return to remove elements, only delete an element
list.remove();
// clear
list.clear();

// using List to operation
List<Integer> list1 = new FixedList<Integer>(2);
list1.add(1);
list1.clear();


// ====================HashUtils==================
// Hash to calculate（Md5）
// String with the file can be calculated Md5 value

// Get the MD5
String hash = HashUtils.getStringMd5(String str);
// Access to the file MD5
String hash = HashUtils.getFileMd5(File file);


// ======================Log======================
// Log class
// Calls the method with using Android as the default method
// Can be set if the store log information
// Can copy the log information to SD card
// Can add the event callback in the main interface, the interface, real-time display the log

// Add a callback
// The callback class
Log.LogCallbackListener listener = new LogCallbackListener() {
    @Override
    public void onLogArrived(Log data) {
        ...
    }
};
// adding
Log.addCallbackListener(listener);

// Whether Android call system Log, can control whether to display
Log.setCallLog(true);
// Is open to a file, the file number, a single file size (Mb)
// The default is stored in the application directory is /Genius/Logs
Log.setSaveLog(true, 10, 1);
// Set whether to monitor external storage inserts
// Open: insert an external device (SD), will copy the log files to external storage
// This operation depends on whether written to the file open function, not open, this method is invalid
Log.setCopyExternalStorage(true, "Test/Logs");

// Copies of internal storage log files to external storage（SD）
// This operation depends on whether written to the file open function, not open, this method is invalid
Log.copyToExternalStorage("Test/Logs");

// Set the log level
// ALL(show all)，VERBOSE to ERROR decreasing
Log.setLevel(Log.ALL);
Log.setLevel(Log.INFO);

// add log
Log.v(TAG, "log VERBOSE ");
Log.d(TAG, "log DEBUG ");
Log.i(TAG, "log INFO ");
Log.w(TAG, "log WARN ");
Log.e(TAG, "log ERROR ");


// ====================ToolUtils====================
// Commonly used toolkit
// Are all static methods, later will continue to add

// dormant
ToolUtils.sleepIgnoreInterrupt(long time);
// copy the files
ToolUtils.copyFile(File source, File target);
// AndroidId
ToolUtils.getAndroidId(Context context);
// SN Id
ToolUtils.getSerialNumber();

```


## Permission 


```xml
    <!-- Internet permission -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- Log file permission -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- getDeviceId permission -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    
```


## Developer

Download the project, the project can be imported into `Android Studio`, Android Studio >= 1.0

Project which contains a library and a test project, the library can be imported into your own project use.

'Eclipse' Cannot import directly in the program, please create a project in accordance with the corresponding category replacement to their projects.


## Feedback

You in use if you have any question, please timely feedback to me, you can use the following contact information to communicate with me

* Project: [submit Bug or idea](https://github.com/qiujuer/Genius-Android/issues)
* Email: [qiujuer@live.cn](mailto:qiujuer@live.cn)
* QQ: 756069544
* WeiBo: [@qiujuer](http://weibo.com/qiujuer)
* WebSit:[www.qiujuer.net](http://www.qiujuer.net)


## Giving developers

Are interested in and write a `free`, have joy, also there is sweat, I hope you like my work, but also can support it.
Of course, rich holds a money (AliPay: ` qiujuer@live.cn `); No money holds personal field, thank you.


## About me

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

