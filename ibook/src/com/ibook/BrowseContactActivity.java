package com.ibook;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

public class BrowseContactActivity extends Activity {

	private ListView lstContactSetRec=null;
	private final String LOG_TAG="iBook - BrowseContactActivity";
	private InitializerCls m_InitInst=null;
	private DbWorkerCls m_DbWrkInst=null;
	private int m_PersonId=-1;
	
	/** Occurs on current activity loading */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_contact);
		this.setTitle(R.string.title_activity_browse);
		
		try{
			lstContactSetRec=(ListView)this.findViewById(R.id.lstPersonContacts);
			m_InitInst=(InitializerCls)(getIntent().getParcelableExtra(InitializerCls.class.getCanonicalName()));
			m_PersonId=getIntent().getIntExtra("personId", -1);
		}
		catch(Exception ex){
			String strErr="Сбой инициализации - "+ex.getMessage();
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
			
			if(m_PersonId<0)
				finish();
			
			PersonCls personInst=new PersonCls(m_DbWrkInst);
			personInst=personInst.getContactInfo(m_PersonId, "");
			
			if(personInst==null)
				finish();
			
			((TextView)findViewById(R.id.lblPersonName)).setText(personInst.m_PersonName);
			((TextView)findViewById(R.id.lblPersonGroup)).setText(personInst.m_GroupObj.m_GroupName);
			
			if(personInst.m_ContactRecordSet!=null&&personInst.m_ContactRecordSet.size()>0){
				ContactSetROListAdapterCls contactSetAda=new ContactSetROListAdapterCls(this,personInst.m_ContactRecordSet);
				lstContactSetRec.setAdapter(contactSetAda);
			}
		}
		catch(Exception ex){
			String strErr="Сбой инициализации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Adds, updates or deletes contact */
	private void makeActionWithContact(int actionId,int personId){
		try{
			Intent intent = new Intent(this, AddNewContactActivity.class);
			intent.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
			intent.putExtra("action", actionId);
			
			if(actionId!=ConstCls.INSERT_OP)
				intent.putExtra("personId", personId);
			
			startActivityForResult(intent, ConstCls.CONTACT_REC);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе makeActionWithContact - "+ex.getMessage();
			Log.d(LOG_TAG, strErr);
			return;
		}
	}

	/** Occurs on options menu creation */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_browse_contact, menu);
		return true;
	}
	
	/** Occurs on option menu item clicking*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()){
				case R.id.mnuCtxInsertPerson:
					makeActionWithContact(ConstCls.INSERT_OP,-1);		
				break;
				
				case R.id.mnuCtxUpdatePerson:{
					if(m_PersonId>-1)
						makeActionWithContact(ConstCls.UPDATE_OP,m_PersonId);				
				}
				break;
								
				case R.id.mnuCtxDeletePerson:{
					if(m_PersonId>-1)
						makeActionWithContact(ConstCls.DELETE_OP,m_PersonId);
				}
				break;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка onOptionItemSelected - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/** Retrieves results from edit activities */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    try{
	    	if(resultCode==RESULT_CANCELED)
	    		return;
	    	
	    	if (data == null) 
		    	return;
			
	    	int actionId = data.getIntExtra("action",-1);
	    	
	    	if(actionId==ConstCls.DELETE_OP)
	    		this.finish();
	    }
	    catch(Exception ex){
	    	String strErr="Ошибка получения данных с дочерней формы - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
			return;
	    }
	}
	
	/** Resource cleaner */
	private void cleanResources(){
		try{
			if(m_DbWrkInst!=null)
				m_DbWrkInst.close();
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка очистки ресурсов - "+ex.getMessage());
			return;
		}
	}
	
	/** Activity destroyer and Resource cleaner */
	@Override
	protected void onDestroy() {
		cleanResources();
		super.onDestroy();
	}
}
