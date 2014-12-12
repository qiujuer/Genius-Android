package net.qiujuer.genius.app;

/**
 * Created by QiuJu
 * on 2014/11/24.
 */
final class SyncPost {
    boolean end = false;
    Runnable runnable;

    SyncPost(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        if (!end) {
            synchronized (this) {
                if (!end) {
                    runnable.run();
                    end = true;
                    try {
                        this.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void waitRun() {
        if (!end) {
            synchronized (this) {
                if (!end) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void waitRun(int time, boolean cancel) {
        if (!end) {
            synchronized (this) {
                if (!end) {
                    try {
                        this.wait(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (!end && cancel)
                            end = true;
                    }
                }
            }
        }
    }
}
