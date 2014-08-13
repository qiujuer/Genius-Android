// ICommandInterface.aidl
package net.qiujuer.libraries.genius.command;

// Declare any non-default types here with import statements

interface ICommandInterface {
     void killSelf();
     String command(String params);
}
