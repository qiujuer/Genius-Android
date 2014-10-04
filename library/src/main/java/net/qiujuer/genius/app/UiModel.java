package net.qiujuer.genius.app;

/**
 * Created by QiuJu
 * on 2014/10/3.
 */
public abstract class UiModel {
    protected boolean end = false;
    protected final Object object = new Object();

    public abstract void doUi();
}
