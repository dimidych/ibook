package com.ibook;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddNewContactRecordActivity extends Activity {
	
	private EditText txtNewContactRec=null;
	private ListView lstAllRecordTypes=null;		
	private final String LOG_TAG="iBook - AddNewContactRecordActivity";
	
	private DbWorkerCls m_DbWrkInst=null;
	private InitializerCls m_InitInst=null;
	private ArrayList<FieldCls> m_AllFieldLst=null;
	private int m_Operation=0;
	private ContactSetCls m_ContactDataInst=null;
	private FieldListAdapterCls m_FldLstAdapterObj=null;
	
	/** Occurs on visual component creation */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_contact_record);
		
		try{
			txtNewContactRec=(EditText)findViewById(R.id.txtContactValue);
			lstAllRecordTypes=(ListView)findViewById(R.id.lstContactType);
			Button btnAddContactRec=(Button)findViewById(R.id.btnAddContactRec);
			
			Intent intent=getIntent();
			m_InitInst=(InitializerCls)(intent.getParcelableExtra(InitializerCls.class.getCanonicalName()));
			m_Operation=intent.getIntExtra("action", -1);
			m_ContactDataInst=(ContactSetCls)(intent.getParcelableExtra(ContactSetCls.class.getCanonicalName()));
			
			switch(m_Operation){
				case ConstCls.INSERT_OP:{
					this.setTitle(R.string.title_activity_add_new_contact_record);
					btnAddContactRec.setText(R.string.btnAddContact);
				}
				break;
				
				case ConstCls.UPDATE_OP:{
					this.setTitle(R.string.title_activity_add_new_contact_record_upd);
					btnAddContactRec.setText(R.string.btnUpd);
				}
				break;
				
				case ConstCls.DELETE_OP:{
					this.setTitle(R.string.title_activity_add_new_contact_record_del);
					btnAddContactRec.setText(R.string.btnDel);
					lstAllRecordTypes.setEnabled(false);
					txtNewContactRec.setEnabled(false);
				}
				break;
			}
			
			if(m_ContactDataInst!=null)
				txtNewContactRec.setText(m_ContactDataInst.m_FieldValue);
		}
		catch(Exception ex){
			String strErr="Ошибка при загрузке - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Occurs on context menu creation */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_new_contact_record, menu);
		return true;
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
			
			FieldCls fldObj=new FieldCls(m_DbWrkInst);
			m_AllFieldLst=fldObj.getAllFieldInfo();
			
			if(m_AllFieldLst==null||m_AllFieldLst.size()==0)
				throw new Exception("Список полей пуст");
				
			m_FldLstAdapterObj=new FieldListAdapterCls(this,m_AllFieldLst);
			lstAllRecordTypes.setAdapter(m_FldLstAdapterObj);
			
			lstAllRecordTypes.setOnItemClickListener(new OnItemClickListener(){
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 m_FldLstAdapterObj.m_SelectedIndex=position;
					 m_FldLstAdapterObj.notifyDataSetChanged();
				 }
			});
			
			if((m_Operation==ConstCls.DELETE_OP||m_Operation==ConstCls.UPDATE_OP)&&m_ContactDataInst!=null)
				for(int i=0;i<m_AllFieldLst.size();i++)
					if(m_AllFieldLst.get(i).m_FieldId==m_ContactDataInst.m_FieldId){
						m_FldLstAdapterObj.m_SelectedIndex=i;
						m_FldLstAdapterObj.notifyDataSetChanged();
						break;
					}
		}
		catch(Exception ex){
			String strErr="Ошибка при загрузке1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Releases all resources */
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
	
	/** Destroys current activity and Releases all resources */
	@Override
	protected void onDestroy(){
		cleanResources();
		super.onDestroy();
	}

	/** Adds, updates or deletes contact record */
	public void btnAddContactRec_Click(View vw){
		try{
			if(txtNewContactRec.getText().toString().trim().equals(""))
				throw new Exception("Контактные данные не могут быть пустыми");
			
			if(m_DbWrkInst==null)
				throw new Exception("Не найдена БД");

			ContactSetCls contactSetInst=new ContactSetCls(m_DbWrkInst);
			Intent intent = new Intent();
			intent.putExtra("action", m_Operation);
			
			switch(m_Operation){
				case ConstCls.INSERT_OP:{
					if(!lstAllRecordTypes.getAdapter().isEmpty()&&lstAllRecordTypes.getCheckedItemCount()==0)
						throw new Exception("Необходимо выбрать вид поля");

					contactSetInst=new ContactSetCls(m_ContactDataInst.m_RecordId,m_ContactDataInst.m_PersonId,
							m_AllFieldLst.get(lstAllRecordTypes.getCheckedItemPosition()),
							txtNewContactRec.getText().toString().trim());
					intent.putExtra(ContactSetCls.class.getCanonicalName(), contactSetInst);
				}
				break;
				case ConstCls.UPDATE_OP:{
					if(!lstAllRecordTypes.getAdapter().isEmpty()&&lstAllRecordTypes.getCheckedItemCount()==0)
						throw new Exception("Необходимо выбрать вид поля");
					
					if(m_ContactDataInst!=null&&contactSetInst.checkContactFieldsExistance(m_ContactDataInst.m_PersonId,
							txtNewContactRec.getText().toString().trim(),m_ContactDataInst.m_FieldDefinition))
						throw new Exception("Запись уже существует");
					
					contactSetInst=new ContactSetCls(m_ContactDataInst.m_RecordId,m_ContactDataInst.m_PersonId,
							m_AllFieldLst.get(lstAllRecordTypes.getCheckedItemPosition()),
							txtNewContactRec.getText().toString().trim());
					intent.putExtra(ContactSetCls.class.getCanonicalName(), contactSetInst);
				}
				break;
				case ConstCls.DELETE_OP:{
					intent.putExtra(ContactSetCls.class.getCanonicalName(), m_ContactDataInst);
				}
				break;
			}

		    setResult(RESULT_OK, intent);
		    this.finish();
		}
		catch(Exception ex){
			String strErr="Ошибка при добавлении контактной записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Cancels this action */
	public void btnCancel_Click(View vw){
		try{
			Intent intent = new Intent();
		    setResult(RESULT_CANCELED, intent);
		    this.finish();
		}
		catch(Exception ex){
			String strErr="Ошибка отмены - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
}
