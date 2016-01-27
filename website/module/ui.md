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
<net.qiujuer.genius.ui.widget.TextView
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
