package com.xyw.smartlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.PdReadWriteDataActivity;
import com.xyw.smartlock.R;


public class ReadWriteStateActivity extends Activity implements View.OnClickListener {

    private TextView tv_read_write_title;
    private TextView tv_read_write_state;
    private ImageView cancel_read_write_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_write_data);

        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            boolean read_write_state = intent.getBooleanExtra(PdReadWriteDataActivity.READ_WRITE_STATE, false);
            String title = intent.getStringExtra(PdReadWriteDataActivity.READ_WRITE_TITLE);
            if (title != null) {
                tv_read_write_title.setText(title);
            }
            if (read_write_state) {
                if (title.equals(getString(R.string.close_rom_read_write))) {
                    setTextView(getString(R.string.close_rom_read_write_success), getResources().getColor(R.color.zhuangtai_true), R.drawable.text_bg_success);
                } else if (title.equals(getString(R.string.open_rom_read_write))) {
                    setTextView(getString(R.string.open_rom_read_write_success), getResources().getColor(R.color.zhuangtai_true), R.drawable.text_bg_success);
                }
            } else {
                if (title.equals(getString(R.string.close_rom_read_write))) {
                    setTextView(getString(R.string.close_rom_read_write_false), getResources().getColor(R.color.red), R.drawable.text_bg_success);
                } else if (title.equals(getString(R.string.open_rom_read_write))) {
                    setTextView(getString(R.string.open_rom_read_write_false), getResources().getColor(R.color.red), R.drawable.text_bg_faile);
                }
            }
        }
    }

    private void setTextView(String text, int color, int bg) {
        tv_read_write_state.setText(text);
        tv_read_write_state.setTextColor(color);
        tv_read_write_state.setBackgroundResource(bg);
    }

    private void initView() {
        tv_read_write_title = (TextView) findViewById(R.id.tv_read_write_title);
        tv_read_write_state = (TextView) findViewById(R.id.tv_read_write_state);
        cancel_read_write_state = (ImageView) findViewById(R.id.cancel_read_write_state);

        cancel_read_write_state.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_read_write_state:
                ReadWriteStateActivity.this.finish();
                break;
        }
    }
}
