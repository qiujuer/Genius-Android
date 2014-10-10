package net.qiujuer.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.qiujuer.genius.Genius;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化
        Genius.initialize(getApplication());
    }

    @Override
    protected void onDestroy() {
        //销毁
        Genius.dispose();
        super.onDestroy();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.MaterialButton)
            startActivity(new Intent(this, MaterialActivity.class));
        else
            startActivity(new Intent(this, TestCaseActivity.class));
    }
}
