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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FieldSettingsTab extends Activity {
	
	private ListView lstAllField;
	private EditText txtFieldName;
	private Spinner spnrFieldType;
	private ListView lstFieldIntension;
	
	private final String LOG_TAG="iBook - FieldSettingsTab";
	private InitializerCls m_InitInst=null;
	private DbWorkerCls m_DbWrkInst=null;
	private FieldListAdapterCls m_AllFieldsAdapter=null;
	private IntensionListAdapterCls m_IntntLstAdapter=null;
	private FieldTypeListAdapterCls m_FldTypeLstAdapter=null;
	private int m_CurrentFieldId=-1;
	
	/** Occurs on current activity loading */
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_settings_field);
	    
	    try{
	    	lstAllField=(ListView)this.findViewById(R.id.lstAllFields);
	    	txtFieldName=(EditText)this.findViewById(R.id.txtField);
	    	spnrFieldType=(Spinner)this.findViewById(R.id.spnrFieldType);
	    	lstFieldIntension=(ListView)this.findViewById(R.id.lstIntents);
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
			
			getAllFields();
			getAllIntents(null);
			getAllFieldTypes(null);
			
			lstAllField.setOnItemClickListener(new OnItemClickListener(){
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 if(m_AllFieldsAdapter!=null){
						 m_AllFieldsAdapter.m_SelectedIndex=position;
						 FieldCls selectedFldItm=m_AllFieldsAdapter.getItmAsFieldCls(position);
						 m_CurrentFieldId=selectedFldItm.m_FieldId;
						 txtFieldName.setText(selectedFldItm.m_FieldName);
						 getAllFieldTypes(selectedFldItm.m_FieldType);
						 getAllIntents(selectedFldItm.m_IntentActionArr);
						 m_AllFieldsAdapter.notifyDataSetChanged();
					 }
				 }
			});
			
			lstFieldIntension.setOnItemClickListener(new OnItemClickListener(){
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 if(m_IntntLstAdapter!=null){
						 m_IntntLstAdapter.setIntentChecked(position);
						 m_IntntLstAdapter.notifyDataSetChanged();
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

	/** Retrieves all fields */
	private void getAllFields(){
		try{
			FieldCls fldObj=new FieldCls(m_DbWrkInst);
			ArrayList<FieldCls> allFieldLst=fldObj.getAllFieldInfo();
			
			if(allFieldLst==null||allFieldLst.size()==0)
				throw new Exception("Список полей пуст");
				
			m_AllFieldsAdapter=new FieldListAdapterCls(this,allFieldLst);
			lstAllField.setAdapter(m_AllFieldsAdapter);
			txtFieldName.setText("");
			m_IntntLstAdapter.clearSelectedList();
			m_IntntLstAdapter.notifyDataSetChanged();
		}
		catch(Exception ex){
			String strErr="Ошибка в getAllFields - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Retrieves all field intents */
	private void getAllIntents(ArrayList<IntensionCls> selectedIntentLst){
		try{
			IntensionCls intnObj=new IntensionCls(this,m_DbWrkInst);
			ArrayList<IntensionCls> allIntentLst=intnObj.getAllIntentInfo();
			
			if(allIntentLst==null||allIntentLst.size()==0)
				throw new Exception("Список интентов пуст");
			
			m_IntntLstAdapter=new IntensionListAdapterCls(this,allIntentLst);
			lstFieldIntension.setAdapter(m_IntntLstAdapter);
			
			if(selectedIntentLst!=null){
				for(int j=0;j<selectedIntentLst.size();j++)	
					for(int i=0;i<allIntentLst.size();i++)
						if(allIntentLst.get(i).equals(selectedIntentLst.get(j))){
							m_IntntLstAdapter.setIntentChecked(i);
							break;
						}
				
				m_IntntLstAdapter.notifyDataSetChanged();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в getAllIntents - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Retrieves all field data types */
	private void getAllFieldTypes(FieldTypeCls selectedFieldType){
		try{
			FieldTypeCls fieldTypeInst=new  FieldTypeCls(m_DbWrkInst);
			ArrayList<FieldTypeCls> allFieldTypes=fieldTypeInst.getAllFieldTypes();
			
			if(allFieldTypes==null||allFieldTypes.size()==0)
				throw new Exception("Список типов полей пуст");
			
			m_FldTypeLstAdapter=new  FieldTypeListAdapterCls(this,allFieldTypes);
			spnrFieldType.setAdapter(m_FldTypeLstAdapter);
			
			if(selectedFieldType!=null)
				for(int i=0;i<allFieldTypes.size();i++)
					if(allFieldTypes.get(i).equals(selectedFieldType)){
						spnrFieldType.setSelection(i);
						break;
					}
		}
		catch(Exception ex){
			String strErr="Ошибка в getAllFieldTypes - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Occurs on adding new field */
	public void btnAddField_Click(View vw){
		try{
			String fieldName=txtFieldName.getText().toString().trim();
			
			if(fieldName.equals(""))
				throw new Exception("Не указано имя поля");
			
			FieldCls fldItm=new FieldCls(m_DbWrkInst);
			
			if(fldItm.checkFieldExistence(-1,fieldName))
				throw new Exception("Поле уже существует");
		
			ArrayList<IntensionCls> intensionsArr=m_IntntLstAdapter.m_SelectedItemLst;
			
			if(intensionsArr.size()==0)
				intensionsArr.add(new IntensionCls(this,m_DbWrkInst).getIntent(-1, "None"));
			
			if(!fldItm.addField(fieldName,m_FldTypeLstAdapter.getItmAsFieldCls(spnrFieldType.getSelectedItemPosition()),intensionsArr))
				throw new Exception("Не удалось добавить поле");
			
			getAllFields();
		}
		catch(Exception ex){
			String strErr="Ошибка в btnAddField_Click - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Occurs on editing existing field */
	public void btnUpdateField_Click(View vw){
		try{
			if(m_CurrentFieldId<1)
				throw new Exception("Поле не выбрано");
			
			String fieldName=txtFieldName.getText().toString().trim();
			
			if(fieldName.equals(""))
				throw new Exception("Не указано имя поля");
			
			FieldCls fldItm=new FieldCls(m_DbWrkInst);
			
			ArrayList<IntensionCls> intensionsArr=m_IntntLstAdapter.m_SelectedItemLst;
			
			if(intensionsArr.size()==0)
				intensionsArr.add(new IntensionCls(this,m_DbWrkInst).getIntent(-1, "None"));
			
			if(!fldItm.updateField(m_CurrentFieldId,"",fieldName,
					m_FldTypeLstAdapter.getItmAsFieldCls(spnrFieldType.getSelectedItemPosition()),intensionsArr))
				throw new Exception("Не удалось изменить группу");
			
			getAllFields();
		}
		catch(Exception ex){
			String strErr="Ошибка в btnUpdateField_Click - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	/** Occurs on deleting existing field */
	public void btnDeleteField_Click(View vw){
		try{
			if(m_CurrentFieldId<1)
				throw new Exception("Поле не выбрано");
			
			FieldCls fldItm=new FieldCls(m_DbWrkInst);
			
			if(!fldItm.deleteField(m_CurrentFieldId, ""))
				throw new Exception("Поле не удалено");
			
			getAllFields();
		}
		catch(Exception ex){
			String strErr="Ошибка в btnDeleteField_Click - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Releases all resources */
	
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
