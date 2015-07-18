package net.qiujuer.sample.genius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.drawable.RipAnimDrawable;
import net.qiujuer.genius.ui.drawable.RipDrawable;

import java.util.Random;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int color = getRandomColor();

        View topLay = findViewById(R.id.lay_main_top);
        RipAnimDrawable ripAnim = new RipAnimDrawable();
        ripAnim.setColor(color);
        ripAnim.setFluCount(0, 0, 0, 36);
        topLay.setBackgroundDrawable(ripAnim);

        View topText = findViewById(R.id.txt_main_top);
        RipDrawable rip = new RipDrawable();
        rip.setColor(color);
        rip.setAlpha(192);
        rip.setFluCount(16, 0, 16, 0);
        rip.setDeepness(5, 16);
        rip.setSmooth(false);
        topText.setBackgroundDrawable(rip);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_blur) {
            Intent intent = new Intent(MainActivity.this, BlurActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_kit) {
            Intent intent = new Intent(MainActivity.this, KitActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private Random mRandom = new Random();

    private int getRandomColor() {
        int[] colors = Resource.Color.COLORS;
        int index = mRandom.nextInt(colors.length - 2);
        return colors[index + 1];
    }
}
