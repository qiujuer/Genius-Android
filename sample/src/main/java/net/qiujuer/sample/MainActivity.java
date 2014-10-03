package net.qiujuer.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.util.Log;


public class MainActivity extends Activity {
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1:
                    if (mText != null)
                        mText.setText(mText.getText() + "\n" + msg.obj.toString());
                    break;
            }
        }
    };
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
            public void onLogArrived(Log data) {
                //显示到界面
                if (mHandler != null) {
                    Message msg = mHandler.obtainMessage(0x1, data.getMsg());
                    mHandler.sendMessage(msg);
                }
            }
        });

        //开始测试
        TestCase test = new TestCase();
        test.testLog();
        test.testCommand();
        test.testHashUtils();
        test.testToolUtils();
        test.testNetTool();
        test.testFixedSizeList();
    }

    @Override
    protected void onDestroy() {
        mHandler = null;
        //销毁
        Genius.dispose();
        super.onDestroy();
    }
}
