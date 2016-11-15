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
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class AddNewContactActivity extends Activity {
	
	private EditText txtName=null;
	private Spinner cmbAllGroups=null;
	private ListView lstContactSet=null;
	private final String LOG_TAG="iBook - AddNewContactActivity";
	
	private InitializerCls m_InitInst=null;
	private DbWorkerCls m_DbWrkInst=null;
	private ArrayList<ContactSetCls> m_AddedContactSetArr=new ArrayList<ContactSetCls>();
	private ContactSetListAdapterCls m_CntSetAdapterObj=null;
	private GroupsListAdapterCls m_AllGroupsAdapter=null;
	private int m_PersonId=-1;
	private PersonCls m_PersonObj=null;
	private int m_Operation=-1;
	private int m_ContactSetRecIdForInserion=60001;
	private boolean m_CanRereadDb=true;

	/** Occurs on current activity loading*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_contact);
		
		try{
			txtName=(EditText)findViewById(R.id.txtName);
			cmbAllGroups=(Spinner)findViewById(R.id.cmbAllGroups);
			cmbAllGroups.setPrompt("Выберите группу");
			lstContactSet=(ListView)findViewById(R.id.lstNewContacts);
			Button btnAddContact=(Button)findViewById(R.id.btnAddContact);
			
			Intent intent=getIntent();
			m_InitInst=(InitializerCls)(intent.getParcelableExtra(InitializerCls.class.getCanonicalName()));
			m_Operation=intent.getIntExtra("action", 0);
			
			switch(m_Operation){
				case ConstCls.INSERT_OP:{
					this.setTitle(R.string.title_activity_add_new_contact);
					btnAddContact.setText(R.string.btnAddContact);
				}
				break;
				
				case ConstCls.UPDATE_OP:{
					this.setTitle(R.string.title_activity_add_new_contact_upd);
					btnAddContact.setText(R.string.btnUpd);
					m_PersonId=intent.getIntExtra("personId", 0);
				}
				break;
				
				case ConstCls.DELETE_OP:{
					this.setTitle(R.string.title_activity_add_new_contact_del);
					btnAddContact.setText(R.string.btnDel);
					lstContactSet.setEnabled(false);
					cmbAllGroups.setEnabled(false);
					txtName.setEnabled(false);
					((Button)findViewById(R.id.btnAddGroup)).setEnabled(false);
					((Button)findViewById(R.id.btnAddContactRecord)).setEnabled(false);
					((Button)findViewById(R.id.btnUpdateContactRecord)).setEnabled(false);
					((Button)findViewById(R.id.btnDeleteContactRecord)).setEnabled(false);
					m_PersonId=intent.getIntExtra("personId", 0);
				}
				break;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка при загрузке - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Occurs on activity starting */
	@Override
	protected void onStart() {
		super.onStart();
		
		try{
			if(m_DbWrkInst==null)
				if(m_InitInst!=null)
					m_DbWrkInst=new DbWorkerCls(this,m_InitInst.m_DbPath,m_InitInst.m_DbVersion,m_InitInst.m_CryptKey);
				else
					m_DbWrkInst=new DbWorkerCls(this,"eBook",1,m_InitInst.m_CryptKey);
			
			if(!m_CanRereadDb)
				return;
			
			int groupId=-1;
			
			if(m_Operation==ConstCls.UPDATE_OP||m_Operation==ConstCls.DELETE_OP)
				if(m_PersonId>0){
					m_PersonObj=(new PersonCls(m_DbWrkInst)).getContactInfo(m_PersonId, "");
					groupId=m_PersonObj.m_GroupObj.m_GroupId;
					getContactSet(m_PersonId);
					txtName.setText(m_PersonObj.m_PersonName.trim());
				}
			
			lstContactSet.setOnItemClickListener(new OnItemClickListener(){
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 m_CntSetAdapterObj.m_SelectedRecordId=position;
					 m_CntSetAdapterObj.notifyDataSetChanged();
				 }
			});
			
			getAllGroups(groupId);
			m_CanRereadDb=false;
		}
		catch(Exception ex){
			String strErr="Ошибка добавления группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Occurs on adding new contact */
	public void btnAddContact_Click(View vw){
		try{
			String personName=txtName.getText().toString().trim();
			
			if(personName.equals(""))
				throw new Exception("ФИО не может быть пустым");
			
			if(m_AddedContactSetArr==null||m_AddedContactSetArr.size()==0)
				throw new Exception("Необходимо добавить хоть 1 контактную запись");
			
			Intent intent = new Intent();
			intent.putExtra("action", m_Operation);
			PersonCls personInst=new PersonCls(m_DbWrkInst);
			
			switch(m_Operation){
				case ConstCls.INSERT_OP:{					
					GroupCls grpInst=(m_AllGroupsAdapter!=null&&cmbAllGroups.getSelectedItem()!=null&&cmbAllGroups.getSelectedItemPosition()>-1)
							?m_AllGroupsAdapter.getItmAsGroup(cmbAllGroups.getSelectedItemPosition())
							:((new GroupCls(m_DbWrkInst)).getGroupInfo(1, ""));
					
					if(!personInst.addPerson(personName, grpInst, m_AddedContactSetArr))
						throw new Exception("Не удалось добавить контакт"); 
				}
				break;
				
				case ConstCls.UPDATE_OP:{
					GroupCls grpInst=(m_AllGroupsAdapter!=null&&cmbAllGroups.getSelectedItem()!=null&&cmbAllGroups.getSelectedItemPosition()>-1)
							?m_AllGroupsAdapter.getItmAsGroup(cmbAllGroups.getSelectedItemPosition())
							:((new GroupCls(m_DbWrkInst)).getGroupInfo(1, ""));
							
					if(!personInst.updatePerson(m_PersonId,"",personName, grpInst, m_AddedContactSetArr))
						throw new Exception("Не удалось изменить контакт"); 		
				}
				break;
				
				case ConstCls.DELETE_OP:{
					if(!personInst.deletePerson(m_PersonId,"",null))
						throw new Exception("Не удалось удалить контакт"); 
				}
				break;
			}
			
		    setResult(RESULT_OK, intent);
		    this.finish();
		}
		catch(Exception ex){
			String strErr="Ошибка при добавлении контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Occurs on adding new user group */
	public void btnAddGroup_Click(View vw){
		try{
			Intent intent = new Intent(this, AddNewGroupActivity.class);
			intent.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
			intent.putExtra("action", ConstCls.INSERT_OP);
			startActivityForResult(intent, ConstCls.GROUP_REC);
		}
		catch(Exception ex){
			String strErr="Ошибка добавления группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Occurs on adding new contact record */
	public void btnAddContactRecord_Click(View vw){
		try{
			Intent intent = new Intent(this, AddNewContactRecordActivity.class);
			intent.putExtra("action", ConstCls.INSERT_OP);
			intent.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
			intent.putExtra(ContactSetCls.class.getCanonicalName(), new ContactSetCls(m_ContactSetRecIdForInserion,m_PersonId,null,""));
			m_ContactSetRecIdForInserion++;
			startActivityForResult(intent, ConstCls.CONTACT_SET_REC);
		}
		catch(Exception ex){
			String strErr="Ошибка добавления контактной информации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Occurs on changing contact record */
	public void btnUpdateContactRecord_Click(View vw){
		try{
			if(lstContactSet.getAdapter().isEmpty()&&lstContactSet.getCheckedItemCount()==0)
				throw new Exception("Необходимо выбрать запись");
			
			Intent intent = new Intent(this, AddNewContactRecordActivity.class);
			intent.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
			intent.putExtra("action", ConstCls.UPDATE_OP);
			intent.putExtra(ContactSetCls.class.getCanonicalName(),m_AddedContactSetArr.get(lstContactSet.getCheckedItemPosition()));	
			startActivityForResult(intent, ConstCls.CONTACT_SET_REC);
		}
		catch(Exception ex){
			String strErr="Ошибка изменения контактной информации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Occurs on deleting contact record */
	public void btnDeleteContactRecord_Click(View vw){
		try{
			if(lstContactSet.getAdapter().isEmpty()&&lstContactSet.getCheckedItemCount()==0)
				throw new Exception("Необходимо выбрать запись");
			
			Intent intent = new Intent(this, AddNewContactRecordActivity.class);
			intent.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
			intent.putExtra("action", ConstCls.DELETE_OP);
			intent.putExtra(ContactSetCls.class.getCanonicalName(),m_AddedContactSetArr.get(lstContactSet.getCheckedItemPosition()));
			startActivityForResult(intent, ConstCls.CONTACT_SET_REC);
		}
		catch(Exception ex){
			String strErr="Ошибка удаления контактной информации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Retrieves all group data */
	private ArrayList<GroupCls> getAllGroups(int selectedGroupId){
		ArrayList<GroupCls> result=null;

		try{
				if(m_DbWrkInst==null)
					throw new Exception("БД не подключена"); 
				
				GroupCls grpObj=new GroupCls(m_DbWrkInst);
				result=grpObj.getAllGroups();
				
				if(result==null||result.size()==0)
					throw new Exception("Группы не обнаружены");
				
				m_AllGroupsAdapter=new GroupsListAdapterCls(this,result,(byte)0);
				cmbAllGroups.setAdapter(m_AllGroupsAdapter);
					
				if(selectedGroupId>-1)
					for(int i=0;i<result.size();i++)
						if(result.get(i).m_GroupId==selectedGroupId){
							cmbAllGroups.setSelection(i);
							break;
						}
		}
		catch(Exception ex){
			String strErr="Ошибка получения групп - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return null;
		}
		
		return result;
	}
	
	/** Retrieves contact set list */
	private ArrayList<ContactSetCls> getContactSet(int personId){
		ArrayList<ContactSetCls> result=null;
		
		try{
			if(m_DbWrkInst==null)
				throw new Exception("БД не подключена"); 
			
			ContactSetCls cntSetObj=new ContactSetCls(m_DbWrkInst);
			result=cntSetObj.getPersonRelatedFields(personId);
			
			if(result==null||result.size()==0)
				throw new Exception("Данные не обнаружены");

			m_AddedContactSetArr=result;
			m_CntSetAdapterObj=new ContactSetListAdapterCls(this,m_AddedContactSetArr);
			lstContactSet.setAdapter(m_CntSetAdapterObj);
		}
		catch(Exception ex){
			String strErr="Ошибка получения списка контактов - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return null;
		}
		
		return result;
	}
	
	/** Retrieves results from group and contact set operations activities */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    try{
	    	if(resultCode==RESULT_CANCELED)
	    		return;
	    	
	    	if (data == null) 
		    	return;
			
	    	switch(requestCode){
		    	case ConstCls.GROUP_REC:{
				    int newGroupId = data.getIntExtra("newGroupId",-1);
				    
				    if(newGroupId>0)
				    	getAllGroups(newGroupId);
		    	}
		    	break;
		    	case ConstCls.CONTACT_SET_REC:{
		    		int actionId = data.getIntExtra("action",-1);
		    		ContactSetCls newCntctSetInst=(ContactSetCls)(data.getParcelableExtra(ContactSetCls.class.getCanonicalName()));
		    		
		    		if(newCntctSetInst==null)
		    			throw new Exception("Данные не получены");
		    		
		    		newCntctSetInst.m_FieldDefinition=newCntctSetInst.m_FieldId>0?(new FieldCls(m_DbWrkInst)).getField(newCntctSetInst.m_FieldId, ""):null;
		    		
		    		switch(actionId){
			    		case ConstCls.INSERT_OP:{
			    			if(!m_AddedContactSetArr.contains(newCntctSetInst))
			    				m_AddedContactSetArr.add(newCntctSetInst);
			    		}
			    		break;
						case ConstCls.UPDATE_OP:{
							for(int i=0;i<m_AddedContactSetArr.size();i++)
								if(m_AddedContactSetArr.get(i).m_RecordId==newCntctSetInst.m_RecordId){
				    				m_AddedContactSetArr.get(i).m_FieldValue=newCntctSetInst.m_FieldValue.trim();
				    				m_AddedContactSetArr.get(i).m_FieldDefinition=newCntctSetInst.m_FieldDefinition;
				    				break;
				    			}
						}
						break;
						case ConstCls.DELETE_OP:{
							for(int i=0;i<m_AddedContactSetArr.size();i++)
								if(m_AddedContactSetArr.get(i).m_RecordId==newCntctSetInst.m_RecordId){
									m_AddedContactSetArr.remove(i);
									break;
								}
						}
						break;
		    		}
		    		
		    		if(m_CntSetAdapterObj==null){
    					m_CntSetAdapterObj=new ContactSetListAdapterCls(this,m_AddedContactSetArr);
    					lstContactSet.setAdapter(m_CntSetAdapterObj);
		    		}
    				
    				m_CntSetAdapterObj.notifyDataSetChanged();
		    	}
		    	break;
	    	}
	    }
	    catch(Exception ex){
	    	String strErr="Ошибка получения данных с дочерней формы - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
	    	Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
	    }
	}
	
	/** Occurs on canceling adding */
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

	/** Occurs on context menu creation */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_new_contact, menu);
		return true;
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
