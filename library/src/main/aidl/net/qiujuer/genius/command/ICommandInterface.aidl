// ICommandInterface.aidl
package net.qiujuer.genius.command;

// Declare any non-default types here with import statements

interface ICommandInterface {
    String command(String id, String params);
    void cancel(String id);
    void dispose();
    int getTaskCount();
}
