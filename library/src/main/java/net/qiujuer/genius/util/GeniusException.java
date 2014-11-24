package net.qiujuer.genius.util;

/**
 * Created by QiuJu
 * on 2014/11/24.
 */
public class GeniusException extends RuntimeException {

    private static final long serialVersionUID = -2912559384646531479L;

    public GeniusException(String detailMessage) {
        super(detailMessage);
    }

    public GeniusException(Throwable throwable) {
        super(throwable);
    }

    public GeniusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}