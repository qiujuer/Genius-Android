package net.qiujuer.genius.kit.handler;

import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by qiujuer
 * on 16/4/19.
 */
class ActionAsyncRunnable implements Action, Runnable {
    private final Action mAction;

    ActionAsyncRunnable(Action action) {
        mAction = action;
    }

    @Override
    public void run() {
        call();
    }

    @Override
    public void call() {
        mAction.call();
    }
}
