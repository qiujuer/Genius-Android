package net.qiujuer.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.app.UiModel;
import net.qiujuer.genius.app.UiTool;
import net.qiujuer.genius.util.Log;


public class MainActivity extends Activity {
    TextView mText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.text);
        //初始化
        Genius.initialize(getApplication());

        //添加回调
        Log.addCallbackListener(new Log.LogCallbackListener() {
            @Override
            public void onLogArrived(final Log data) {
                //异步显示到界面
                UiTool.asyncRunOnUiThread(MainActivity.this, new UiModel() {
                    @Override
                    public void doUi() {
                        if (mText != null)
                            mText.append("\n" + data.getMsg());
                    }
                });
            }
        });

        //开始测试
        TestCase test = new TestCase();
        test.testLog();
        test.testCommand();
        test.testHashUtils();
        test.testToolUtils();
        test.testNetTool();
        test.testFixedList();
    }

    @Override
    protected void onDestroy() {
        mText = null;
        //销毁
        Genius.dispose();
        super.onDestroy();
    }
}
