package net.qiujuer.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.util.GLog;


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

        GLog.LogCallbackListener listener = new GLog.LogCallbackListener() {
            @Override
            public void onLogArrived(GLog data) {
                //有日志写来了
                Message msg = mHandler.obtainMessage(0x1, data.getMsg());
                mHandler.sendMessage(msg);
            }
        };

        //添加回调
        GLog.addCallbackListener(listener);

        TestCase test = new TestCase(this, mHandler);
        test.testGLog();
        test.testCommand();
    }

    @Override
    protected void onDestroy() {
        Command.destroy();
        GLog.destroy();
        super.onDestroy();
    }
}
