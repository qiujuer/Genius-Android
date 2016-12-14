package net.qiujuer.sample.genius;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.qiujuer.genius.ui.drawable.RipAnimDrawable;
import net.qiujuer.genius.ui.widget.CheckBox;
import net.qiujuer.genius.ui.widget.FloatActionButton;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.genius.ui.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initToolbar();
        initFloatActionButton();
        initCheckBox();
        initLoading();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "OnClickListener.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "OnLongClickListener.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_add) {
            Toast.makeText(v.getContext(), "FlaotActionButton OnClick.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_blur) {
            Intent intent = new Intent(MainActivity.this, BlurActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_kit) {
            Intent intent = new Intent(MainActivity.this, KitActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_seekBar) {
            Intent intent = new Intent(MainActivity.this, SeekBarActivity.class);
            startActivity(intent);
        }

        return true;
    }

    private void initToolbar() {
        // ToolBar
        RipAnimDrawable ripAnim = new RipAnimDrawable();
        ripAnim.setColor(getResources().getColor(R.color.cyan_600));
        ripAnim.setFluCount(0, 0, 0, 36);

        Toolbar toolbar = (Toolbar) findViewById(R.id.title);
        toolbar.setBackgroundDrawable(ripAnim);
        toolbar.setTitle(getTitle());
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);
    }

    private void initFloatActionButton() {
        FloatActionButton addButton = (FloatActionButton) findViewById(R.id.action_add);
        addButton.setOnClickListener(this);
    }

    private void initCheckBox() {
        final CheckBox none_a = (CheckBox) findViewById(R.id.checkbox_none_a);
        final CheckBox none_b = (CheckBox) findViewById(R.id.checkbox_none_b);
        final CheckBox custom_a = (CheckBox) findViewById(R.id.checkbox_custom_a);
        final CheckBox custom_b = (CheckBox) findViewById(R.id.checkbox_custom_b);
        none_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                none_b.setEnabled(isChecked);
            }
        });

        custom_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                custom_b.setEnabled(isChecked);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void initLoading() {
        final Loading loading = (Loading) findViewById(R.id.loading_progress);
        loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loading l = (Loading) v;
                if (l.isRunning())
                    l.stop();
                else
                    l.start();
            }
        });

        TextView tv = (TextView) findViewById(R.id.txt_loading);
        LoadingDrawable drawable = new LoadingCircleDrawable();
        Resources resources = getResources();

        drawable.setBackgroundColor(resources.getColor(R.color.g_default_base_background));
        drawable.setForegroundColor(resources.getIntArray(R.array.g_default_loading_fg));
        drawable.setBackgroundLineSize(2);
        drawable.setForegroundLineSize(4);

        tv.setBackgroundDrawable(drawable);
        drawable.start();
    }
}
