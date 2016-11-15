package com.ibook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsWorkerCls extends BroadcastReceiver {

	private final String LOG_TAG="iBook - SmsWorkerCls";
	private Context m_Ctx=null;
	
	/** Not default constructor */
	public SmsWorkerCls(Context ctx){
		m_Ctx=ctx;
	}
	
	/** Sends SMS message */
	public void sendMessage(String phoneNum, String message){
		try{
			if(phoneNum.trim().equals("")||message.trim().equals(""))
				return;
			
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNum.trim(), null, message.trim(), null, null);
		}
		catch(Exception ex){
			String strErr="Ошибка отправки SMS - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Reciever when sending handler */
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			switch(getResultCode()) {
			  case Activity.RESULT_OK:
				  Toast.makeText(m_Ctx, "Сообщение успешно доставлено", Toast.LENGTH_LONG).show();
			   break;
			  case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				  Toast.makeText(m_Ctx, "Сообщение не доставлено", Toast.LENGTH_LONG).show();
			   break;
			  case SmsManager.RESULT_ERROR_RADIO_OFF:
				  Toast.makeText(m_Ctx, "Сообщение не доставлено", Toast.LENGTH_LONG).show();
			   break;
			  case SmsManager.RESULT_ERROR_NULL_PDU:
				  Toast.makeText(m_Ctx, "Сообщение не доставлено", Toast.LENGTH_LONG).show();
			   break;
			  }
		}
		catch(Exception ex){
			String strErr="Ошибка получения ответа - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

}
