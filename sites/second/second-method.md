---
layout: second
title: Second Methods - Genius-Android
id: second-method
root: ../
---

## Method of application

##### Initialization and destruction

{% highlight java %}
Genius.initialize(Application application);
Genius.dispose();

{% endhighlight %}


##### `widget` module

{% highlight xml %}
// First of all specified in the root container:
<LinearLayout
    ...
    xmlns:genius="http://schemas.android.com/apk/res-auto"/>

// The theme style: see screenshot
// Provide the font: `opensans` `roboto`
// The font size: `bold` `extrabold` `extralight` `light` `regular`

// ==================Global Attribute=================
<net.qiujuer.genius.widget.all
    ...
    genius:g_textAppearance="light"
    genius:g_fontFamily="opensans"
    genius:g_fontWeight="bold"
    genius:g_fontExtension="ttf"
    genius:g_borderWidth="5dp"
    genius:g_theme="@array/StrawberryIce"
    genius:g_cornerRadius="5dp"
    genius:g_cornerRadii_A="10dp"
    genius:g_cornerRadii_B="10dp"
    genius:g_cornerRadii_C="10dp"
    genius:g_cornerRadii_D="10dp"/>

// `g_textAppearance`: Specify the font color, the default for ` none `
// `g_fontFamily`: Specify a font of two kinds of fonts
// `g_fontWeight`: The specified font weight
// `g_fontExtension`: The font extension
// `g_borderWidth`: Border width
// `g_theme`: Specify the subject style, 17 kinds of arbitrary choice
// `g_cornerRadius`: Arc radius, default: ` 0`
// `g_cornerRadii`: Arc radius, four angles(A,B,C,D)radius, default: `0`

// ==================GeniusButton==================
<net.qiujuer.genius.widget.GeniusButton
    ...
    genius:g_delayClick="true"
    genius:g_touchEffect="move"
    genius:g_touchEffectColor="#ff4181ff"
    genius:g_blockButtonEffectHeight="10dp" />

// `g_delayClick`: Delayed response to the click event, Default "True"
// `g_touchEffect`: press, move, ease, ripple, none
// `g_touchEffectColor`: TouchEffectColor,Invalid when `g_touchEffect` is 'None'
// `g_blockButtonEffectHeight`: The button shadow height

// ==================GeniusCheckBox==================
<net.qiujuer.genius.widget.GeniusCheckBox
    ...
    genius:g_ringWidth="2dp"
    genius:g_circleRadius="22dp"
    genius:g_checked="true"
    genius:g_enabled="true" />

// `g_ringWidth`: Ring width
// `g_circleRadius`: The center of the circle radius
// `g_checked`: Is checked
// `g_enabled`: Is allow click
//  Note: If you want to change color, you should use Attributes method "setColors"
//  And call Attribute.notifyAttributeChange() method

// ==================GeniusTextView==================
<net.qiujuer.genius.widget.GeniusTextView
    ...
    genius:g_textColor="light"
    genius:g_backgroundColor="dark"
    genius:g_customBackgroundColor="#FFFFFF" />

// `g_textColor`: Font color type
// `g_backgroundColor`: Background color type
// `g_customBackgroundColor`: Background color

// ==================GeniusEditText==================
<net.qiujuer.genius.widget.GeniusEditText
    ...
    genius:g_fieldStyle="fill"
    genius:g_showTitle="true"
    genius:g_titleTextColor="#ff1fedff|statusColor"
    genius:g_titleTextSize="12sp"
    genius:g_titlePaddingTop="5dp"
    genius:g_titlePaddingLeft="5dp" />

// `g_fieldStyle`: Style: `fill` `box` `transparent` `line`
// `g_showTitle`: If show Hint Title
// `g_titleTextColor`: Title font color
// `g_titleTextSize`: Title font size
// `g_titlePaddingTop`: Title padding to top
// `g_titlePaddingLeft`: Title padding to left

// ==================GeniusSeekBar==================
<net.qiujuer.genius.widget.GeniusSeekBar
    ...
    genius:g_min="0"
    genius:g_max="100"
    genius:g_value="0"
    genius:g_tickSize="0dp"
    genius:g_thumbSize="6dp"
    genius:g_touchSize="12dp"
    genius:g_trackStroke="2dp"
    genius:g_scrubberStroke="4dp"
    genius:g_rippleColor="@color/color_value"
    genius:g_scrubberColor="@color/color_value"
    genius:g_trackColor="@color/color_value"
    genius:g_thumbColor="@color/color_value"
    genius:g_indicatorBackgroundColor="@color/color_value"
    genius:g_indicatorFormatter="%04d"
    genius:g_indicatorTextAppearance="@style/DefaultBalloonMarkerTextAppearanceStyle"
    genius:g_allowTrackClickToDrag="true"/>

// `g_indicatorBackgroundColor`: BalloonMarker background color
// `g_indicatorFormatter`: BalloonMarker show int value's Formatter
// `g_indicatorTextAppearance`: BalloonMarker text style
// `g_allowTrackClickToDrag`: Any position allows you to control drag, default: True

{% endhighlight %}

