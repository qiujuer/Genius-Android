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
* `Kit` Include **command**、**net** and same **util** class.
* `Blur`Include **blur** image by **java** and **jni** method.


#### Gradle:

`Ui` `Resource` `Blur` `Kit`

{% highlight gradle %}
dependencies {
  compile 'net.qiujuer.genius:ui:1.6.0'
  compile 'net.qiujuer.genius:res:1.5.0'
  compile 'net.qiujuer.genius:kit:1.5.0'
  compile 'net.qiujuer.genius:blur:1.5.0'
}
{% endhighlight %}

**Note: Each module is independent and can be used independently to dependency.**

#### Maven

`Ui`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>ui</artifactId>
    <version>1.6.0</version>
</dependency>
{% endhighlight %}


`Res`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>res</artifactId>
    <version>1.5.0</version>
</dependency>
{% endhighlight %}


`Kit`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit</artifactId>
    <version>1.5.0</version>
</dependency>
{% endhighlight %}


`Blur`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>blur</artifactId>
    <version>1.5.0</version>
</dependency>
{% endhighlight %}





### Developer

[Download](https://github.com/qiujuer/Genius-Android/archive/master.zip) the project, the project can be imported into `Android Studio`, Android Studio >= 2.0, SDK:23.

Project which contains a library and a test project, the library can be imported into your own project use.

`Eclipse` Cannot import directly in the program, please create a project in accordance with the corresponding category replacement to their projects.