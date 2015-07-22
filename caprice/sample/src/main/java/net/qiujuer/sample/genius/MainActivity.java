package net.qiujuer.sample.genius;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.drawable.RipAnimDrawable;
import net.qiujuer.genius.ui.drawable.RipDrawable;
import net.qiujuer.genius.ui.widget.FloatActionButton;
import net.qiujuer.sample.genius.drawable.AddLineShape;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View topLay = findViewById(R.id.lay_main_top);
        RipAnimDrawable ripAnim = new RipAnimDrawable();
        ripAnim.setColor(getResources().getColor(R.color.cyan_600));
        ripAnim.setFluCount(0, 0, 0, 36);
        topLay.setBackgroundDrawable(ripAnim);
        topLay.setOnClickListener(this);

        View topText = findViewById(R.id.txt_main_top);
        RipDrawable rip = new RipDrawable();
        rip.setColor(Resource.Color.WHITE);
        rip.setAlpha(128);
        rip.setFluCount(16, 0, 16, 0);
        rip.setDeepness(5, 16);
        rip.setSmooth(false);
        topText.setBackgroundDrawable(rip);


        final float density = getResources().getDisplayMetrics().density;
        FloatActionButton addButton = (FloatActionButton) findViewById(R.id.action_add);
        AddLineShape lineShape = new AddLineShape();
        ShapeDrawable drawable = new ShapeDrawable(lineShape);
        Paint paint = drawable.getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xc0ffffff);
        paint.setStrokeWidth(2 * density);
        drawable.setIntrinsicWidth(100);
        drawable.setIntrinsicHeight(100);
        addButton.setImageDrawable(drawable);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lay_main_top) {
            RipAnimDrawable drawable = (RipAnimDrawable) v.getBackground();
            drawable.startAnim();
        }
    }
}
