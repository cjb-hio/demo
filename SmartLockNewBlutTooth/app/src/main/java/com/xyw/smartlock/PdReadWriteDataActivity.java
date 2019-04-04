package com.xyw.smartlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.activity.ReadWriteStateActivity;
import com.xyw.smartlock.utils.ActivityUtils;
import com.xyw.smartlock.utils.DemoApplication;
import com.xyw.smartlock.utils.LockUtil;
import com.xyw.smartlock.utils.NfcUtils;
import com.xyw.smartlock.view.MessageDialog;

import java.io.UnsupportedEncodingException;


public class PdReadWriteDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_lock_data_back, iv_lock_data_connect_state;
    private EditText tv_lock_data;
    private Button btn_write_lock_data, btn_clear_lock_data, btn_read_lock_data;
    private Button btn_close_lock_data, btn_open_lock_data;

    public static final String XIA_ZHUANG_STATE = "xia_zhuang_state";
    public static final String READ_WRITE_STATE = "read_write_state";
    public static final String READ_WRITE_TITLE = "read_write_title";

    private byte[] data;
    private byte[] currentData;
    private int currentIndex = 0;

    //dialog
    private MessageDialog mMessageDialog;
    private View messageView;
    private TextView tv_dialog_message;
    private ImageView cancel_dialog_message;

    private DemoApplication demoApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.getInstance().setWindowStatusBarColor(PdReadWriteDataActivity.this, R.color.status_bar_color);
        setContentView(R.layout.activity_lock_data);
        getSupportActionBar().hide();
        demoApplication = (DemoApplication) getApplication();

        initView();
        initData();
        initDialog();
    }

    private void initData() {
        demoApplication = (DemoApplication) getApplication();
        keyValue = "0810151308107781";
    }

    private BroadcastReceiver upbtimg = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "UPBTIMG":
                    iv_lock_data_connect_state.setImageResource(R.mipmap.bluet);
                    break;
                case "UPBTIMG_DIS":
                    iv_lock_data_connect_state.setImageResource(R.mipmap.bluef);
                    break;
            }
        }
    };

    private void initDialog() {
        messageView = getLayoutInflater().inflate(R.layout.message_dialog, null);
        mMessageDialog = new MessageDialog(PdReadWriteDataActivity.this, R.style.custom_dialog, messageView);
        mMessageDialog.setCanceledOnTouchOutside(false);
        cancel_dialog_message = (ImageView) messageView.findViewById(R.id.cancel_dialog_message);
        tv_dialog_message = (TextView) messageView.findViewById(R.id.tv_dialog_message);
        cancel_dialog_message.setOnClickListener(this);
    }

    private void initView() {
        iv_lock_data_back = (ImageView) findViewById(R.id.iv_lock_data_back);
        tv_lock_data = (EditText) findViewById(R.id.tv_lock_data);
        btn_write_lock_data = (Button) findViewById(R.id.btn_write_lock_data);
        btn_clear_lock_data = (Button) findViewById(R.id.btn_clear_lock_data);
        btn_read_lock_data = (Button) findViewById(R.id.btn_read_lock_data);
        iv_lock_data_connect_state = (ImageView) findViewById(R.id.iv_lock_data_connect_state);

        btn_close_lock_data = (Button) findViewById(R.id.btn_close_lock_data);
        btn_open_lock_data = (Button) findViewById(R.id.btn_open_lock_data);

        if (demoApplication.getConnect() == 1) {
            iv_lock_data_connect_state.setImageResource(R.mipmap.bluet);
        } else {
            iv_lock_data_connect_state.setImageResource(R.mipmap.bluef);
        }

        iv_lock_data_back.setOnClickListener(this);
        btn_read_lock_data.setOnClickListener(this);
        btn_write_lock_data.setOnClickListener(this);
        btn_clear_lock_data.setOnClickListener(this);
        btn_close_lock_data.setOnClickListener(this);
        btn_open_lock_data.setOnClickListener(this);
    }


    private boolean isRead = false;
    private boolean isOpenData = false;
    private boolean isAlreadyOpenData = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_lock_data_back:
                PdReadWriteDataActivity.this.finish();
                break;
            case R.id.btn_read_lock_data:
                if (demoApplication.getConnect() == 1) {
                    if (isAlreadyOpenData) {
                        btn_read_lock_data.setClickable(false);
                        isRead = true;
                        readData();
                    } else {
                        Toast.makeText(PdReadWriteDataActivity.this, "请打开读写功能", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PdReadWriteDataActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_write_lock_data:
                if (demoApplication.getConnect() == 1) {
                    if (isAlreadyOpenData) {
                        String str = tv_lock_data.getText().toString().trim();
                        if (str.length() != 0) {
                            if (isRead) {
                                tv_lock_data.setText("");
                                isRead = false;
                            } else {
                                subBegin = 0;
                                subEnd = 24;
                                sendData(20);
//                                writeData();
                                btn_write_lock_data.setClickable(false);
                            }
                        } else {
                            Toast.makeText(PdReadWriteDataActivity.this, "写入数据不能为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PdReadWriteDataActivity.this, "请打开读写功能", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PdReadWriteDataActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear_lock_data:
                if (demoApplication.getConnect() == 1) {

                } else {
                    Toast.makeText(PdReadWriteDataActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_dialog_message:
                dismissMessageDialog();
                break;
            case R.id.btn_close_lock_data:
                if (demoApplication.getConnect() == 1) {
                    isOpenData = false;
                    demoApplication.ReadIdBegin(mHandler);
                } else {
                    Toast.makeText(PdReadWriteDataActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_open_lock_data:
                if (demoApplication.getConnect() == 1) {
                    isOpenData = true;
                    demoApplication.ReadIdBegin(mHandler);
                } else {
                    Toast.makeText(PdReadWriteDataActivity.this, R.string.disconnect_ble_device, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void readData() {
        demoApplication.bleReadData(currentCount, mHandler);
    }

    private void showMessageDialog(String message, int textColor, int background) {
        tv_dialog_message.setText(message);
        tv_dialog_message.setTextColor(textColor);
        tv_dialog_message.setBackgroundResource(background);
        mMessageDialog.show();
    }

    private void dismissMessageDialog() {
        mMessageDialog.dismiss();
    }

    private String dataStr;

    private String[] dataStrs;
    private String lineData;
    private byte[] lineDataBytes;
    private int lineDataLength;
    private int dataCounts;
    private int dataCurrentCount = 0;
    private int lineCurrentCount = 0;

    private void writeData() {
        dataCurrentCount = 0;
        String str = tv_lock_data.getText().toString().replaceAll(" ", "");
        dataStrs = str.split("\n");
        dataCounts = dataStrs.length;
        writeLineData();
    }

    private void writeLineData() {
        lineCurrentCount = 0;
        lineData = dataStrs[dataCurrentCount++];
        try {
            lineDataBytes = LockUtil.getInstance().getBytesFromChs(lineData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        lineDataLength = LockUtil.getInstance().bytes2HexString(lineDataBytes).length();
        dataZ = lineDataLength / 24;
        dataY = lineDataLength % 24;
        StringBuffer sb = new StringBuffer();
        if (dataZ == 0) {
            sb.append(LockUtil.getInstance().bytes2HexString(lineDataBytes));
            while (sb.toString().length() < 24) {
                sb.append("0");
            }
            currentStrData = sb.toString();
            demoApplication.bleWriteData(currentCount, currentStrData, mHandler);
            dataY = 0;
            currentStrData = "";
        } else {
            currentStrData = LockUtil.getInstance().bytes2HexString(lineDataBytes).substring(subBegin, subEnd);
            demoApplication.bleWriteData(currentCount, currentStrData, mHandler);
            currentStrData = "";
        }
    }

    /**
     * @param dataLength 每次传输最大数据长度，不能超过20
     */
    private void sendData(int dataLength) {
//        if (currentIndex == data.length) {
//            currentIndex = 0;
//            toast("数据写入成功");
//            return;
//        }
//        byte[] surplusData = new byte[data.length - currentIndex];
//        System.arraycopy(data, currentIndex, surplusData, 0, data.length - currentIndex);
//        if (surplusData.length < dataLength) {
//            currentData = new byte[surplusData.length];
//            System.arraycopy(surplusData, 0, currentData, 0, surplusData.length);
//            currentIndex += surplusData.length;
//        } else {
//            currentData = new byte[dataLength];
//            System.arraycopy(data, currentIndex, currentData, 0, dataLength);
//            currentIndex += dataLength;
//        }
        dataStr = tv_lock_data.getText().toString().replaceAll("\n", "").replaceAll(" ", "");

        int dataLenght = dataStr.length();
        dataZ = dataLenght / 24;
        dataY = dataLenght % 24;
        StringBuffer sb = new StringBuffer();
        if (dataZ == 0) {
            sb.append(dataStr);
            while (sb.toString().length() < 24) {
                sb.append("0");
            }
            currentStrData = sb.toString();
            demoApplication.bleWriteData(currentCount, currentStrData, mHandler);
            dataY = 0;
            currentStrData = "";
        } else {
            currentStrData = dataStr.substring(subBegin, subEnd);
            demoApplication.bleWriteData(currentCount, currentStrData, mHandler);
            currentStrData = "";
        }
    }

    private int currentCount = 0;
    private int subBegin = 0;
    private int subEnd = 24;
    private String currentStrData = null;
    private int dataZ;
    private int dataY;
    private StringBuffer readSb = new StringBuffer();

    private byte[] lockSafe = new byte[4];//存放锁设备返回安全码
    private byte[] lockId = new byte[8];//存放锁设备返回锁ID
    private String keyValue;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    if (msg.obj != null) {
                        byte[] lock_data = (byte[]) msg.obj;
                        byte[] b_lock_state = {lock_data[12]};
                        byte[] b_lock_power = {lock_data[13]};
                        for (int i = 0; i < 8; i++) {
                            lockId[i] = lock_data[i];
                        }
                        for (int j = 0; j < 4; j++) {
                            lockSafe[j] = lock_data[j + 8];
                        }
                        if (isOpenData) {
                            demoApplication.bleOpenWriteRead(lockId, lockSafe, keyValue, mHandler);
                        } else {
                            demoApplication.bleCloseWriteRead(lockId, lockSafe, keyValue, mHandler);
                        }
                    }
                    break;
                case 21:
                    readLockByteData(msg);
//                    readData(msg);
                    break;
                case 22:
                    writeLockByteData(msg);
//                    writeLockData(msg);
                    break;
                case 24:
                    boolean closeResult = (boolean) msg.obj;
                    Intent intentClose = new Intent(PdReadWriteDataActivity.this, ReadWriteStateActivity.class);
                    intentClose.putExtra(PdReadWriteDataActivity.READ_WRITE_TITLE, getString(R.string.open_rom_read_write));
                    if (closeResult) {
                        isAlreadyOpenData = true;
                        NfcUtils.getInstance().starSuccessMedia();
                        intentClose.putExtra(PdReadWriteDataActivity.READ_WRITE_STATE, true);
                    } else {
                        NfcUtils.getInstance().startFaileMedia();
                        intentClose.putExtra(PdReadWriteDataActivity.READ_WRITE_STATE, false);
                    }
                    startActivity(intentClose);
                    break;
                case 23:
                    boolean openResult = (boolean) msg.obj;
                    Intent intentOpen = new Intent(PdReadWriteDataActivity.this, ReadWriteStateActivity.class);
                    intentOpen.putExtra(PdReadWriteDataActivity.READ_WRITE_TITLE, getString(R.string.close_rom_read_write));
                    if (openResult) {
                        isAlreadyOpenData = false;
                        NfcUtils.getInstance().starSuccessMedia();
                        intentOpen.putExtra(PdReadWriteDataActivity.READ_WRITE_STATE, true);
                    } else {
                        NfcUtils.getInstance().startFaileMedia();
                        intentOpen.putExtra(PdReadWriteDataActivity.READ_WRITE_STATE, false);
                    }
                    startActivity(intentOpen);
                    break;
            }
        }
    };

    private void readLockByteData(Message msg) {
        byte[] readResult = (byte[]) msg.obj;
        if (readResult != null) {
            if (readResult.length > 0) {
                readSb.append(currentCount + " : " + LockUtil.getInstance().bytes2HexString(readResult) + "\n");
                tv_lock_data.setText(readSb.toString());
            }
//                        btn_read_lock_data.setClickable(true);
            currentCount++;
            if (currentCount < 10) {
                demoApplication.bleReadData(currentCount, mHandler);
            } else {
//                            Toast.makeText(PdReadWriteDataActivity.this, "读数据完成", Toast.LENGTH_SHORT).show();
                showMessageDialog("读取数据完成", getResources().getColor(R.color.zhuangtai_true), R.drawable.text_bg_success);
                btn_read_lock_data.setClickable(true);
                currentCount = 0;
                readSb.delete(0, readSb.length());
            }
        } else {
            demoApplication.bleReadData(currentCount, mHandler);
            Log.e("LockDataActivity", "handleMessage: readResult == null");
        }
    }

    private void writeLockByteData(Message msg) {
        boolean writeResult = (boolean) msg.obj;
        if (writeResult) {
            currentStrData = "";
            currentCount++;
            if (currentCount < 10) {
                Log.e("LockDataActivity", "handleMessage: subBegin = " + subBegin);
                Log.e("LockDataActivity", "handleMessage: subEnd = " + subEnd);
                if (dataZ > 1) {
                    if (currentCount < dataZ) {
                        subEnd += 24;
                        subBegin += 24;
                        currentStrData = dataStr.substring(subBegin, subEnd);
                    } else if (dataZ == currentCount) {
                        subBegin += 24;
                        subEnd += dataY;
                        currentStrData = dataStr.substring(subBegin, subEnd);
                        dataY = 0;
                    }
                } else if (dataZ == 1) {
                    if (dataY != 0) {
                        subBegin += 24;
                        subEnd += dataY;
                        currentStrData = dataStr.substring(subBegin, subEnd);
                        dataY = 0;
                    }
                }
                StringBuffer sb = new StringBuffer();
                sb.append(currentStrData);
                while (sb.toString().length() < 24) {
                    sb.append("0");
                }
                demoApplication.bleWriteData(currentCount, sb.toString(), mHandler);
                currentStrData = "";
            } else {
//                            toast("发送完成");
                showMessageDialog("数据传输成功", getResources().getColor(R.color.zhuangtai_true), R.drawable.text_bg_success);
                currentCount = 0;
                btn_write_lock_data.setClickable(true);
            }
        } else {
            demoApplication.bleWriteData(currentCount, currentStrData, mHandler);
        }
    }

    private void readData(Message msg) {
        byte[] readResult = (byte[]) msg.obj;
        if (readResult != null) {
            if (readResult.length > 0) {
                String rr = LockUtil.getInstance().getChsFromHex(readResult);
                readSb.append(currentCount + " : " + rr + "\n");
//                readSb.append(currentCount + " : " + LockUtil.getInstance().bytes2HexString(readResult) + "\n");
                tv_lock_data.setText(readSb.toString());
            }
            currentCount++;
            if (currentCount < 10) {
                demoApplication.bleReadData(currentCount, mHandler);
            } else {
//                Toast.makeText(PdReadWriteDataActivity.this, "读数据完成", Toast.LENGTH_SHORT).show();
                showMessageDialog("读取数据完成", getResources().getColor(R.color.zhuangtai_true), R.drawable.text_bg_success);
                btn_read_lock_data.setClickable(true);
                currentCount = 0;
                readSb.delete(0, readSb.length());
            }
        } else {
            demoApplication.bleReadData(currentCount, mHandler);
            Log.e("LockDataActivity", "handleMessage: readResult == null");
        }
    }

    /**
     * 写中文、英文数据
     * @param msg
     */
    private void writeLockData(Message msg) {
        boolean writeResult = (boolean) msg.obj;
        if (writeResult) {
            currentStrData = "";
            currentCount++;
            lineCurrentCount++;
            if (currentCount < 10) {
                Log.e("LockDataActivity", "writeLockData: dataCurrentCount = " + dataCurrentCount);
                if (dataZ == 0 && dataCurrentCount != dataCounts) {
                    writeLineData();
                } else {
                    if (dataZ > 1) {
                        if (lineCurrentCount < dataZ) {
                            subEnd += 24;
                            subBegin += 24;
                            currentStrData = LockUtil.getInstance().bytes2HexString(lineDataBytes).substring(subBegin, subEnd);
                        } else if (dataZ == lineCurrentCount) {
                            subBegin += 24;
                            subEnd += dataY;
                            currentStrData = LockUtil.getInstance().bytes2HexString(lineDataBytes).substring(subBegin, subEnd);
                            dataY = 0;
                            dataZ = 0;
                            subBegin = 0;
                            subEnd = 24;
                            lineCurrentCount = 0;
                        }
                    } else if (dataZ == 1) {
                        if (dataY != 0) {
                            subBegin += 24;
                            subEnd += dataY;
                            currentStrData = LockUtil.getInstance().bytes2HexString(lineDataBytes).substring(subBegin, subEnd);
                            dataY = 0;
                            dataZ = 0;
                            subBegin = 0;
                            subEnd = 24;
                        }
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append(currentStrData);
                    while (sb.toString().length() < 24) {
                        sb.append("0");
                    }
                    demoApplication.bleWriteData(currentCount, sb.toString(), mHandler);
                    currentStrData = "";
                }
            } else {
//                toast("发送完成");
                showMessageDialog("数据传输成功", getResources().getColor(R.color.zhuangtai_true), R.drawable.text_bg_success);
                currentCount = 0;
                btn_write_lock_data.setClickable(true);
            }
        } else {
            demoApplication.bleWriteData(currentCount, currentStrData, mHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        demoApplication.unregisterLocalBroadcast(LockDataActivity.this);
    }
}
