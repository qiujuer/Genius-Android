package net.qiujuer.sample.genius;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.SeekBar;
import net.qiujuer.genius.ui.widget.TextView;

public class SeekBarActivity extends AppCompatActivity {
    TextView mStatus;
    SeekBar mBar;
    EditText mMin, mMax;
    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_bar);

        mMin = (EditText) findViewById(R.id.edit_min);
        mMax = (EditText) findViewById(R.id.edit_max);
        mStatus = (TextView) findViewById(R.id.tv_status);
        mBar = (SeekBar) findViewById(R.id.seekBar);
        mBtn = (Button) findViewById(R.id.btn);

        if (mBar != null) {
            mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    showStatus();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min = Integer.valueOf(mMin.getText().toString());
                mBar.setMin(min);

                int max = Integer.valueOf(mMax.getText().toString());
                mBar.setMax(max);

                showStatus();
            }
        });

        showStatus();
    }

    void showStatus() {
        mStatus.setText(String.format("SeekBar: Min:%s, Max:%s, Progress:%s", mBar.getMin(), mBar.getMax(), mBar.getProgress()));
    }
}
