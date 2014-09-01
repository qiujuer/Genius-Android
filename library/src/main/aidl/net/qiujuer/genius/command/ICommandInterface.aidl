// ICommandInterface.aidl
package net.qiujuer.genius.command;

// Declare any non-default types here with import statements

interface ICommandInterface {
     void killSelf();
     String command(String params);
}
