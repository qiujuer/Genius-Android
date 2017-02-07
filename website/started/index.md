---
layout: default
title: Getting Started
id: started
root: ../
---

### Start

`CAPRICE` branch use guide. This library include modules: 

* **Resource Lib:** Include `Material Design` Color and Dimen
* **Ui Lib:** Is `Material Design` style widget, support to api-15 (4.0.3) 
* **Kit Lib:** Include `command`、`handler`、`reflect` 
* **Graphics Lib:** Include `blur` 


#### Gradle: 

`Ui` `Resource` `Graphics` `Kit`

{% highlight gradle %}
def genius_version = "2.0.0"

dependencies {
  // ui module
  compile "net.qiujuer.genius:ui:${genius_version}"
  compile "net.qiujuer.genius:res:${genius_version}"

  // use to bitmap blur and more
  compile 'net.qiujuer.genius:graphics:${genius_version}'

  // ping/telnet/tracert/dns and run cmd
  compile 'net.qiujuer.genius:kit-cmd:${genius_version}'
  // shuttle between ui-thread and child-thread
  compile 'net.qiujuer.genius:kit-handler:${genius_version}'
  // calss reflect
  compile 'net.qiujuer.genius:kit-reflect:${genius_version}'
}
{% endhighlight %}


*  **Note: Each module is independent and can be used independently to dependency.**


#### Maven

`Ui`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>ui</artifactId>
    <version>{insert genius_version}</version>
</dependency>
{% endhighlight %}


`Res`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>res</artifactId>
    <version>{insert genius_version}</version>
</dependency>
{% endhighlight %}


`Blur`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>graphics</artifactId>
    <version>{insert genius_version}</version>
</dependency>
{% endhighlight %}


`Kit Cmd`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit-cmd</artifactId>
    <version>{insert genius_version}</version>
</dependency>
{% endhighlight %}


`Kit Handler`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit-handler</artifactId>
    <version>{insert genius_version}</version>
</dependency>
{% endhighlight %}


`Kit Reflect`

{% highlight xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit-reflect</artifactId>
    <version>{insert genius_version}</version>
</dependency>
{% endhighlight %}



### Developer

[Download](https://github.com/qiujuer/Genius-Android/archive/master.zip) the project, the project can be imported into `Android Studio`, Android Studio >= 2.3, Gradle:3.3, SDK:25.

Project which contains a library and a test project, the library can be imported into your own project use.

`Eclipse` Cannot import directly in the program, please create a project in accordance with the corresponding category replacement to their projects.