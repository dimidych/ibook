package com.ibook;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewGroupActivity extends Activity {
	private final String LOG_TAG="iBook - AddNewGroupActivity";
	private DbWorkerCls m_DbWrkInst=null;
	private InitializerCls m_InitInst=null;
	private EditText txtNeGroup=null;
	private int m_Operation=0;
	private int m_GroupId=-1;
	private String m_GroupName=new String("");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_group);
		
		try{
			txtNeGroup=(EditText)findViewById(R.id.txtNewGroup);
			Intent intent=getIntent();
			m_InitInst=(InitializerCls)(intent.getParcelableExtra(InitializerCls.class.getCanonicalName()));
			Button btnAddGroup=(Button)findViewById(R.id.btnAddGroup);
			m_Operation=intent.getIntExtra("action", 1);
			
			switch(m_Operation){
				case ConstCls.INSERT_OP:{
					this.setTitle(R.string.title_activity_add_new_group);
					btnAddGroup.setText(R.string.btnAddContact);
				}
				break;
				case ConstCls.UPDATE_OP:{
					this.setTitle(R.string.title_activity_add_new_group_upd);
					btnAddGroup.setText(R.string.btnUpd);
					m_GroupId=intent.getIntExtra("groupId", 1);
				}
				break;
				case ConstCls.DELETE_OP:{
					this.setTitle(R.string.title_activity_add_new_group_del);
					btnAddGroup.setText(R.string.btnDel);
					m_GroupId=intent.getIntExtra("groupId", 1);
					txtNeGroup.setEnabled(false);
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

	@Override
	protected void onStart(){
		super.onStart();
		
		try{
			if(m_DbWrkInst==null)
				if(m_InitInst!=null)
					m_DbWrkInst=new DbWorkerCls(this,m_InitInst.m_DbPath,m_InitInst.m_DbVersion,m_InitInst.m_CryptKey);
				else
					m_DbWrkInst=new DbWorkerCls(this,"eBook",1,m_InitInst.m_CryptKey);
			

			if(m_Operation==ConstCls.UPDATE_OP||m_Operation==ConstCls.DELETE_OP)
				if(m_GroupId>-1){
					GroupCls groupInfo=new GroupCls(m_DbWrkInst);
					groupInfo=groupInfo.getGroupInfo(m_GroupId, "");
					
					if(groupInfo!=null){
						m_GroupName=groupInfo.m_GroupName;
						txtNeGroup.setText(m_GroupName);
					}
				}
		}
		catch(Exception ex){
			String strErr="Ошибка при загрузке1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
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
	
	@Override
	protected void onDestroy(){
		cleanResources();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add_new_group, menu);
		return true;
	}

	/** Adds new group */
	public void btnAddGroup_Click(View vw){
		try{
			if(txtNeGroup.getText().toString().trim().equals(""))
				throw new Exception("Название группы не может быть пустым");
			
			if(m_DbWrkInst==null)
				throw new Exception("Не найдена БД");

			GroupCls grpObj=new GroupCls(m_DbWrkInst);
			Intent intent = new Intent();
			
			switch(m_Operation){
				
				case ConstCls.INSERT_OP:{
					if(grpObj.checkGroupExistence(-1,txtNeGroup.getText().toString().trim()))
						throw new Exception("Группа уже существует");
				
					if(!grpObj.addGroup(txtNeGroup.getText().toString().trim()))
						throw new Exception("Не удалось добавить группу");
					
					intent.putExtra("newGroupId", grpObj.getGroupInfo(-1, txtNeGroup.getText().toString().trim()).m_GroupId);
				}
				break;
				
				case ConstCls.UPDATE_OP:{
					if(m_GroupName.trim().equalsIgnoreCase(txtNeGroup.getText().toString().trim()))
						throw new Exception("Совпадение старого и нового названий группы"); 
					
					if(!grpObj.updateGroup(m_GroupId,"",txtNeGroup.getText().toString().trim()))
						throw new Exception("Не удалось изменить группу");
					
					intent.putExtra("newGroupId", grpObj.getGroupInfo(-1, txtNeGroup.getText().toString().trim()).m_GroupId);
				}
				break;
				
				case ConstCls.DELETE_OP:{
					if(grpObj.isNotEmptyGroup(m_GroupId, ""))
						throw new Exception("Группа не пуста. Воспользуйтесь настройками для удаления.");
					
					if(!grpObj.deleteGroup(m_GroupId,""))
						throw new Exception("Не удалось удалить группу");
				}
				break;
			}

		    setResult(RESULT_OK, intent);
		    this.finish();
		}
		catch(Exception ex){
			String strErr="Ошибка при добавлении группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
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
