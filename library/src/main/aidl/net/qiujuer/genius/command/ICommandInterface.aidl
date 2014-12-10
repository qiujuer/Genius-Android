// ICommandInterface.aidl
package net.qiujuer.genius.command;

// Declare any non-default types here with import statements

interface ICommandInterface {
    String command(String id, int timeout, String params);
    void cancel(String id);
    int getTaskCount();
}
