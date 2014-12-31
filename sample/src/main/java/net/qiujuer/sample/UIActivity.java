package net.qiujuer.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import net.qiujuer.genius.widget.GeniusCheckBox;


public class UIActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

/*        final GeniusCheckBox box1 = (GeniusCheckBox) findViewById(R.id.checkbox1);
        GeniusCheckBox box2 = (GeniusCheckBox) findViewById(R.id.checkbox2);

        box2.setOnCheckedChangeListener(new GeniusCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(GeniusCheckBox checkBox, boolean isChecked) {
                box1.setEnabled(isChecked);
            }
        });*/
    }
}