{% highlight java %}
// Code to set widget
GeniusCheckBox box = new GeniusCheckBox(this);
box.setChecked(!box.isChecked());
// Theme
CheckBoxAttributes attr = box.getAttributes();
attr.setRingWidth(4);
attr.setCircleRadius(22);
attr.setTheme(R.array.StrawberryIce, getResources());
// Or
// Color is Darker, Dark, Primary, Light, Translucence, Transparent
attr.setColors(new int[]{
               Color.parseColor("#ffc26165"), Color.parseColor("#ffdb6e77"),
               Color.parseColor("#ffef7e8b"), Color.parseColor("#fff7c2c8"),
               Color.parseColor("#ffc2cbcb"), Color.parseColor("#ffe2e7e7")});
// End
attr.notifyAttributeChange();

{% endhighlight %}


##### `app`  module

{% highlight java %}
// "Runnable" implementation method "run()"
// "run()" run in the main thread, the can interface
// Synchronization to enter the main thread, waiting for the main thread processing to continue after the completion of the subprocess
UIKit.runOnMainThreadSync(Runnable runnable);
// Asynchronous into the main thread, without waiting for
UIKit.runOnMainThreadAsync(Runnable runnable);
// Synchronously But the child thread just wait for the waitTime long
// @param runnable Runnable Interface
// @param waitTime wait for the main thread run Time
// @param cancel   on the child thread cancel the runnable task
UIKit.runOnMainThreadSync(Runnable runnable, int waitTime, boolean cancel)

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

{% endhighlight %}


##### `animation` 模块

{% highlight java %}
// TouchEffectAnimator Allowed to add click on special effects to your control
// Types: Press, Move, Ease, Ripple, None
public class GeniusButton extends Button {
    private TouchEffectAnimator touchEffectAnimator = null;
    // Initialize
    public void initTouchEffect(TouchEffect touchEffect) {
        touchEffectAnimator = new TouchEffectAnimator(this);
        // Set model
        touchEffectAnimator.setTouchEffect(touchEffect);
        // Set Color
        touchEffectAnimator.setEffectColor("color");
        // Set this clip radius
        touchEffectAnimator.setClipRadius(20);
        // Same as above, Set up four vertices radian
        touchEffectAnimator.setClipRadii(new float[]{20,20,20,20,20,20,20,20});
        // Set animation time factor, you need to set up setTouchEffect() after the call
        touchEffectAnimator.setAnimDurationFactor(1);
    }
    // Delay click event (optional)
    @Override
    public boolean performClick() {
        if (touchEffectAnimator != null) {
            return !touchEffectAnimator.interceptClick() && super.performClick();
        } else
            return super.performClick();
    }
    // Callback onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        if (touchEffectAnimator != null)
            touchEffectAnimator.onDraw(canvas);
        super.onDraw(canvas);
    }
    // Callback onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchEffectAnimator != null)
            touchEffectAnimator.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}

{% endhighlight %}


##### `command` module

{% highlight java %}
// Execute the command, the background service automatic control
// The same way call way and the ProcessBuilder mass participation
// Timeout: Task timeout, optional parameters
// Params: executing params,such as: "/system/bin/ping","-c", "4", "-s", "100","www.baidu.com"
Command command = new Command(int timeout, String... params);

// Synchronous
// After the completion of the results returned directly
String result = Command.command(new Command(Command.TIMEOUT, "..."));

// Asynchronous
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

// Destroy
// Using 'Genius.dispose()' method is run this destroy
Command.dispose();

{% endhighlight %}


##### `net tool` module

{% highlight java %}
// Ping
// Introduced to the domain name or IP
// Result: delay, packet loss
Ping ping = new Ping("www.baidu.com");
// Start
ping.start();
// Return
if (ping.getError() == NetModel.SUCCEED) {}
else {}
...
Others similarly
...

{% endhighlight %}

##### `util` module

{% highlight java %}
// ===================FixedList===================
// Fixed length queue
// Can specify length, using methods similar to ordinary queue
// When join the element number to a specified number elements will pop up
// Insert the tail pop-up head, tail insertion head pops up

// Initialize the maximum length of 5
FixedList<Integer> list = new FixedList<Integer>(5);

// To obtain the maximum capacity
list.getMaxSize();
// Adjust the maximum length; Narrow length will be automatically deleted when the head redundant elements
list.setMaxSize(3);

// Using List to operation
List<Integer> list1 = new FixedList<Integer>(2);


// ====================HashUtils==================
// Hash to calculate(Md5)
// String with the file can be calculated Md5 value

// Get the MD5
String hash = HashUtils.getMD5String(String str);
// Access to the file MD5
String hash = HashUtils.getMD5String(File file);


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
// Adding
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

// Copies of internal storage log files to external storage(SD)
// This operation depends on whether written to the file open function, not open, this method is invalid
Log.copyToExternalStorage("Test/Logs");

// Set the log level
// ALL(show all)，VERBOSE to ERROR decreasing
Log.setLevel(Log.ALL);

// Add log
Log.d(TAG, "DEBUG ");


// ====================Tools====================
// Commonly used toolkit
// Are all static methods, later will continue to add

// Thread sleep
Tools.sleepIgnoreInterrupt(long time);
// Copy the files
Tools.copyFile(File source, File target);
// AndroidId
Tools.getAndroidId(Context context);
// SN Id
Tools.getSerialNumber();

{% endhighlight %}

