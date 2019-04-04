package com.xyw.smartlock.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.ACache;
import com.xyw.smartlock.utils.AcacheUserBean;
import com.xyw.smartlock.utils.PostImageHead;

import java.io.File;

public class ClipActivity extends Activity{
	private ClipImageLayout mClipImageLayout;
	private String path;
	private String pathUpload;
	private ProgressDialog loadingDialog;
	private ACache aCache;
	private AcacheUserBean acacheUserBean;
	private PostImageHead postImageHead;
	private String lingpai;
	private TextView back_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.activity_clipimage);
		back_photo = (TextView) findViewById(R.id.back_photo);
		back_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		acacheUserBean = new AcacheUserBean();
		aCache = ACache.get(this);
		acacheUserBean = (AcacheUserBean) aCache.getAsObject("LoginInfo");
		//这步必须要加
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		lingpai = acacheUserBean.getUSER_CONTEXT();
		loadingDialog=new ProgressDialog(this);
		loadingDialog.setTitle("please wait...");
		path=getIntent().getStringExtra("path");
		if(TextUtils.isEmpty(path)||!(new File(path).exists())){
			Toast.makeText(this, "图片加载失败",Toast.LENGTH_SHORT).show();
			return;
		}
		Bitmap bitmap=ImageTools.convertToBitmap(path, 600,600);
		if(bitmap==null){
			Toast.makeText(this, "图片加载失败",Toast.LENGTH_SHORT).show();
			return;
		}
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		mClipImageLayout.setBitmap(bitmap);
		((Button)findViewById(R.id.id_action_clip)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadingDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Bitmap bitmap = mClipImageLayout.clip();
						final String path= Environment.getExternalStorageDirectory()+"/SmartLock/image/"+acacheUserBean.getOP_NO()+".png";
						ImageTools.savePhotoToSDCard(bitmap,path);
						if(bitmap!=null && !bitmap.isRecycled()){
							bitmap.recycle();
						}
						postImageHead = new PostImageHead();
						try {
							String result = postImageHead.run(HttpServerAddress.UPLOADFILE+"&USER_CONTEXT="+lingpai,path);
							Log.e("result",result);
							if (result.equalsIgnoreCase("true")){
								
								loadingDialog.dismiss();
								Intent intent = new Intent(BroadcastKey.MSG_HEADPIX_UPLOAD_SUCCESS);
								intent.putExtra("pathUpload",path);
								sendBroadcast(intent);
								finish();
							}else if(result.equalsIgnoreCase("false")){
								loadingDialog.dismiss();
							}else{
								loadingDialog.dismiss();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}
}
