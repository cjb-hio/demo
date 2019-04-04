package com.xyw.smartlock;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.smartlock.nfc.Ntag_I2C_Demo;


public class TestReadIdActivity extends AppCompatActivity {

	private Ntag_I2C_Demo demo;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private Boolean newIntent = false;
	private TextView title;
	private ImageView backImage;
	private Button button;
	static private TextView read_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_read_id);
		getSupportActionBar().hide();
		// TODO Auto-generated method stub
		// 设置标题栏名称
		initView();
		//
		setNfcForeground();
		// 判断是否有Nfc功能
		checkNFC();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.common_tv_title);
		title.setText("读ID");
		// 设置返回按键
		backImage = (ImageView) findViewById(R.id.common_title_back);
		backImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
// 初始化控件
		read_tv = (TextView) findViewById(R.id.read_tv);
		button = (Button) findViewById(R.id.read_btn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});



//demo = new Ntag_I2C_Demo(null, this);
		mAdapter = NfcAdapter.getDefaultAdapter(this);
	}


	private void checkNFC() {
		if (mAdapter != null) {
			if (!mAdapter.isEnabled()) {
				new AlertDialog.Builder(this).setTitle("NFC关闭了").setMessage("去设置")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// if (android.os.Build.VERSION.SDK_INT
								// >= 16) {
								// startActivity(new
								// Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
								// } else {
								startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
								// }
								// startActivity(new
								// Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				}).show();
			}
		} else {
			new AlertDialog.Builder(this).setTitle("没有可用的NFC。应用程序将被关闭。")
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);
						}
					}).show();
		}
	}

	public void setNfcForeground() {
		// Create a generic PendingIntent that will be delivered to this
		// activity. The NFC stack will fill
		// in the intent with the details of the discovered tag before
		// delivering it to this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Setup an intent filter for all NDEF based dispatches
		mFilters = new IntentFilter[]{
				// new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
				new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};

		// Setup a tech list for all NFC tags
		mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
		}

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) && newIntent == false) {
			// give the UI some time to load, then execute the Demo
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onNewIntent(getIntent());
				}
			}, 100);
		}
		newIntent = false;
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mAdapter != null && newIntent == false) {
			mAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onNewIntent(Intent nfc_intent) {
		newIntent = true;
		super.onNewIntent(nfc_intent);
		// Set the pattern for vibration
		long pattern[] = {0, 100};

		// Vibrate on new Intent
		Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, -1);
		doProcess(nfc_intent);
	}

	public void doProcess(Intent nfc_intent) {
		final Tag tag = nfc_intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//		if (demo != null) {
//			if (demo.isReady()) {
//				demo.FinishAllTasks();
//				demo = null;
//			}
//		}
//		demo = new Ntag_I2C_Demo(tag, this);
//		demo.readId();

	}

}
