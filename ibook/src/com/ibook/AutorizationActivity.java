package com.ibook;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AutorizationActivity extends Activity {
	private final String LOG_TAG="iBook - AutorizationActivity";
	private EditText txtPwd=null;
	private EditText txtLogin=null;
	private Button btnOk=null;
	
	/** Occurs on current activity loading */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autorization);
		this.setTitle(R.string.title_activity_autorization);
		
		try{
			txtPwd = (EditText) findViewById(R.id.txtPwd);
			txtLogin = (EditText) findViewById(R.id.txtLogin);
			btnOk=(Button) findViewById(R.id.btnOk);
		}
		catch(Exception ex){
			String strErr="Непредвиденная ошибка приложения - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Occurs on bntOk clicking */
	public void btnOk_Click(View vw){
		DbWorkerCls dbWrkObj=null;
		
		try{
			InitializerCls initInst=new InitializerCls(this);
			initInst.readIniXmlData();
			
			if(initInst.checkAuthorization(txtLogin.getText().toString(), txtPwd.getText().toString())){
				btnOk.setEnabled(false);
				dbWrkObj=new DbWorkerCls(this, initInst.m_DbPath.trim(),initInst.m_DbVersion,initInst.m_CryptKey);
				
				if(dbWrkObj.checkRecordExistance("FIELD_TYPE_TBL", "", new String[]{})){
					Intent intent = new Intent(this, MainActivity.class);
					intent.putExtra(InitializerCls.class.getCanonicalName(),initInst);
					startActivity(intent);
				}
				else
					throw new Exception("Неверный логин или пароль");
			}
		}
		catch(Exception ex){
			String strErr="Ошибка авторизации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
		finally{
			try{
				if(dbWrkObj!=null){
					dbWrkObj.close();
				}
			}
			catch(Exception ex){Log.d(LOG_TAG,ex.getMessage());}
		}
	}
	
	/** Occurs on bntCancel clicking */
	public void btnCancel_Click(View vw){
		try{
			onDestroy();
		}
		catch(Exception ex){
			String strErr="Непредвиденная ошибка приложения2 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Cleans text fields */
	private void cleanFields(){
		txtLogin.setText("");
		txtPwd.setText("");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_autorization, menu);
		return true;
	}

	@Override
	protected void onPause(){
		cleanFields();
		super.onPause();
	}
	
	@Override
	protected void onStop(){
		cleanFields();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		try{
			final int pid = android.os.Process.myPid();
			android.os.Process.killProcess(pid);
			super.onDestroy();
		}
		catch(Exception ex){
			String strErr="Непредвиденная ошибка приложения3 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	} 
}
