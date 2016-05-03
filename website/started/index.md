---
layout: default
title: Getting Started
id: started
root: ../
---

### Start

`CAPRICE` branch use guide. This library include modules: 

* `Ui` Research and development in **Color-Drive** and **Material Design** style.
* `Resource` Include Material Design **Color** and Lay **Size**.
* `Blur`Include **blur** image by **java** and **jni** method.
* `Kit` Include **kit-cmd**、**kit-handler** and **kit-reflect** class.


#### Gradle:

`Ui` `Resource` `Blur` `Kit`

{% highlight gradle %}
dependencies {
  // ui module
  compile 'net.qiujuer.genius:ui:2.0.0-beta2'
  compile 'net.qiujuer.genius:res:2.0.0-beta2'

  // blur module
  compile 'net.qiujuer.genius:blur:2.0.0-beta2'

  // kit module
  compile 'net.qiujuer.genius:kit:2.0.0-beta2'
  // or
  compile 'net.qiujuer.genius:kit-cmd:2.0.0-beta2'
  compile 'net.qiujuer.genius:kit-handler:2.0.0-beta2'
  compile 'net.qiujuer.genius:kit-reflect:2.0.0-beta2'
}
{% endhighlight %}

**Note: 'Kit' module included ‘kit-cmd’, ‘kit-handler‘, 'kit-reflect’ modules. **

**Note: Each module is independent and can be used independently to dependency.**


#### Maven

`Ui`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>ui</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}


`Res`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>res</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}


`Blur`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>blur</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}


`Kit`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}


`Kit Cmd`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit-cmd</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}


`Kit Handler`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit-handler</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}


`Kit Reflect`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit-reflect</artifactId>
    <version>2.0.0-beta2</version>
</dependency>
{% endhighlight %}



### Developer

[Download](https://github.com/qiujuer/Genius-Android/archive/master.zip) the project, the project can be imported into `Android Studio`, Android Studio >= 2.0, SDK:23.

Project which contains a library and a test project, the library can be imported into your own project use.

`Eclipse` Cannot import directly in the program, please create a project in accordance with the corresponding category replacement to their projects.