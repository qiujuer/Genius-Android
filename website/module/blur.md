---
layout: default
title: Blur Module
id: lib-blur
root: ../
---

### Blur Module

![Img](../assets/images/blur.gif)

**First you need:**
{% highlight java %}
// import
import net.qiujuer.genius.graphics.Blur;
// source
Bitmap overlay = mBitmap.copy(mBitmap.getConfig(), true);
{% endhighlight %}

**You have three ways to process the picture**
* 1.Java
{% highlight java %}
overlay = Blur.onStackBlurJava(overlay, (int) radius);
{% endhighlight %}

* 2.Pixels JNI Native
{% highlight java %}
int w = overlay.getWidth();
int h = overlay.getHeight();
int[] pix = new int[w * h];
overlay.getPixels(pix, 0, w, 0, 0, w, h);
{% endhighlight %}

* 3.Jni Blur **(Strongly recommend)**
{% highlight java %}
pix = Blur.onStackBlurPixels(pix, w, h, (int) radius);
overlay.setPixels(pix, 0, w, 0, 0, w, h);
// Bitmap JNI Native
overlay = Blur.onStackBlur(overlay, (int) radius);
{% endhighlight %}


#### Big Picture

**If you want to blur a big picture, I suggest you use this:**
{% highlight java %}
// used:
Blur.onStackBlurClip(mSrc, 50);
// code:
Bitmap onStackBlurClip(Bitmap original, int radius);
{% endhighlight %}
**This method will split up a big picture and blur it one by one.**


#### Note:

* Big Picture: **Blur.onStackBlurClip(Bitmap original, int radius);**
* Radius must be between 1 and 255


#### Sample:
* [BlurActivity](https://github.com/qiujuer/Genius-Android/blob/master/caprice/app/src/main/java/net/qiujuer/sample/genius/BlurActivity.java)
* [BlurClipActivity](https://github.com/qiujuer/Genius-Android/blob/master/caprice/app/src/main/java/net/qiujuer/sample/genius/BlurClipActivity.java)