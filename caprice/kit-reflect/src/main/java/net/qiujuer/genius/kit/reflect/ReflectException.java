package net.qiujuer.genius.kit.reflect;

/**
 * Created by qiujuer
 * on 16/4/16.
 */
public class ReflectException extends RuntimeException {

    private static final long serialVersionUID = 312038727504126519L;

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException() {
        super();
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }
}