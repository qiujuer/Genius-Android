---
layout: default
title: Getting Started
id: started
root: ../
---

### Start

This is `CAPRICE` branch use guide.

#### Gradle:

`Ui` `Blur` `Resource` `Kit`

{% prism java %}
dependencies {
  compile 'net.qiujuer.genius:ui:1.5.0'
  compile 'net.qiujuer.genius:res:1.5.0'
  compile 'net.qiujuer.genius:kit:1.5.0'
  compile 'net.qiujuer.genius:blur:1.5.0'
}
{% endprism %}

**Note: Each module is independent and can be used independently to dependency.**

#### Maven

`Ui`

{% prism xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>ui</artifactId>
    <version>1.5.0</version>
</dependency>
{% endprism %}


`Res`

{% prism xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>res</artifactId>
    <version>1.5.0</version>
</dependency>
{% endprism %}


`Kit`

{% prism xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>kit</artifactId>
    <version>1.5.0</version>
</dependency>
{% endprism %}


`Blur`

{% prism xml %}
<dependency>
    <groupId>net.qiujuer.genius</groupId>
    <artifactId>blur</artifactId>
    <version>1.5.0</version>
</dependency>
{% endprism %}





### Developer

[Download](https://github.com/qiujuer/Genius-Android/archive/master.zip) the project, the project can be imported into `Android Studio`, Android Studio >= 2.0, SDK:23.

Project which contains a library and a test project, the library can be imported into your own project use.

`Eclipse` Cannot import directly in the program, please create a project in accordance with the corresponding category replacement to their projects.