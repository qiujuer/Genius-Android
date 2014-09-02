// ICommandInterface.aidl
package net.qiujuer.genius;

// Declare any non-default types here with import statements

interface ICommandInterface {
    //杀死服务自身
     void killSelf();
     //执行命令返回执行结果
     String command(String params);
}
