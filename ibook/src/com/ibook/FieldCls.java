package com.ibook;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class FieldCls implements Comparator<FieldCls>,Parcelable  {
	
	private DbWorkerCls m_DbWrkObj=null;
	public int m_FieldId=-1;
	public String m_FieldName=new String("");
	public int m_FieldTypeId=-1;
	public FieldTypeCls m_FieldType=null;
	public int[] m_IntentIdArr=null;
	public ArrayList<IntensionCls> m_IntentActionArr=null;
	private final String TABLE="FIELD_TBL";
	private final String LOG_TAG="iBook - FieldCls";
	public boolean m_IsSelected=false;
	
	/** Default constructor */
	public FieldCls(){}
	
	/** Not default constructor */
	public FieldCls(DbWorkerCls dbWrkObj){
		m_DbWrkObj=dbWrkObj;
	}

	/** Not default constructor */
	public FieldCls(Parcel prcl){
		try{
			m_FieldId=prcl.readInt();
			m_FieldName=prcl.readString();
			m_FieldTypeId=prcl.readInt();
			m_FieldType=new FieldTypeCls(m_FieldTypeId,prcl.readString());
			String[] strIntentArr=prcl.readString().split(",");
			m_IntentIdArr=new int[strIntentArr.length];
			m_IntentActionArr=new ArrayList<IntensionCls>();
			
			for(int i=0;i<strIntentArr.length;i++){
				m_IntentIdArr[i]=Integer.parseInt(strIntentArr[i]);
				m_IntentActionArr.add((new IntensionCls(null,m_DbWrkObj)).getIntent(m_IntentIdArr[i], ""));
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в Parcel конструкторе1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Not default constructor */
	public FieldCls(int fieldId, String fieldName,FieldTypeCls fieldType,ArrayList<IntensionCls> intentActionArr){
		try{
			m_FieldId=fieldId;
			m_FieldName=fieldName;
			m_FieldTypeId=fieldType!=null?fieldType.m_FieldTypeId:-1;
			m_FieldType=fieldType;
			m_IntentActionArr=intentActionArr;
			
			if(intentActionArr!=null&&intentActionArr.size()>0){
				m_IntentIdArr=new int[intentActionArr.size()];
				
				for(int i=0;i<intentActionArr.size();i++)
					m_IntentIdArr[i]=intentActionArr.get(i).m_IntentId;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в конструкторе2 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Not default constructor */
	public FieldCls(DbWorkerCls dbWrkObj,int fieldId, String fieldName,FieldTypeCls fieldType,ArrayList<IntensionCls> intentActionArr){
		try{
			m_DbWrkObj=dbWrkObj;
			m_FieldId=fieldId;
			m_FieldName=fieldName;
			m_FieldTypeId=fieldType!=null?fieldType.m_FieldTypeId:-1;
			m_FieldType=fieldType;
			m_IntentActionArr=intentActionArr;
			
			if(intentActionArr!=null&&intentActionArr.size()>0){
				m_IntentIdArr=new int[intentActionArr.size()];
				
				for(int i=0;i<intentActionArr.size();i++)
					m_IntentIdArr[i]=intentActionArr.get(i).m_IntentId;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в конструкторе - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Returns all fields info*/
	public ArrayList<FieldCls> getAllFieldInfo(){
		ArrayList<FieldCls> result=null;

		try{
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select a.field_id, a.field_name, "+
					" a.type_id, b.type_name, a.intent_id from "+TABLE+" a " +
					" left join FIELD_TYPE_TBL b on b.type_id=a.type_id "+
					" order by a.field_name", new String[]{});
			
			if(reader!=null)
			{
				if(reader.moveToFirst()){
					result=new ArrayList<FieldCls>();
					
					do{
						String[] strIntentArr=reader.getString(4).split(",");
						m_IntentIdArr=new int[strIntentArr.length];
						ArrayList<IntensionCls> intentActionArr=new ArrayList<IntensionCls>();
						
						for(int i=0;i<strIntentArr.length;i++){
							m_IntentIdArr[i]=Integer.parseInt(strIntentArr[i]);
							IntensionCls intnInst=(new IntensionCls(null,m_DbWrkObj)).getIntent(m_IntentIdArr[i], "");
							intentActionArr.add(intnInst);
						}

						result.add(new FieldCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey),
								new FieldTypeCls(reader.getInt(2),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(3),m_DbWrkObj.m_CryptKey)),
								intentActionArr));
					}
					while(reader.moveToNext());
				}
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных инфы о записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns field info by its id or name*/
	public FieldCls getField(int fieldId,String fieldName){
		FieldCls result=null;
		
		try{
			String[] queryParams=fieldId!=-1?new String[]{""+fieldId}:(fieldName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(fieldName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldId!=-1?" where a.field_id=?":(fieldName.trim().equals("")?"":" where trim(lower(a.field_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select a.field_id, a.field_name, a.type_id, b.type_name, a.intent_id from "+TABLE+" a " +
					" left join FIELD_TYPE_TBL b on b.type_id=a.type_id "+strCondition, queryParams);
			
			if(reader!=null)
			{
				if(reader.moveToFirst()){
					String[] strIntentArr=reader.getString(4).split(",");
					m_IntentIdArr=new int[strIntentArr.length];
					ArrayList<IntensionCls> intentActionArr=new ArrayList<IntensionCls>();
					
					for(int i=0;i<strIntentArr.length;i++){
						m_IntentIdArr[i]=Integer.parseInt(strIntentArr[i]);
						intentActionArr.add((new IntensionCls(null,m_DbWrkObj)).getIntent(m_IntentIdArr[i], ""));
					}
					
					result=new FieldCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey),
							new FieldTypeCls(reader.getInt(2),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(3),m_DbWrkObj.m_CryptKey)),
							intentActionArr);
				}
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных инфы о записи1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Checks field existence by its id or name*/
	public boolean checkFieldExistence(int fieldId,String fieldName){
		boolean result=false;
		
		try{
			String[] queryParams=fieldId!=-1?new String[]{""+fieldId}:(fieldName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(fieldName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldId!=-1?" where field_id=?":(fieldName.trim().equals("")?"":" where trim(lower(field_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select count(1) as cnt from "+TABLE+strCondition, queryParams);
			
			if(reader!=null){
				if(reader.moveToFirst())
					result=(reader.getInt(0)>0);
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка проверки данных поля - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Adds new field info */
	public boolean addField(String fieldName,FieldTypeCls fieldType,ArrayList<IntensionCls> intentInstArr){
		boolean result=false;
		
		try{
			ContentValues cv = new ContentValues();
			cv.put("field_name", m_DbWrkObj.m_CryptInst.Crypt(fieldName,m_DbWrkObj.m_CryptKey));
			cv.put("type_id", fieldType.m_FieldTypeId);
			String strIntentIdLst="";
			
			if(intentInstArr!=null&&intentInstArr.size()>0)
				for(int i=0;i<intentInstArr.size();i++)
					strIntentIdLst=strIntentIdLst+(strIntentIdLst.trim().equals("")?"":",")+intentInstArr.get(i).m_IntentId;
			
			cv.put("intent_id", strIntentIdLst);
			result=(m_DbWrkObj.m_CurrentDb.insert(TABLE, null, cv)>-1);
		}
		catch(Exception ex){
			String strErr="Ошибка добавления данных инфы о записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Updates field info */
	public boolean updateField(int fieldId,String oldFieldName,String newFieldName,FieldTypeCls fieldType,ArrayList<IntensionCls> intentInstArr){
		boolean result=false;
		
		try{
			ContentValues cv = new ContentValues();
			cv.put("field_name", m_DbWrkObj.m_CryptInst.Crypt(newFieldName,m_DbWrkObj.m_CryptKey));
			cv.put("type_id", fieldType.m_FieldTypeId);
			String strIntentIdLst="";
			
			if(intentInstArr!=null&&intentInstArr.size()>0)
				for(int i=0;i<intentInstArr.size();i++)
					strIntentIdLst=strIntentIdLst+(strIntentIdLst.trim().equals("")?"":",")+intentInstArr.get(i).m_IntentId;
			
			cv.put("intent_id", strIntentIdLst);
			String[] queryParams=fieldId!=-1?new String[]{""+fieldId}:(oldFieldName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(oldFieldName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldId!=-1?"field_id=?":(oldFieldName.trim().equals("")?"":"trim(lower(field_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.update(TABLE, cv, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка обновления данных инфы о записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Deletes field info */
	public boolean deleteField(int fieldId,String fieldName){
		boolean result=false;
		
		try{
			String[] queryParams=fieldId!=-1?new String[]{""+fieldId}:(fieldName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(fieldName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldId!=-1?"field_id=?":(fieldName.trim().equals("")?"":"trim(lower(field_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.delete(TABLE, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка удаления данных инфы о записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	@Override
	public int compare(FieldCls lhs, FieldCls rhs) {
		int result=1;
		
		try{
			if(lhs.m_FieldId==rhs.m_FieldId&&lhs.m_FieldName.trim().equalsIgnoreCase(rhs.m_FieldName)
					&&lhs.m_FieldType.equals(rhs.m_FieldType))
				result=0;
		}
		catch(Exception ex){
			String strErr="Ошибка сравнения объектов - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return 1;
		}
		
		return result;
	}

	@Override
	public boolean equals(Object inst){
		boolean result=false;
		
		try{
			FieldCls fieldInst=(FieldCls)inst;
			result=(this.m_FieldId==fieldInst.m_FieldId&&this.m_FieldName.trim().equalsIgnoreCase(fieldInst.m_FieldName)
					&&this.m_FieldType.equals(fieldInst.m_FieldType));
		}
		catch(Exception ex){
			String strErr="Ошибка сравнения объектов1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		try{
			dest.writeInt(m_FieldId);
			dest.writeString(m_FieldName);
			dest.writeInt(m_FieldType==null?1:m_FieldType.m_FieldTypeId);
			dest.writeString(m_FieldType==null?"string":m_FieldType.m_FieldTypeName);
			String strIntentIdLst="";
			
			if(m_IntentActionArr!=null&&m_IntentActionArr.size()>0)
				for(int i=0;i<m_IntentActionArr.size();i++)
					strIntentIdLst=strIntentIdLst+(strIntentIdLst.trim().equals("")?"":",")+m_IntentActionArr.get(i).m_IntentId;
			
			dest.writeString(strIntentIdLst);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<FieldCls> CREATOR = new Parcelable.Creator<FieldCls>() { 
		public FieldCls createFromParcel(Parcel prcl) {
	    	return new FieldCls(prcl);
		}

	    public FieldCls[] newArray(int size) {
	      return new FieldCls[size];
	    }
	};
}
