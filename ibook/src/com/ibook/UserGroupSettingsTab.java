package com.ibook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserGroupSettingsTab extends Activity {
	
	private ListView lstAllGroup=null;
	private EditText txtGroupName=null;
	
	private final String LOG_TAG="iBook - UserGroupSettingsTab";
	private InitializerCls m_InitInst=null;
	private DbWorkerCls m_DbWrkInst=null;
	private GroupsListAdapterCls m_AllGroupsAdapter=null;
	private ArrayList<GroupCls> m_AllGroupLst=null;
	private int m_CurrentGroupId=-1;
	
	/** Occurs on current activity loading */
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_settings_user_group);
	    
	    try{
	    	lstAllGroup=(ListView)this.findViewById(R.id.lstAllGroup);
	    	txtGroupName=(EditText)this.findViewById(R.id.txtGroup);
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
			
			getAllGroups();
			lstAllGroup.setOnItemClickListener(new OnItemClickListener(){
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 if(m_AllGroupsAdapter!=null){
						 m_AllGroupsAdapter.m_SelectedRecordId=position;
						 m_CurrentGroupId=(int)m_AllGroupsAdapter.getItemId(position);
						 txtGroupName.setText(m_AllGroupsAdapter.getItmAsGroup(position).m_GroupName);
						 m_AllGroupsAdapter.notifyDataSetChanged();
					 }
				 }
			});
		}
		catch(Exception ex){
			String strErr="Сбой инициализации1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Retrieves all group data */
	private void getAllGroups(){
		m_AllGroupLst=null;

		try{
			if(m_DbWrkInst==null)
				throw new Exception("БД не подключена"); 
				
			GroupCls grpObj=new GroupCls(m_DbWrkInst);
			m_AllGroupLst=grpObj.getAllGroups();
				
			if(m_AllGroupLst==null||m_AllGroupLst.size()==0)
				throw new Exception("Группы не обнаружены");
				
			m_AllGroupsAdapter=new GroupsListAdapterCls(this,m_AllGroupLst,(byte)1);
			lstAllGroup.setAdapter(m_AllGroupsAdapter);
		}
		catch(Exception ex){
			String strErr="Ошибка получения групп - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Adds new user group */
	public void btnAddGroup_Click(View vw){
		try{
			String groupName=txtGroupName.getText().toString().trim();
			if(groupName.equals(""))
				throw new Exception("Не указано наименование группы");
			
			GroupCls grpObj=new GroupCls(m_DbWrkInst);

			if(grpObj.checkGroupExistence(-1,groupName))
				throw new Exception("Группа уже существует");
		
			if(!grpObj.addGroup(groupName))
				throw new Exception("Не удалось добавить группу");
			
			getAllGroups();
		}
		catch(Exception ex){
			String strErr="Ошибка при добавлении группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Updates user group */
	public void btnUpdateGroup_Click(View vw){
		try{
			if(m_CurrentGroupId<1)
				throw new Exception("Не выбрана группа");
			
			String groupName=txtGroupName.getText().toString().trim();
			
			if(groupName.equals(""))
				throw new Exception("Не указано наименование группы");
			
			GroupCls grpObj=new GroupCls(m_DbWrkInst);

			if(!grpObj.updateGroup(m_CurrentGroupId,"",groupName))
				throw new Exception("Не удалось изменить группу");
			
			getAllGroups();
		}
		catch(Exception ex){
			String strErr="Ошибка при изменении группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Retrieves dialog result */
	private byte getDialogResult(String message,String caption){
		byte result=ConstCls.DIALOG_CANCEL;
		
		try{
			AskFragment askFrgmnt=new AskFragment(message);
			askFrgmnt.show(this.getFragmentManager(), caption);
			result=AskFragment.m_DialogResult;
		}
		catch(Exception ex){
			return ConstCls.DIALOG_CANCEL;
		}
		
		return result;
	}
	
	/** Deletes user group */
	public void btnDeleteGroup_Click(View vw){
		try{
			if(m_CurrentGroupId<1)
				throw new Exception("Не выбрана группа");
			
			GroupCls grpObj=new GroupCls(m_DbWrkInst);
			
			if(grpObj.isNotEmptyGroup(m_CurrentGroupId, ""))
				if(getDialogResult("Будут удалены все члены группы","Группа не пуста...")==ConstCls.DIALOG_CANCEL)
					return;
			
			(new PersonCls(m_DbWrkInst)).deletePerson(-1, "", new GroupCls(m_CurrentGroupId,""));
			
			if(!grpObj.deleteGroup(m_CurrentGroupId,""))
				throw new Exception("Не удалось удалить группу");
			
			getAllGroups();
		}
		catch(Exception ex){
			String strErr="Ошибка при удалении группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
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

}
