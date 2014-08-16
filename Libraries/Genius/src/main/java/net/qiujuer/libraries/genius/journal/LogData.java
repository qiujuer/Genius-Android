package net.qiujuer.libraries.genius.journal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * Created by Genius on 2014/8/16.
 * 日志信息格式化存储
 */
public class LogData {
    private static final SimpleDateFormat Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat FormatterSimple = new SimpleDateFormat("HH:mm:ss");
    private String FormatterStr = null;
    private String FormatterSimpleStr = null;
    private java.util.Date Date;
    private int Level;
    private String Tag;
    private String Msg;

    public LogData(int level, String tag, String msg) {
        this(new Date(), level, tag, msg);
    }

    public LogData(Date date, int level, String tag, String msg) {
        Date = date;
        Level = level;
        Tag = tag;
        Msg = msg;
    }

    public String toString() {
        if (FormatterStr == null)
            FormatterStr = (new java.util.Formatter().format("[%s][%s] %s:%s \r\n", Formatter.format(Date), Level, Tag, Msg)).toString();
        return FormatterStr;
    }

    public String toStringSimple() {
        if (FormatterSimpleStr == null)
            FormatterSimpleStr = (new Formatter().format("[%s][%s] %s:%s \r\n", FormatterSimple.format(Date), Level, Tag, Msg)).toString();
        return FormatterSimpleStr;
    }

    public String getDate() {
        return Formatter.format(Date);
    }

    public String getDateSimple() {
        return FormatterSimple.format(Date);
    }

    public int getLevel() {
        return Level;
    }

    public String getTag() {
        return Tag;
    }

    public String getMsg() {
        return Msg;
    }
}

