package com.ibook;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class FieldTypeCls implements Comparator<FieldTypeCls>, Parcelable  {
	private DbWorkerCls m_DbWrkObj=null;
	public int m_FieldTypeId=-1;
	public String m_FieldTypeName=new String("");
	public boolean m_IsSelected=false;
	private static final String TABLE="FIELD_TYPE_TBL";
	private final String LOG_TAG="iBook - FieldTypeCls";
	
	/** Default constructor */
	public FieldTypeCls(){}
	
	/** Not default constructor */
	public FieldTypeCls(DbWorkerCls dbWrkObj){
		m_DbWrkObj=dbWrkObj;
	}
	
	/** Not default constructor */
	public FieldTypeCls(Parcel prcl){
		m_FieldTypeId=prcl.readInt();
		m_FieldTypeName=prcl.readString();
	}
	
	/** Not default constructor */
	public FieldTypeCls(int fieldTypeId,String fieldTypeName){
		m_FieldTypeId=fieldTypeId;
		m_FieldTypeName=fieldTypeName;
	}
	
	/** Not default constructor */
	public FieldTypeCls(DbWorkerCls dbWrkObj,int fieldTypeId,String fieldTypeName){
		m_DbWrkObj=dbWrkObj;
		m_FieldTypeId=fieldTypeId;
		m_FieldTypeName=fieldTypeName;
	}
	
	/** Retrieves all field types */
	public ArrayList<FieldTypeCls> getAllFieldTypes(){
		ArrayList<FieldTypeCls> result=null;
		
		try{
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select type_id,type_name from "+TABLE, new String[]{});
			
			if(reader!=null)
			{
				if(reader.moveToFirst()){
					result=new ArrayList<FieldTypeCls>();
					
					do{
						result.add(new FieldTypeCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey)));
					}
					while(reader.moveToNext());
				}
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки всех типов столбца - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns group info by its id or name*/
	public FieldTypeCls getFieldType(int fieldTypeId,String fieldTypeName){
		FieldTypeCls result=null;
		
		try{
			String[] queryParams=fieldTypeId!=-1?new String[]{""+fieldTypeId}:(fieldTypeName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(fieldTypeName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldTypeId!=-1?" where type_id=?":(fieldTypeName.trim().equals("")?"":" where trim(lower(type_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select type_id,type_name from "+TABLE+strCondition, queryParams);
			
			if(reader!=null)
			{
				if(reader.moveToFirst()){
					result=new FieldTypeCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey));
				}
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных типа столбца - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Adds new group */
	public boolean addFieldType(String fieldTypeName){
		boolean result=false;
		
		try{
			ContentValues cv = new ContentValues();
			cv.put("type_name", m_DbWrkObj.m_CryptInst.Crypt(fieldTypeName,m_DbWrkObj.m_CryptKey));
			result=(m_DbWrkObj.m_CurrentDb.insert(TABLE, null, cv)>-1);
		}
		catch(Exception ex){
			String strErr="Ошибка добавления данных типа столбца - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}

	/** Updates group */
	public boolean updateFieldType(int fieldTypeId,String oldFieldTypeName,String newFieldTypeName){
		boolean result=false;
		
		try{
			ContentValues cv = new ContentValues();
			cv.put("type_name", m_DbWrkObj.m_CryptInst.Crypt(newFieldTypeName,m_DbWrkObj.m_CryptKey));
			String[] queryParams=fieldTypeId!=-1?new String[]{""+fieldTypeId}:(oldFieldTypeName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(oldFieldTypeName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldTypeId!=-1?"type_id=?":(oldFieldTypeName.trim().equals("")?"":"trim(lower(type_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.update(TABLE, cv, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка обновления данных типа столбца - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Deletes group */
	public boolean deleteFieldType(int fieldTypeId,String fieldTypeName){
		boolean result=false;
		
		try{
			String[] queryParams=fieldTypeId!=-1?new String[]{""+fieldTypeId}:(fieldTypeName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(fieldTypeName,m_DbWrkObj.m_CryptKey)});
			String strCondition=fieldTypeId!=-1?"type_id=?":(fieldTypeName.trim().equals("")?"":"trim(lower(type_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.delete(TABLE, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка удаления данных типа столбца - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}

	@Override
	public int compare(FieldTypeCls lhs, FieldTypeCls rhs) {
		int result=1;
		
		try{
			if(lhs.m_FieldTypeId==rhs.m_FieldTypeId&&lhs.m_FieldTypeName.trim().equalsIgnoreCase(rhs.m_FieldTypeName))
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
			FieldTypeCls fieldTypeInst=(FieldTypeCls)inst;
			result=(this.m_FieldTypeId==fieldTypeInst.m_FieldTypeId&&this.m_FieldTypeName.trim().equalsIgnoreCase(fieldTypeInst.m_FieldTypeName));
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
			dest.writeInt(m_FieldTypeId);
			dest.writeString(m_FieldTypeName);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<FieldTypeCls> CREATOR = new Parcelable.Creator<FieldTypeCls>() { 
		public FieldTypeCls createFromParcel(Parcel prcl) {
	    	return new FieldTypeCls(prcl);
		}

	    public FieldTypeCls[] newArray(int size) {
	      return new FieldTypeCls[size];
	    }
	};
}
