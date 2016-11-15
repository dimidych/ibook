package com.ibook;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendSmsActivity extends Activity {

	private final String LOG_TAG="iBook - SendSmsActivity";
	private EditText txtWhom=null;
	private EditText txtMessage=null;
	
	/** Occurs on current activity loading */
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_sms);
		this.setTitle(R.string.titleSendSms);
		
		try{
			txtWhom=(EditText)findViewById(R.id.txtWhom);
			txtMessage=(EditText)findViewById(R.id.txtMessage);
			txtWhom.setText(getIntent().getStringExtra("uri"));
			txtMessage.setText("Введите сообщение");
		}
		catch(Exception ex){
			String strErr="Ошибка инициализации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
		}
	}

	/** Sends Sms */
	public void btnSendSms_Click(View vw){
		try{
			if(txtWhom.getText().toString().trim().equals("")||txtMessage.getText().toString().trim().equals(""))
				return;
			
			SmsWorkerCls smsWrkObj=new SmsWorkerCls(this);
			smsWrkObj.sendMessage(txtWhom.getText().toString().trim(), txtMessage.getText().toString().trim());
			finish();
		}
		catch(Exception ex){
			String strErr="Ошибка при отправке СМС - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Cancels sending Sms */
	public void btnCancel_Click(View vw){
		finish();
	}
}
