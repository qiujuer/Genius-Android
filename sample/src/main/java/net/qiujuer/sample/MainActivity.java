package net.qiujuer.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import net.qiujuer.genius.Genius;


public class MainActivity extends ActionBarActivity {
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
        else if (view.getId() == R.id.Other)
            startActivity(new Intent(this, TestCaseActivity.class));
        else if (view.getId() == R.id.Blur)
            startActivity(new Intent(this, BlurActivity.class));
    }
}
