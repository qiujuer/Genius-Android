---
layout: default
title: Ui Module
id: lib-ui
root: ../
---

### Ui Module

This module provide same plate :

* `widget` include:

    * `TextView` quick set boder and boder
    * `Button` have touch press button
    * `FloatActionButton` have shadow button
    * `CheckBox` have click animation
    * `EditText` a MD line edit    
    * `ImageView` the imageview support touch press
    * `SeekBar` have move popup view seekbar
    * `Loading` loading progress widget
    
* `drawable` This is widget drawable and same abs class.
* `ui kit` It have font, color, attr and size convert.


### First 

{% highlight xml %}
// First of all specified in the root container:
<YourLayout
    ...
    xmlns:app="http://schemas.android.com/apk/res-auto"/>
{% endhighlight %}


### Widget 

#### TextView 

![Img](../assets/images/textview.png)

{% highlight xml %}
<net.qiujuer.genius.ui.widget.TextView
    ...
    android:text="Border TB"
    app:gFont="roboto.ttf"
    app:gBorder="top|bottom"
    app:gBorderColor="@color/teal_500"
    app:gBorderSize="1dp" />
{% endhighlight %}

* `gFont`: Set text font file name, the path's **"assets\fonts\roboto.ttf"**
* `gBorder`: Set show border, you can set: `all`, `top`, `top|bottom`
    * **all** 
    * **left**, **top**, **right**, **bottom**
* `gBorderColor`: Set border color, you can set: `@color/teal_500` or `#ff000000`
* `gBorderSize`: Set border size, the unit is **dp**


#### Button 

![Img](../assets/images/button.gif)

{% highlight xml %}
 <net.qiujuer.genius.ui.widget.Button
    ...
    android:background="@drawable/g_button_background"
    app:gFont="roboto.ttf"
    app:gTouchColor="@color/black_alpha_224"
    app:gTouchEffect="auto"
    app:gTouchCornerRadius="@dimen/lay_12"
    app:gTouchCornerRadiusTL="@dimen/lay_16"
    app:gTouchCornerRadiusTR="@dimen/lay_16"
    app:gTouchCornerRadiusBL="@dimen/lay_16"
    app:gTouchCornerRadiusBR="@dimen/lay_16"
    app:gTouchDurationRate="0.7" />
{% endhighlight %}

* `gFont`: See textview xml.
* `gTouchColor`: Set the touch press color, you can set: `@color/black_alpha_64` or `#80000000`
* `gTouchEffect`: Set the touch press type, include:
    * **none** Not have touch effect.
    * **auto** This effect is default by MD, spread from your touch point.
    * **ease** This is Gradually fade effect.
    * **press** The spread effect from center.
    * **ripple** This is ripple effect.
* `gTouchCornerRadius`: Set the touch effect edge corner radius, this is set four corners.
* `gTouchCornerRadiusTL`: Set the corner radius between **top** and **left**.
* `gTouchCornerRadiusTR`: Set the corner radius between **top** and **right**.
* `gTouchCornerRadiusBL`: Set the corner radius between **bottom** and **left**.
* `gTouchCornerRadiusBR`: Set the corner radius between **bottom** and **right**.
 
    > In this, if you set **gTouchCornerRadius="12dp"** and **gTouchCornerRadiusTL="16dp"**,  
    > The four corners of the rectangle are 16, 12, 12, 12, respectively.
* `gTouchDurationRate`: Set touch press speed. 
If need **fast** you can set `0.0~1.0`; if you need **slow**, you can set `1~10`
 
    > Default **1.0**, EnterDuration=**280ms**, ExitDuration=**160ms**
    
    
    
#### FloatActionButton 

![Img](../assets/images/fab.gif)

{% highlight xml %}
<net.qiujuer.genius.ui.widget.FloatActionButton
    ... 
    android:id="@+id/action_add"    
    app:gBackgroundColor="@color/cyan_500"
    app:gTouchColor="@color/black_alpha_64"/>
{% endhighlight %}

