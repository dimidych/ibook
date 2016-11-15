package com.ibook;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class ContactSetCls implements Comparator<ContactSetCls>, Parcelable {
	public int m_RecordId=-1;
	public int m_PersonId=-1;
	public String m_FieldValue=new String("");
	public int m_FieldId=-1;
	public FieldCls m_FieldDefinition=null;
	private DbWorkerCls m_DbWrkObj=null;
	private final String TABLE="CONTACTS_TBL";
	private final String LOG_TAG="iBook - ContactSetCls";
	public boolean m_IsSelected=false;
	
	/** Default constructor */
	public ContactSetCls(){}

	/** Not Default constructor */
	public ContactSetCls(DbWorkerCls dbWrkObj){
		m_DbWrkObj=dbWrkObj;
	}

	/** Not Default constructor */
	public ContactSetCls(Parcel prcl){
		m_RecordId=prcl.readInt();
		m_PersonId=prcl.readInt();
		m_FieldValue=prcl.readString();
		m_FieldId=prcl.readInt();
	}
	
	/** Not default constructor */
	public ContactSetCls(int recordId,int personId,FieldCls fieldDefinition,String fieldValue){
		m_RecordId=recordId;
		m_PersonId=personId;
		m_FieldId=fieldDefinition!=null?fieldDefinition.m_FieldId:-1;
		m_FieldDefinition=fieldDefinition;
		m_FieldValue=fieldValue;
	}

	/** Not default constructor */
	public ContactSetCls(DbWorkerCls dbWrkObj,int recordId,int personId,FieldCls fieldDefinition,String fieldValue){
		m_DbWrkObj=dbWrkObj;
		m_RecordId=recordId;
		m_PersonId=personId;
		m_FieldId=fieldDefinition!=null?fieldDefinition.m_FieldId:-1;
		m_FieldDefinition=fieldDefinition;
		m_FieldValue=fieldValue;
	}
	
	/** Checks whether contact record exists */
	public boolean checkContactFieldsExistance(int personId,String fieldValue, FieldCls fieldDefinition){
		boolean result=false;

		try{
			String[] queryParams=personId>-1?new String[]{""+personId,""+fieldDefinition.m_FieldId,m_DbWrkObj.m_CryptInst.Crypt(fieldValue,m_DbWrkObj.m_CryptKey)}:null;
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select count(1) as cnt from "+TABLE+
					" where person_id=? and field_id=? and trim(lower(field_value))=trim(lower(?))",queryParams);
			
			if(reader!=null){
				if(reader.moveToFirst())
					result=(reader.getInt(0)>0);
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки контактных данных - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}

	/** Retrieves contact lines for person */
	public ArrayList<ContactSetCls> getPersonRelatedFields(int personId){
		ArrayList<ContactSetCls> result=null;
		
		try{
			String[] queryParams=personId>-1?new String[]{""+personId}:null;
			String queryCondition=personId>-1?" where a.person_id=?":"";
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select a.person_id, b.field_id, b.field_value," +
					" b.contact_id from PERSON_TBL a  "+
					" left join CONTACTS_TBL b on b.person_id=a.person_id "+queryCondition,queryParams);
			
			if(reader!=null){
				result=new ArrayList<ContactSetCls>();
				
				if(reader.moveToFirst())
					do{
						result.add(new ContactSetCls(reader.getInt(3),reader.getInt(0),
								(new FieldCls(m_DbWrkObj)).getField(reader.getInt(1), ""),
								m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(2),m_DbWrkObj.m_CryptKey)));
					}
					while(reader.moveToNext());
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки контактных данных1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}

		return result;
	}

	/** Adds new contact records */
	public boolean addContactFields(ArrayList<ContactSetCls> contactsLst){
		boolean result=false;

		try{
			
			if(contactsLst!=null&&contactsLst.size()>0){
				int counter=0;
				
				for(int i=0;i<contactsLst.size();i++){
					try{
						ContentValues cv = new ContentValues();
						cv.put("person_id", contactsLst.get(i).m_PersonId);
						cv.put("field_id", contactsLst.get(i).m_FieldDefinition.m_FieldId);
						cv.put("field_value", m_DbWrkObj.m_CryptInst.Crypt(contactsLst.get(i).m_FieldValue,m_DbWrkObj.m_CryptKey));
					
						if(m_DbWrkObj.m_CurrentDb.insert(TABLE, null, cv)>-1)
							counter++;
					}catch(Exception ex){}
				}
				
				if(counter!=contactsLst.size())
					throw new Exception("Не все контактных данные были добавлены");
				
				result=true;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка добавления контактных данных - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Updates contact records */
	public boolean updateContactFields(ArrayList<ContactSetCls> newContactsLst){
		boolean result=false;

		try{
			if(newContactsLst!=null&&newContactsLst.size()>0){
				int counter=0;
				String strCondition="person_id=? and contact_id=? and field_id=?";
				
				for(int i=0;i<newContactsLst.size();i++){
					try{
						ContentValues cv = new ContentValues();
						cv.put("field_value", m_DbWrkObj.m_CryptInst.Crypt(newContactsLst.get(i).m_FieldValue,m_DbWrkObj.m_CryptKey));
						String[] queryParams=new String[]{""+newContactsLst.get(i).m_PersonId,""+newContactsLst.get(i).m_RecordId,""+newContactsLst.get(i).m_FieldDefinition.m_FieldId};
						
						if(m_DbWrkObj.m_CurrentDb.update(TABLE, cv, strCondition, queryParams)>0)
							counter++;
					}catch(Exception ex){}
				}
				
				if(counter!=newContactsLst.size())
					throw new Exception("Не все контактных данные были изменены");
				
				result=true;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка изменения контактных данных - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Deletes contact records */
	public boolean deleteContactFields(ContactSetCls contactSet,byte dropRelatedToPerson,byte dropRelatedToPersonGroup){
		boolean result=false;

		try{
			String strCondition="";
			
			if(dropRelatedToPersonGroup==1)
				strCondition="person_id in (select t.person_id from PERSON_TBL t where t.group_id="+
						"(select x.group_id from PERSON_TBL x where x.person_id=?))";
			else if(dropRelatedToPerson==1)
				strCondition="person_id=?";
			else
				strCondition="person_id=? and contact_id=? and field_id=?";
			
			String[] queryParams=dropRelatedToPerson==1||dropRelatedToPersonGroup==1?new String[]{""+contactSet.m_PersonId}:
				new String[]{""+contactSet.m_PersonId,""+contactSet.m_RecordId,""+contactSet.m_FieldDefinition.m_FieldId};
			result=(m_DbWrkObj.m_CurrentDb.delete(TABLE, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка удаления контактных данных - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}

	@Override
	public int compare(ContactSetCls lhs, ContactSetCls rhs) {
		int result=1;
		
		try{
			if(lhs.m_FieldValue.trim().equalsIgnoreCase(rhs.m_FieldValue)&&lhs.m_FieldDefinition.equals(rhs.m_FieldDefinition))
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
			ContactSetCls contactRecInst=(ContactSetCls)inst;
			result=(this.m_FieldValue.trim().equalsIgnoreCase(contactRecInst.m_FieldValue)&&this.m_FieldDefinition.equals(contactRecInst.m_FieldDefinition));
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
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		try{
			dest.writeInt(m_RecordId);
			dest.writeInt(m_PersonId);
			dest.writeString(m_FieldValue);
			dest.writeInt(m_FieldDefinition==null?1:m_FieldDefinition.m_FieldId);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<ContactSetCls> CREATOR = new Parcelable.Creator<ContactSetCls>() { 
		public ContactSetCls createFromParcel(Parcel prcl) {
	    	return new ContactSetCls(prcl);
		}

	    public ContactSetCls[] newArray(int size) {
	      return new ContactSetCls[size];
	    }
	};
}
