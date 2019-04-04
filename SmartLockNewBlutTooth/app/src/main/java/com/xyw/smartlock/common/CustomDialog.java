package com.xyw.smartlock.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xyw.smartlock.R;

public class CustomDialog extends Dialog {

	private Context context;
	EditText name;
	private String title;
	private String btnOkText;
	private String btnCancleText;
	private ClickListenerInterface clickListenerInterface;
	

	public interface ClickListenerInterface {

		public void doConfirm();

		public void doCancel();
	}

	public CustomDialog(Context context, String title, String btnOkText,
			String btnCancleText) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.title = title;
		this.btnOkText = btnOkText;
		this.btnCancleText = btnCancleText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		init();
	}

	public void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.customdialog, null);
		setContentView(view);

		TextView tvTitle = (TextView) view.findViewById(R.id.cDialog_title);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);
		Button btnCancle = (Button) view.findViewById(R.id.btnCancle);
		name = (EditText) findViewById(R.id.et_text);
		
		tvTitle.setText(title);
		btnOk.setText(btnOkText);
		btnCancle.setText(btnCancleText);
		

		btnOk.setOnClickListener(new clickListener());
		btnCancle.setOnClickListener(new clickListener());

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
		dialogWindow.setAttributes(lp);
	}

	public void setClicklistener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}

	private class clickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.btnOk:
				clickListenerInterface.doConfirm();
				
				break;
			case R.id.btnCancle:
				clickListenerInterface.doCancel();
				break;
			}
		}

	};

}