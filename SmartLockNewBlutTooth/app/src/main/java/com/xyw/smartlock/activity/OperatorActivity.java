package com.xyw.smartlock.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.ActivityUtils;

import java.util.Calendar;

public class OperatorActivity extends Activity {
    private Button unlockrecord_button1;
    private Button unlockrecord_button2;
    private TextView unlockrecord_startDate;
    private TextView unlockrecord_endDate;
    private TextView user;
    private static final int RESULT_OK = 0;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mYear1;
    private int mMonth1;
    private int mDay1;
    private String unLock_startDate, unLock_endDate;
    private String number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_dialog);
        TextView customdialog_title = (TextView) findViewById(R.id.operator__customdialog_title);
        customdialog_title.setText(R.string.choose_date);
        unlockrecord_button1 = (Button) findViewById(R.id.operator_button1);
        unlockrecord_button2 = (Button) findViewById(R.id.operator_button2);
        unlockrecord_startDate = (TextView) findViewById(R.id.operator_textview1);
        unlockrecord_endDate = (TextView) findViewById(R.id.operator_textview2);
        user = (TextView) findViewById(R.id.operator_quer_user);

        ActivityUtils.getInstance().setTextUnderLine(unlockrecord_startDate);
        ActivityUtils.getInstance().setTextUnderLine(unlockrecord_endDate);
//                unlockrecord_startDate.setInputType(InputType.TYPE_NULL);// 关闭软键盘
//                unlockrecord_endDate.setInputType(InputType.TYPE_NULL);// 关闭软键盘
        unlockrecord_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OperatorActivity.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });

        unlockrecord_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OperatorActivity.this, mDateSetListener1, mYear1, mMonth1, mDay1).show();
            }
        });

        startDate();
        endDate();
        unlockrecord_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String strUnLock_startDate = unlockrecord_startDate.getText().toString().trim();
                String strUnStartTime = "00:00:00";
                unLock_startDate = strUnLock_startDate + "%20" + strUnStartTime.trim();
                System.out.println("unLock_startDate=" + unLock_startDate);
                String strUnLock_endDate = unlockrecord_endDate.getText().toString().trim();
                String strUnEndTime = "23:59:59";
                unLock_endDate = strUnLock_endDate + "%20" + strUnEndTime;
                System.out.println("unLock_endDate=" + unLock_endDate);

                Intent intent = new Intent();
                intent.putExtra("start_date", unLock_startDate);
                intent.putExtra("end_date", unLock_endDate);
                intent.putExtra("number", number);
                OperatorActivity.this.setResult(RESULT_OK, intent);
                // 关闭Activity
                OperatorActivity.this.finish();
            }
        });
        unlockrecord_button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("start_date", "CANCEL");
                intent.putExtra("end_date", "CANCEL");
                OperatorActivity.this.setResult(RESULT_OK, intent);
                // 关闭Activity
                OperatorActivity.this.finish();
            }
        });
    }

    /**
     * 设置开始日期
     */
    private void startDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatstart();
    }

    /**
     * 设置结束日期
     */
    private void endDate() {
        final Calendar c = Calendar.getInstance();
        mYear1 = c.get(Calendar.YEAR);
        mMonth1 = c.get(Calendar.MONTH);
        mDay1 = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplatend();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplatstart() {
        unlockrecord_startDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    private void updateDateDisplatend() {
        unlockrecord_endDate.setText(new StringBuilder().append(mYear1).append("-")
                .append((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1) : (mMonth1 + 1)).append("-")
                .append((mDay1 < 10) ? "0" + mDay1 : mDay1));
    }

    /**
     * 日期控件事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplatstart();
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear1 = year;
            mMonth1 = monthOfYear;
            mDay1 = dayOfMonth;
            updateDateDisplatend();
        }
    };

    @Override
    public void finish() {
        super.finish();
    }

    public void toSelectUser(View view) {
        startActivityForResult(new Intent(OperatorActivity.this, SelectUserrActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            user.setText(data.getStringExtra("username"));
            number = data.getStringExtra("usernumber");
        }
    }

}
