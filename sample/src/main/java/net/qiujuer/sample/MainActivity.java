package net.qiujuer.sample;

import android.app.Activity;
import android.os.Bundle;

import net.qiujuer.genius.command.Command;
import net.qiujuer.genius.util.GLog;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GLog.destroy();
        TestCase test = new TestCase(this);
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
