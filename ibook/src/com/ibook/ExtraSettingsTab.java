package com.ibook;

import java.util.Calendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ExtraSettingsTab extends FragmentActivity {
		
	private final String LOG_TAG="iBook - ExtraSettingsTab";
	private InitializerCls m_InitInst=null;
	private DbWorkerCls m_DbWrkInst=null;
	private ProgressFragment m_DlgProgress=null;
	
	/** Occurs on current activity loading */
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_settings_extra);
	    
	    try{
		    Intent intent=getIntent();
			m_InitInst=(InitializerCls)(intent.getParcelableExtra(InitializerCls.class.getCanonicalName()));
	    }
	    catch(Exception ex){
			String strErr="Ошибка при загрузке - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Occurs on activity starting */
	@Override
	protected void onStart(){
		super.onStart();
		
		try{
			if(m_DbWrkInst==null)
				if(m_InitInst!=null)
					m_DbWrkInst=new DbWorkerCls(this,m_InitInst.m_DbPath,m_InitInst.m_DbVersion,m_InitInst.m_CryptKey);
				else
					m_DbWrkInst=new DbWorkerCls(this,"eBook",1,m_InitInst.m_CryptKey);
		}
		catch(Exception ex){
			String strErr="Сбой инициализации1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Downloads data from xml */
	public void btnDwnldFromXml_Click(View vw){
		CreateTask("DwnldFromXml");
	}
	
	/** Downloads data from crypted xml */
	public void btnDwnldFromCryptXml_Click(View vw){
		CreateTask("DwnldFromCryptXml");	
	}
	
	/** Downloads data from wm xml */
	public void btnDwnldFromWmXml_Click(View vw){
		CreateTask("DwnldFromWmXml");
	}
	
	/** Uploads data to xml */
	public void btnUpldToXml_Click(View vw){
		CreateTask("UpldToXml");
	}
	
	/** Uploads data to crypted xml */
	public void btnUpldToCryptXml_Click(View vw){
		CreateTask("UpldToCryptXml");
	}
	
	/** Forced deletes all data */
	public void btnDeleteAll_Click(View vw){
		CreateTask("DeleteAll");
	}

	/** Creates asynchronous task */
	private void CreateTask(String taskName){
		try{
			DifferentAsyncTaskCls asyncTask=new DifferentAsyncTaskCls();
			asyncTask.execute(taskName);
		}
		catch(Exception ex){
			String strErr="Ошибка CreateTask - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Sends message into different thread */
	private void SendMessage(String message){
		if(message.trim().equals(""))
			return;
		
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void createProgressDialog(String captionText){
		m_DlgProgress=new ProgressFragment();
		m_DlgProgress.show(this.getFragmentManager(), captionText);
	}
	
	/** Internal class for asynchronous tasks */
    class DifferentAsyncTaskCls extends AsyncTask<String, Integer, Void> {
		
		private final String LOG_TAG="iBook - DifferentAsyncTaskCls";
		private Calendar m_StartDate=Calendar.getInstance();
		
		/** Not default constructor */
		public DifferentAsyncTaskCls(){}
		
		@Override
	    protected void onPreExecute() {
	      super.onPreExecute();
	      m_StartDate=Calendar.getInstance();
	      createProgressDialog("Выполнение...");
	    }

		@Override
		protected Void doInBackground(String... taskName) {
			
			try{
				if(taskName[0].equals("DwnldFromXml")){
					XmlDbWorkerCls xmlDbWorker=new XmlDbWorkerCls(m_DbWrkInst,m_InitInst.m_XmlPath);
					xmlDbWorker.AddDataFromEbook(false);
				}
				
				if(taskName[0].equals("DwnldFromCryptXml")){
					XmlDbWorkerCls xmlDbWorker=new XmlDbWorkerCls(m_DbWrkInst,m_InitInst.m_XmlPath);
					xmlDbWorker.AddDataFromEbook(true);	
				}
				
				if(taskName[0].equals("DwnldFromWmXml")){
					XmlDbWorkerCls xmlDbWorker=new XmlDbWorkerCls(m_DbWrkInst,m_InitInst.m_XmlPath);
					xmlDbWorker.AddDataFromWmEbook();
				}
				
				if(taskName[0].equals("UpldToXml")){
					XmlDbWorkerCls xmlDbWorker=new XmlDbWorkerCls(m_DbWrkInst,m_InitInst.m_XmlPath);
					xmlDbWorker.writeXmlData(false);
				}
				
				if(taskName[0].equals("UpldToCryptXml")){
					XmlDbWorkerCls xmlDbWorker=new XmlDbWorkerCls(m_DbWrkInst,m_InitInst.m_XmlPath);
					xmlDbWorker.writeXmlData(true);
				}
				
				if(taskName[0].equals("DeleteAll"))
					m_DbWrkInst.dropDatabase();
			}
			catch(Exception ex){
				String strErr="Ошибка doInBackground - "+ex.getMessage();
		    	Log.d(LOG_TAG,strErr);
			}
			
			return null;
		}

		@Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);
	      
	      try{
	    	  m_DlgProgress.dismiss();
	    	  SendMessage("Выполнено за "+((Calendar.getInstance().getTimeInMillis()-m_StartDate.getTimeInMillis())/1000)+" секунд");
	      }
		  catch(Exception ex){
				String strErr="Ошибка onPostExecute - "+ex.getMessage();
		    	Log.d(LOG_TAG,strErr);
		  }
	    }

	}

	/** Releases all resources */
	private void cleanResources(){
		try{
			if(m_DbWrkInst!=null){
				m_DbWrkInst.close();
				Log.d(LOG_TAG,"очисткa ресурсов");
			}
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка очистки ресурсов - "+ex.getMessage());
			return;
		}
	}
	
	/** Destroys current activity and Releases all resources */
	@Override
	protected void onDestroy(){
		cleanResources();
		super.onDestroy();
	}

}
