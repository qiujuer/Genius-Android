---
layout: default
title: Blur Module
id: lib-blur
root: ../
---

### Blur Module

![Img](../assets/images/blur.gif)

{% highlight java %}
// "bitmap" is to be processed images
// "radius" is picture is fuzzy radius
// "canReuseInBitmap" Whether directly using the "bitmap" fuzzy,
// "false" will copy the "bitmap" to doing fuzzy
// Java blur
StackBlur.blur(Bitmap bitmap, int radius, boolean canReuseInBitmap);
// Jni blur, To the Jni is a kind of Bitmap images
StackBlur.blurNatively(Bitmap bitmap, int radius, boolean canReuseInBitmap);
// Jni blur, To the Jni is image collection "pixel"
StackBlur.blurNativelyPixels(Bitmap bitmap, int radius, boolean canReuseInBitmap);
{% endhighlight %}