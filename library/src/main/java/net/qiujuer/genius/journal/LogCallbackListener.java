package net.qiujuer.genius.journal;

/**
 * Created by Genius on 2014/8/13.
 * 程序界面接口
 */
public interface LogCallbackListener {

    public void OnLogArrived(LogData data);

    public void OnListenerAdded(LogData[] dataArray);
}