* `gBackgroundColor`: If you want change bg color, you need use it. you can set: `@color/cyan_500` or `#aa000000`
* `gTouchColor`: Set the touch press color, you can set: `@color/black_alpha_64` or `#80000000`

You can add create drawable in the src by java:

{% highlight java %}
private void initFloatActionButton() {
    final float density = getResources().getDisplayMetrics().density;
    FloatActionButton addButton = (FloatActionButton) findViewById(R.id.action_add);
    AddLineShape lineShape = new AddLineShape();
    ShapeDrawable drawable = new ShapeDrawable(lineShape);
    Paint paint = drawable.getPaint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setAntiAlias(true);
    paint.setDither(true);
    paint.setColor(0xc0ffffff);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeWidth(2 * density);
    drawable.setIntrinsicWidth(100);
    drawable.setIntrinsicHeight(100);
    addButton.setImageDrawable(drawable);
}
{% endhighlight %}

The **AddLineShape** in [this](https://github.com/qiujuer/Genius-Android/blob/master/caprice/sample/src/main/java/net/qiujuer/sample/genius/drawable/AddLineShape.java).

    
#### CheckBox

![Img](../assets/images/checkbox.gif)
 
{% highlight xml %}
<net.qiujuer.genius.ui.widget.CheckBox
    ...
    android:background="@null"
    app:gFont="roboto.ttf"
    app:gBorderSize="1dp"
    app:gIntervalSize="2dp"
    app:gMarkSize="22dp"
    app:gMarkColor="@color/m_check_box" />
{% endhighlight %}

* `gFont`: See textview xml.
* `gBorderSize`: Set ring drawable size
* `gIntervalSize`: Set ring to circle interval size
* `gMarkSize`: Set mark drawable size; if you not set, the size is **Math.min(width, hight)**
* `gMarkColor`: Set mark color, the color allow set **ColorStateList**
    * `#ff000000`
    * `@color/black_alpha_64`
    * `color drawable:`
{% highlight xml %}
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="@color/grey_400" android:state_checked="false" android:state_enabled="true" />
    <item android:color="@color/grey_300" android:state_checked="false" android:state_enabled="false" />
    <item android:color="@color/cyan_500" android:state_checked="true" android:state_enabled="true" />
    <item android:color="@color/cyan_300" android:state_checked="true" android:state_enabled="false" />
</selector>
{% endhighlight %}



#### EditText

![Img](../assets/images/edittext.gif)
 
{% highlight xml %}
<net.qiujuer.genius.ui.widget.EditText
    ...
    app:gFont="roboto.ttf"
    app:gHintTitle="slide"
    app:gHintTitlePaddingBottom="2dp"
    app:gHintTitlePaddingLeft="0dp"
    app:gHintTitlePaddingRight="0dp"
    app:gHintTitlePaddingTop="0dp"
    app:gHintTitleTextSize="14sp"
    app:gLineColor="@color/m_edit_view_line"
    app:gLineSize="2dp" />
{% endhighlight %}

* `gFont`: See textview xml
* `gHintTitle`: Set Hint title style type:
    * **none** Not hava hint
    * **slide** Slide show title, this is Deafault
* `gHintTitlePaddingBottom`: Set title padding bottom
* `gHintTitlePaddingLeft`: Set title padding left, if align left, default by TextView padding left
* `gHintTitlePaddingRight`: Set title padding right, if align right, default by TextView padding right
* `gHintTitlePaddingTop`: Set title padding top
* `gHintTitleTextSize`: Set title text size
* `gLineColor`: Set EditText bottom line color
* `gLineSize`: Set EditText bottom line size



#### ImageView
 
{% highlight xml %}
 <net.qiujuer.genius.ui.widget.ImageView
    ...
    app:gTouchColor="@color/black_alpha_224"
    app:gTouchEffect="auto"
    app:gTouchCornerRadius="@dimen/lay_12"
    app:gTouchCornerRadiusTL="@dimen/lay_16"
    app:gTouchCornerRadiusTR="@dimen/lay_16"
    app:gTouchCornerRadiusBL="@dimen/lay_16"
    app:gTouchCornerRadiusBR="@dimen/lay_16"
    app:gTouchDurationRate="0.7" />
{% endhighlight %}

* `gTouchColor`: Set the touch press color, you can set: `@color/black_alpha_64` or `#80000000`
* `gTouchEffect`: Set the touch press type, see **Button** attr, default none
* `gTouchCornerRadius`: Set the touch effect edge corner radius, this is set four corners.
* `gTouchCornerRadiusTL`: Set the corner radius between **top** and **left**.
* `gTouchCornerRadiusTR`: Set the corner radius between **top** and **right**.
* `gTouchCornerRadiusBL`: Set the corner radius between **bottom** and **left**.
* `gTouchCornerRadiusBR`: Set the corner radius between **bottom** and **right**.
* `gTouchDurationRate`: Set touch press speed. you must `>0.0`, see **Button** attr



#### SeekBar

![Img](../assets/images/seekbar.gif)

{% highlight xml %}
 <net.qiujuer.genius.ui.widget.SeekBar
    ...
    app:gFont="roboto.ttf"
    app:gMax="10"
    app:gMin="0"
    app:gValue="2" 
    
    app:gIndicator="auto"
    app:gIndicatorTextPadding="2dp"
    app:gIndicatorBackgroundColor="@color/m_seek_bar_indicator_bg"
    app:gIndicatorFormatter="(∩_∩)%d"
    app:gIndicatorSeparation="14dp"
    app:gIndicatorTextAppearance="@style/Genius.Widget.BalloonMarker.TextAppearance"
    
    app:gRippleColor="@color/m_seek_bar_ripple"
    app:gScrubberColor="@color/m_seek_bar_scrubber"
    app:gScrubberStroke="4dp"
    app:gThumbColor="@color/m_seek_bar_thumb"
    app:gThumbSize="6dp"
    app:gTickSize="3dp"
    app:gTouchSize="12dp"
    app:gTrackColor="@color/grey_500"
    app:gTrackStroke="2dp"
    
    app:gMirrorForRtl="true"
    app:gAllowTrackClickToDrag="true"/>
{% endhighlight %}

* `gFont`:  See textview xml
* `gMax`: Set the touch press type, see **Button** attr, default none
* `gMin`: Set the touch effect edge corner radius, this is set four corners.
* `gValue`: Set the corner radius between **top** and **left**.
* `gIndicator`: Set the corner radius between **top** and **right**.
* `gIndicatorTextPadding`: Set the corner radius between **bottom** and **left**.
* `gIndicatorBackgroundColor`: Set the corner radius between **bottom** and **right**.
* `gIndicatorFormatter`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gIndicatorSeparation`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gIndicatorTextAppearance`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gRippleColor`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gScrubberColor`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gScrubberStroke`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gThumbColor`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gThumbSize`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gTickSize`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gTouchSize`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gTrackColor`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gTrackStroke`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gMirrorForRtl`: Set touch press speed. you must `>0.0`, see **Button** attr
* `gAllowTrackClickToDrag`: Set touch press speed. you must `>0.0`, see **Button** attr


#### Loading

![Img](../assets/images/loading.gif)

{% highlight xml %}
 <net.qiujuer.genius.ui.widget.Loading
    ...    
    app:gBackgroundLineSize="1dp"
    app:gForegroundLineSize="3dp"
    app:gBackgroundColor="@color/yellow_500"
    app:gForegroundColor="@color/deep_orange_500"
    app:gProgressFloat="0.65"
    app:gAutoRun="false"/>
{% endhighlight %}

* `gBackgroundLineSize`:  The loading line background size
* `gForegroundLineSize`: The loading line foreground size
* `gBackgroundColor`: The loading line background color
* `gForegroundColor`: The loading line foreground color
* `gProgressFloat`: If you need hava progress set it. the value `0.0~1.0`, if you set it, the loading isn't running
* `gAutoRun`: Show the widget, auto running, default is **true**