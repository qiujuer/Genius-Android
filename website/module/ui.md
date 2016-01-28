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
    * `Loading` loading progress widget
    * `SeekBar` have move popup view seekbar
    
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