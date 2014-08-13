package net.qiujuer.libraries.genius.journal;

import java.util.List;

/**
 * Created by Genius on 2014/8/13.
 * 程序界面接口
 */
public interface LogsInterface {

    public void OnShowLogListener(LogsData data);

    public void OnAddedLogListener(List<LogsData> dataList);
}
