package com.ibook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

	private EditText txtSearch=null;
	private ExpandableListView treeAllContact=null;
	
	private final String LOG_TAG="iBook - MainActivity";
	private InitializerCls m_InitInst=null;
	private DbWorkerCls m_DbWrkInst=null;
	private CustomExpListAdapter m_AllContactsAdapter=null;
	private ArrayList<PersonCls> m_PersonList=null;
	
	/** Occurs on current activity loading */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.setTitle(R.string.title_activity_main);

		try{
			txtSearch=(EditText)this.findViewById(R.id.txtSearch);
			treeAllContact=(ExpandableListView)this.findViewById(R.id.treeAllContact);
			m_InitInst=(InitializerCls)(getIntent().getParcelableExtra(InitializerCls.class.getCanonicalName()));
			registerForContextMenu(treeAllContact);

			txtSearch.setOnKeyListener(new OnKeyListener(){
				@Override
				public boolean onKey(View vw, int keyCode, KeyEvent event){
					if (event.getAction() == KeyEvent.ACTION_DOWN)
						if (keyCode == KeyEvent.KEYCODE_ENTER){
							btnSearch_Click(vw);	
							return true;
						}
					
					return false;
	            }
	        });
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

			getAllContactData();
			treeAllContact.setOnChildClickListener(new OnChildClickListener(){

				@Override
				public boolean onChildClick(ExpandableListView parent, View viewItm,
						int groupPosition, int childPosition, long id) {
					boolean result=false;
					
					try{
						int personId=(int)(m_AllContactsAdapter.getChildId(groupPosition, childPosition));
						makeActionWithContact(ConstCls.BROWSE_OP,personId);
					}
					catch(Exception ex){
						
					}
					
					return result;
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
	
	/** Searches contact */
	public void btnSearch_Click(View vw){
		
		try{
			String strSearchable=txtSearch.getText().toString();
			
			if(!strSearchable.trim().equalsIgnoreCase("")&&m_PersonList!=null&&!m_PersonList.isEmpty()){
				PersonCls contactObj=new PersonCls(m_DbWrkInst);
				
				if(contactObj!=null){
					m_PersonList=contactObj.searchContactByName(strSearchable.trim());
					
					if(m_PersonList!=null&&m_PersonList.size()>0){
						m_AllContactsAdapter=new CustomExpListAdapter(this,m_PersonList);
						treeAllContact.setAdapter(m_AllContactsAdapter);
						
						if(m_AllContactsAdapter!=null&&m_AllContactsAdapter.getGroupCount()>0)
							for(int i=0;i<m_AllContactsAdapter.getGroupCount();i++)
								treeAllContact.expandGroup(i);
					}
					else{
						Toast.makeText(this, "Поиск не был успешным", Toast.LENGTH_LONG).show();
						getAllContactData();
					}
				}
			}
			else
				getAllContactData();
		}
		catch(Exception ex){
			String strErr="Ошибка поиска - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this,strErr,Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Adds, updates or deletes contact */
	private void makeActionWithContact(int actionId,int personId){
		try{
			Intent intent = new Intent(this, actionId==ConstCls.BROWSE_OP?BrowseContactActivity.class:AddNewContactActivity.class);
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

	/** Refreshes all contacts data in contact list */
	private void getAllContactData(){
		try{
			txtSearch.setText("");
			PersonCls contactObj=new PersonCls(m_DbWrkInst);
			
			if(contactObj!=null){
				m_PersonList=contactObj.getAllContactList();
				
				if(m_PersonList!=null&&m_PersonList.size()>0){
					m_AllContactsAdapter=new CustomExpListAdapter(this,m_PersonList);
					treeAllContact.setAdapter(m_AllContactsAdapter);
				}
			}
		}
		catch(Exception ex){
			String strErr="Ошибка получения всех контактов - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Retrieves results from operations activities */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    try{
	    	if(resultCode==RESULT_CANCELED)
	    		return;
	    	
	    	if (data == null) 
		    	return;
			
	    	switch(requestCode){
		    	case ConstCls.CONTACT_REC:
		    		getAllContactData();
		    	break;
	    	}
	    }
	    catch(Exception ex){
	    	String strErr="Ошибка данных с дочерней активити - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
			return;
	    }
	}
	
	/** Occurs on options menu creation */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/** Occurs on context menu creation */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.activity_main_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/** Occurs on context menu item clicking */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		try{
			ExpandableListContextMenuInfo mnuItmCtx=(ExpandableListContextMenuInfo)item.getMenuInfo();

			if(ExpandableListView.getPackedPositionType(mnuItmCtx.packedPosition)!=ExpandableListView.PACKED_POSITION_TYPE_NULL
					&&ExpandableListView.getPackedPositionType(mnuItmCtx.packedPosition)==ExpandableListView.PACKED_POSITION_TYPE_CHILD){
				
				int personId=(int)(m_AllContactsAdapter.getChildId(ExpandableListView.getPackedPositionGroup(mnuItmCtx.packedPosition), 
                             ExpandableListView.getPackedPositionChild(mnuItmCtx.packedPosition)));
				
				switch(item.getItemId()){
					case R.id.mnuCtxBrowsePerson:
						makeActionWithContact(ConstCls.BROWSE_OP,personId);
					break;
					
					case R.id.mnuCtxInsertPerson:
						makeActionWithContact(ConstCls.INSERT_OP,-1);				
					break;
									
					case R.id.mnuCtxUpdatePerson:
						makeActionWithContact(ConstCls.UPDATE_OP,personId);
					break;
					
					case R.id.mnuCtxDeletePerson:
						makeActionWithContact(ConstCls.DELETE_OP,personId);
					break;
				}
			}
		}
		catch(Exception ex){
			String strErr="Ошибка onContextItemSelected - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
		}
		
		return super.onContextItemSelected(item);
	}
	
	/** Occurs on option menu item clicking*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()){
				case R.id.mnuRefreshAll:
					getAllContactData();
				break;
				
				case R.id.mnuAddPerson:
					makeActionWithContact(ConstCls.INSERT_OP,-1);		
				break;

				case R.id.mnuSettings:{
					Intent settingsIntn=new Intent(this,SettingsActivity.class);
					settingsIntn.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
					startActivity(settingsIntn);
				}
				break;
				
				case R.id.mnuAbout:{
					AboutFragment dlgAbout=new AboutFragment();
					dlgAbout.show(this.getFragmentManager(), "О проге...");
				}
				break;
				
				case R.id.mnuExit:
					this.finish();
				break;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка onOptionItemSelected - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
		}
		
		return super.onOptionsItemSelected(item);
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
