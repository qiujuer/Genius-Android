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
    app:gBorder="top|bottom"
    app:gBorderColor="@color/teal_500"
    app:gBorderSize="1dp" />
{% endhighlight %}