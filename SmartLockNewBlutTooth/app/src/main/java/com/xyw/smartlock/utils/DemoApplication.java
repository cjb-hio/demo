package com.xyw.smartlock.utils;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import com.baidu.mapapi.SDKInitializer;
import com.xyw.smartlock.bean.AcacheSetBean;
import com.yanzhenjie.nohttp.NoHttp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DemoApplication extends Application {
	private int MS;
	private int BT = 0;
	private int connect = 0;
	private String BtName = "未连接";
	private Intent intent = new Intent();
	private String currentAddress = "未连接";
	private String currentLockId = "";
	private int CSY = 0;
	private boolean isManual = false;

	public static final String WRITE_ID_BEGIN = "WriteIdBegin";
	public static final String UNLOCK_BEGIN = "UnLockBegin";
	public static final String READ_ID_BEGIN = "ReadIdBegin";
	public static final String LOCK_BEGIN = "LockBegin";

	public int getCSY() {
		return CSY;
	}

	public void setCSY(int CSY) {
		this.CSY = CSY;
	}

	public void setCurrentAddress(String address) {
		this.currentAddress = address;
	}
	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentLockId(String currentLockId) {
		this.currentLockId = currentLockId;
	}
	public String getCurrentLockId() {
		return currentLockId;
	}

	public void setISManual(boolean isManual) {
		this.isManual = isManual;
	}
	public boolean getIsManual(){
		return isManual;
	}

	public String getBtName() {
		return BtName;
	}

	public void setBtName(String btName) {
		BtName = btName;
	}

	public int getConnect() {
		return connect;
	}

	public void setConnect(int connect) {
		this.connect = connect;
	}

	public int getBT() {
		return BT;
	}

	public void setBT(int BT) {
		this.BT = BT;
	}

	public int getMS() {
		return MS;
	}

	public void setMS(int MS) {
		this.MS = MS;
	}
	private AcacheSetBean acacheSetBean;
	private ACache aCache;
	public Handler mHandler;

	private byte[] lock_id;
	private byte[] lock_safe;

	public byte[] getLock_id() {
		return lock_id;
	}

	public void setLock_id(byte[] lock_id) {
		this.lock_id = lock_id;
	}

	public byte[] getLock_safe() {
		return lock_safe;
	}

	public void setLock_safe(byte[] lock_safe) {
		this.lock_safe = lock_safe;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	public void ReadIdBegin(Handler lHandler)
	{
		setmHandler(lHandler);
		intent.setAction("ReadIdBegin");
		sendBroadcast(intent);
	}
	public void bleOpenLock(byte[] lock_id, byte[] lock_safe, String ZoneKey, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("UnLockBegin");
		intent.putExtra("LockId",lock_id);
		intent.putExtra("LockSafe",lock_safe);
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}
	public void bleXiaZhuang(byte[] lock_id, byte[] lock_safe, String ZoneKey, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("UpdateKeyBegin");
		intent.putExtra("LockId",lock_id);
		intent.putExtra("LockSafe",lock_safe);
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}
	public void bleWriteLock(byte[] lock_id, byte[] lock_safe, String ZoneKey, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("WriteIdBegin");
		intent.putExtra("LockId",lock_id);
		intent.putExtra("LockSafe",lock_safe);
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}
	public void bleControlLock(String type, byte[] lock_id, byte[] lock_safe, String ZoneKey, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction(type);
		intent.putExtra("LockId",lock_id);
		intent.putExtra("LockSafe",lock_safe);
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}
	public void bleReadId(String lock_id, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("ReadIdBegin");
		intent.putExtra("LockId",lock_id);
		sendBroadcast(intent);
	}
	public void WriteIdBegin(String Lid,Handler lHandler) {
		setmHandler(lHandler);
		intent.setAction("WriteIdBegin");
		intent.putExtra("Lid",Lid);
		sendBroadcast(intent);
	}
	public void UnLockBegin(String ZoneKey,Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("UnLockBegin");
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}

	public void LockBegin(String ZoneKey,Handler lHandler) {
		setmHandler(lHandler);
		intent.setAction("LockBegin");
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}
	public void UpdateKeyBegin(String ZoneId,String ZoneKey,Handler lHandler)
	{
		intent.setAction("UpdateKeyBegin");
		setmHandler(lHandler);
		intent.putExtra("ZoneId",ZoneId);
		intent.putExtra("ZoneKey",ZoneKey);
		sendBroadcast(intent);
	}

	public void connect(){
		setmHandler(mHandler);
		intent.setAction("CONNECT");
		sendBroadcast(intent);
	}
	public void adconnect(String address){
		intent.setAction("ADCONNECT");
		intent.putExtra("ADDRESS",address);
		sendBroadcast(intent);
	}
	public void disconnect(){
		Intent intentdis=new Intent();
		intentdis.setAction("DISCONNECT");
		sendBroadcast(intentdis);
	}

	/**
	 * 写数据
	 *
	 * @param qu
	 * @param data
	 * @param mHandler
	 */
	public void bleWriteData(int qu, String data, Handler mHandler) {
		setmHandler(mHandler);
		writeData(qu, data);
	}

	/**
	 * 读数据
	 *
	 * @param qu
	 * @param mHandler
	 */
	public void bleReadData(int qu, Handler mHandler) {
		setmHandler(mHandler);
		readData(qu);
	}

	public void readData(int qu) {
		setmHandler(mHandler);
		intent.setAction("READ_DATA");
		intent.putExtra("QU", qu);
		sendBroadcast(intent);
	}

	public void writeData(int qu, String data) {
		setmHandler(mHandler);
		intent.setAction("WRITE_DATA");
		intent.putExtra("QU", qu);
		intent.putExtra("DATA", data);
		sendBroadcast(intent);
	}

	public void bleOpenWriteRead(byte[] lock_id, byte[] lock_safe, String ZoneKey, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("OPEN_WRITE_READ");
		intent.putExtra("LockId", lock_id);
		intent.putExtra("LockSafe", lock_safe);
		intent.putExtra("ZoneKey", ZoneKey);
		sendBroadcast(intent);
	}

	public void bleCloseWriteRead(byte[] lock_id, byte[] lock_safe, String ZoneKey, Handler mHandler) {
		setmHandler(mHandler);
		intent.setAction("CLOSE_WRITE_READ");
		intent.putExtra("LockId", lock_id);
		intent.putExtra("LockSafe", lock_safe);
		intent.putExtra("ZoneKey", ZoneKey);
		sendBroadcast(intent);
	}

	public ExecutorService handlerPool= Executors.newFixedThreadPool(10);
	private LockUtil mLockUtil;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		//初始化Nohttp
		NoHttp.initialize(this);
		// 缓存数据
		aCache = ACache.get(this);
		mLockUtil = LockUtil.getInstance();
		NfcUtils.getInstance().init(getApplicationContext());
		BluetoothUtils.getInstance().init(getApplicationContext());
		acacheSetBean = new AcacheSetBean();
		acacheSetBean = (AcacheSetBean) aCache.getAsObject("SET");
//		if (acacheSetBean!=null&&acacheSetBean.getMs().equals("1")){
//			MS = 1;
//		}else {
//			MS = 0;
//		}
		if (acacheSetBean != null) {
			MS = Integer.parseInt(acacheSetBean.getMs());
		} else {
			if (NfcUtils.getInstance().isHasNFC()) {
				MS = 0;
			} else {
				MS = 1;
			}
		}
	}

}