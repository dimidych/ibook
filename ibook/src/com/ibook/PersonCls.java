package com.ibook;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class PersonCls implements Parcelable {
	public int m_PersonId=-1;
	public String m_PersonName=new String("");
	public GroupCls m_GroupObj=null;
	public int m_GroupId=-1;
	public ArrayList<ContactSetCls> m_ContactRecordSet=null;
	private DbWorkerCls m_DbWrkObj=null;
	private final String TABLE="PERSON_TBL";
	private final String LOG_TAG="iBook - PersonCls";
	public boolean m_IsSelected=false;
	
	/** Default constructor */
	public PersonCls(){	}

	/** Not Default constructor */
	public PersonCls(DbWorkerCls dbWrkObj){
		m_DbWrkObj=dbWrkObj;
	}

	/** Not Default constructor */
	public PersonCls(Parcel prcl){
		m_PersonId=prcl.readInt();
		m_PersonName=prcl.readString();
		m_GroupId=prcl.readInt();
	}
	
	/** Not default constructor */
	public PersonCls(int personId,String personName,GroupCls groupObj,ArrayList<ContactSetCls> contactRecordSet){
		m_PersonId=personId;
		m_PersonName=personName;
		m_GroupId=groupObj!=null?groupObj.m_GroupId:-1;
		m_GroupObj=groupObj;
		m_ContactRecordSet=contactRecordSet;
	}
	
	/** Not default constructor */
	public PersonCls(DbWorkerCls dbWrkObj,int personId,String personName,GroupCls groupObj,ArrayList<ContactSetCls> contactRecordSet){
		m_DbWrkObj=dbWrkObj;
		m_PersonId=personId;
		m_PersonName=personName;
		m_GroupId=groupObj!=null?groupObj.m_GroupId:-1;
		m_GroupObj=groupObj;
		m_ContactRecordSet=contactRecordSet;
		
	}

	/** Returns contact info with group info and cotact record set by its id or name*/
	public PersonCls getContactInfo(int personId,String personName){
		/*PersonCls result=null;

		try{
			String[] queryParams=personId!=-1?new String[]{""+personId}:(personName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(personName,m_DbWrkObj.m_CryptKey)});
			String strCondition=personId!=-1?" where person_id=?":(personName.trim().equals("")?"":" where trim(lower(person_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select person_id,person_name,group_id from PERSON_TBL "+strCondition, queryParams);
			
			if(reader!=null)
			{
				result=new PersonCls();
				
				if(reader.moveToFirst()){
					result.m_PersonId=reader.getInt(0);
					result.m_PersonName=m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey);
					m_GroupId=reader.getInt(2);
					result.m_GroupObj=(new GroupCls(m_DbWrkObj)).getGroupInfo(m_GroupId, "");
					result.m_ContactRecordSet=(new ContactSetCls(m_DbWrkObj)).getPersonRelatedFields(personId);
				}

				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}

		return result;*/
		
		return getAllContactList(personId,personName,null,(byte)1,(byte)1,(byte)0,(byte)1).get(0);
	}
	
	/** Returns contact info without  cotact record set by its id or name*/
	public PersonCls getContactInfoTrunc(int personId,String personName){
		/*PersonCls result=null;

		try{
			String[] queryParams=personId!=-1?new String[]{""+personId}:(personName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(personName,m_DbWrkObj.m_CryptKey)});
			String strCondition=personId!=-1?" where person_id=?":(personName.trim().equals("")?"":" where trim(lower(person_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select person_id,person_name from PERSON_TBL "+strCondition, queryParams);
			
			if(reader!=null){
				result=new PersonCls();
				
				if(reader.moveToFirst()){
					result.m_PersonId=reader.getInt(0);
					result.m_PersonName=m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey);
					result.m_GroupObj=null;
					result.m_ContactRecordSet=null;
				}

				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных контакта1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;*/
		
		return  getAllContactList(personId,personName,null,(byte)0,(byte)0,(byte)0,(byte)1).get(0);
	}

	/** Returns searched contacts list */
	public ArrayList<PersonCls> searchContactByName(String strSearchCondition){
		ArrayList<PersonCls> result=null;

		try{
			result=getAllContactList(-1,strSearchCondition,null,(byte)1,(byte)0,(byte)1,(byte)0);
			
			if(result!=null&&result.size()>0)
				return result;
			
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select distinct person_id, person_name, group_id from PERSON_TBL "+
					" where person_id in (select t.person_id from CONTACTS_TBL t where trim(lower(t.field_value)) like "+
					"trim(lower('%"+m_DbWrkObj.m_CryptInst.Crypt(strSearchCondition,m_DbWrkObj.m_CryptKey)+
					"%'))) order by group_id, person_name ", new String[]{});
			
			if(reader!=null){
				result=new ArrayList<PersonCls>();
				
				if(reader.moveToFirst())
					do{
						PersonCls contactInfo=new PersonCls();
						contactInfo.m_PersonId=reader.getInt(0);
						contactInfo.m_PersonName=m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey);
						contactInfo.m_GroupId=reader.getInt(2);
						contactInfo.m_GroupObj=(new GroupCls(m_DbWrkObj)).getGroupInfo(contactInfo.m_GroupId, "");
						result.add(contactInfo);
					}
					while(reader.moveToNext());

				reader.close();
			}
			
			if(result!=null&&result.size()>0)
				return result;
			
			reader = m_DbWrkObj.m_CurrentDb.rawQuery("select distinct person_id, person_name, group_id from PERSON_TBL "+
					" where group_id in (select t.group_id from PERSON_GROUP_TBL t where trim(lower(t.group_name)) like "+
					"trim(lower('%"+m_DbWrkObj.m_CryptInst.Crypt(strSearchCondition,m_DbWrkObj.m_CryptKey)+
					"%'))) order by group_id, person_name ", new String[]{});
			
			if(reader!=null){
				result=new ArrayList<PersonCls>();
				
				if(reader.moveToFirst())
					do{
						PersonCls contactInfo=new PersonCls();
						contactInfo.m_PersonId=reader.getInt(0);
						contactInfo.m_PersonName=m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey);
						contactInfo.m_GroupId=reader.getInt(2);
						contactInfo.m_GroupObj=(new GroupCls(m_DbWrkObj)).getGroupInfo(contactInfo.m_GroupId, "");
						result.add(contactInfo);
					}
					while(reader.moveToNext());

				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка поиска контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns searched contacts list, accorded to searching conditions */
	private ArrayList<PersonCls> getAllContactList(int personId,String personName,GroupCls groupObj,
			byte includeGroupInfo,byte includeContactInfo,byte useApproximateSearch, byte getTop1){
		ArrayList<PersonCls> result=null;
		
		try{
			ArrayList<String> queryParamsArr=new ArrayList<String>();
			String strCondition="";
			
			if(personId!=-1){
				queryParamsArr.add(""+personId);
				strCondition=(strCondition.trim().equals("")?" where ":strCondition+" and ")+" person_id=? ";
			}
			
			if(!personName.trim().equals("")){
				if(useApproximateSearch==1)
					strCondition=" where trim(lower(person_name)) like trim(lower('%"+
							m_DbWrkObj.m_CryptInst.Crypt(personName.trim(),m_DbWrkObj.m_CryptKey)+"%')) ";
				else{
					queryParamsArr.add(m_DbWrkObj.m_CryptInst.Crypt(personName.trim(),m_DbWrkObj.m_CryptKey));
					strCondition=(strCondition.trim().equals("")?" where ":strCondition+" and ")+" trim(lower(person_name))=trim(lower(?)) ";
				}
			}
			
			if(groupObj!=null&&groupObj.m_GroupId>0){
				queryParamsArr.add(""+groupObj.m_GroupId);
				strCondition=(strCondition.trim().equals("")?" where ":strCondition+" and ")+" group_id=? ";
			}

			String[] queryParams=queryParamsArr.isEmpty()?new String[]{}:new String[queryParamsArr.size()];
			
			for(int i=0;i<queryParams.length;i++)
				queryParams[i]=queryParamsArr.get(i);
			
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select person_id,person_name"+
					(includeGroupInfo==0?"":",group_id")+
					" from "+TABLE+" "+strCondition+" order by group_id, person_name ",queryParams);
			
			if(reader!=null){
				result=new ArrayList<PersonCls>();
				
				if(reader.moveToFirst())
					do{
						PersonCls contactInfo=new PersonCls();
						contactInfo.m_PersonId=reader.getInt(0);
						contactInfo.m_PersonName=m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey);
						
						if(includeGroupInfo==1){
							contactInfo.m_GroupId=reader.getInt(2);
							contactInfo.m_GroupObj=(new GroupCls(m_DbWrkObj)).getGroupInfo(contactInfo.m_GroupId, "");
						}
						
						if(includeContactInfo==1)
							contactInfo.m_ContactRecordSet=(new ContactSetCls(m_DbWrkObj)).getPersonRelatedFields(contactInfo.m_PersonId);
						
						result.add(contactInfo);
						
						if(getTop1==1&&contactInfo.m_PersonId>0)
							break;
					}
					while(reader.moveToNext());

				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns all contact list*/
	public ArrayList<PersonCls> getAllContactList(){
		/*ArrayList<PersonCls> result=null;

		try{
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select person_id,person_name,group_id from PERSON_TBL order by group_id, person_name ", new String[]{});
			
			if(reader!=null){
				result=new ArrayList<PersonCls>();
				
				if(reader.moveToFirst())
					do{
						PersonCls contactInfo=new PersonCls();
						contactInfo.m_PersonId=reader.getInt(0);
						contactInfo.m_PersonName=m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey);
						contactInfo.m_GroupId=reader.getInt(2);
						contactInfo.m_GroupObj=(new GroupCls(m_DbWrkObj)).getGroupInfo(contactInfo.m_GroupId, "");
						result.add(contactInfo);
					}
					while(reader.moveToNext());

				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных контакта2 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;*/
		return getAllContactList(-1,"",null,(byte)1,(byte)0,(byte)0,(byte)0);
	}
	
	/** Checks whether person with specified id or name exists */
	public boolean checkPersonExistence(int personId,String personName){
		/*boolean result=false;

		try{
			String[] queryParams=personId!=-1?new String[]{""+personId}:(personName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(personName,m_DbWrkObj.m_CryptKey)});
			String strCondition=personId!=-1?" where person_id=?":(personName.trim().equals("")?"":" where trim(lower(person_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select count(1) as cnt from PERSON_TBL "+strCondition, queryParams);
			
			if(reader!=null){
				if(reader.moveToFirst())
					result=(reader.getInt(0)>0);
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка проверки существования записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;*/
		return getAllContactList(personId,personName,null,(byte)0,(byte)0,(byte)0,(byte)0).size()>0;
	}
	
	/** Adds new person with contact record set */
	public boolean addPerson(String personName,GroupCls personGroupInfo,ArrayList<ContactSetCls> personContactSet){
		boolean result=false;

		try{
			if(personName.trim().equalsIgnoreCase(""))
				throw new Exception("Имя контакта не может быть пустым"); 
			
			if(checkPersonExistence(-1,personName))
				throw new Exception("Контакт уже существует"); 

			m_DbWrkObj.m_CurrentDb.beginTransactionNonExclusive();
			ContentValues cv = new ContentValues();
			cv.put("person_name", m_DbWrkObj.m_CryptInst.Crypt(personName,m_DbWrkObj.m_CryptKey));
			cv.put("group_id", personGroupInfo.m_GroupId);
			
			if(m_DbWrkObj.m_CurrentDb.insert(TABLE, null, cv)<0)
				throw new Exception("Не добавлено"); 
			
			int personId=getContactInfoTrunc(-1,personName).m_PersonId;
			ArrayList<ContactSetCls> modPersonContactSet=new ArrayList<ContactSetCls>();
			
			for(int i=0;i<personContactSet.size();i++){
				ContactSetCls contactInst=personContactSet.get(i);
				contactInst.m_PersonId=personId;
				modPersonContactSet.add(contactInst);
			}
			
			if(modPersonContactSet!=null&&modPersonContactSet.size()>0)
				result=(new ContactSetCls(m_DbWrkObj)).addContactFields(modPersonContactSet);
			
			m_DbWrkObj.m_CurrentDb.setTransactionSuccessful();
		}
		catch(Exception ex){
			String strErr="Ошибка добавления данных контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);;
			return false;
		}
		finally{
			try{
				if(m_DbWrkObj!=null){
					m_DbWrkObj.m_CurrentDb.endTransaction();
					//dbWrkObj.close();
				}
			}
			catch(Exception ex){
				Log.d(LOG_TAG,ex.getMessage());
			}
		}
		
		return result;
	}

	/** Updates person data */
	public boolean updatePerson(int personId,String oldPersonName,String newPersonName,GroupCls personGroupInfo,ArrayList<ContactSetCls> personContactSet){
		boolean result=false;

		try{
			if(newPersonName.trim().equalsIgnoreCase(""))
				throw new Exception("Имя контакта не может быть пустым");

			m_DbWrkObj.m_CurrentDb.beginTransactionNonExclusive();
			ContentValues cv = new ContentValues();
			cv.put("person_name", m_DbWrkObj.m_CryptInst.Crypt(newPersonName,m_DbWrkObj.m_CryptKey));
			cv.put("group_id", personGroupInfo.m_GroupId);
			String[] queryParams=personId!=-1?new String[]{""+personId}:(oldPersonName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(oldPersonName,m_DbWrkObj.m_CryptKey)});
			String strCondition=personId!=-1?"person_id=?":(oldPersonName.trim().equals("")?"":"trim(lower(person_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.update(TABLE, cv, strCondition, queryParams)>0);
			
			if(!result)
				throw new Exception("Не изменено");	
			
			ContactSetCls cntSetInst=new ContactSetCls(m_DbWrkObj);
			
			if(cntSetInst.deleteContactFields(personContactSet.get(0), (byte)1,(byte)0))
				result=cntSetInst.addContactFields(personContactSet);
			
			if(!result)
				throw new Exception("Не изменено");

			m_DbWrkObj.m_CurrentDb.setTransactionSuccessful();
		}
		catch(Exception ex){
			String strErr="Ошибка обновления данных контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		finally{
			try{
				if(m_DbWrkObj!=null){
					m_DbWrkObj.m_CurrentDb.endTransaction();
					//dbWrkObj.close();
				}
			}
			catch(Exception ex){
				Log.d(LOG_TAG,ex.getMessage());
			}
		}
		
		return result;
	}

	/** Deletes person */
	public boolean deletePerson(int personId,String personName,GroupCls groupObj){
		boolean result=false;

		try{
			m_DbWrkObj.m_CurrentDb.beginTransactionNonExclusive();
			ArrayList<String> queryParamsArr=new ArrayList<String>();
			String strCondition="";
			byte deleteWholeGroup=0;
			byte deleteOnlyPerson=0;
			
			if(personId!=-1){
				queryParamsArr.add(""+personId);
				strCondition=(strCondition.trim().equals("")?" ":strCondition+" and ")+" person_id=? ";
				deleteOnlyPerson++;
				deleteWholeGroup=0;
				groupObj=null;
			}
			
			if(!personName.trim().equals("")){
				queryParamsArr.add(m_DbWrkObj.m_CryptInst.Crypt(personName.trim(),m_DbWrkObj.m_CryptKey));
				strCondition=(strCondition.trim().equals("")?" ":strCondition+" and ")+" trim(lower(person_name))=trim(lower(?)) ";
				deleteOnlyPerson++;
				deleteWholeGroup=0;
				groupObj=null;
			}
			
			if(groupObj!=null&&groupObj.m_GroupId>0){
				queryParamsArr.add(""+groupObj.m_GroupId);
				strCondition=(strCondition.trim().equals("")?" ":strCondition+" and ")+" group_id=? ";
				deleteWholeGroup++;
				deleteOnlyPerson=0;
			}
			
			String[] queryParams=queryParamsArr.isEmpty()?new String[]{}:new String[queryParamsArr.size()];
			
			for(int i=0;i<queryParams.length;i++)
				queryParams[i]=queryParamsArr.get(i);

			ContactSetCls csObj=new ContactSetCls(m_DbWrkObj,-1,personId,new FieldCls(),"");
			result=(csObj.deleteContactFields(csObj, deleteOnlyPerson,deleteWholeGroup));		
			result=(m_DbWrkObj.m_CurrentDb.delete(TABLE, strCondition, queryParams)>0);
			m_DbWrkObj.m_CurrentDb.setTransactionSuccessful();
		}
		catch(Exception ex){
			String strErr="Ошибка удаления данных контакта - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		finally{
			try{
				if(m_DbWrkObj!=null){
					m_DbWrkObj.m_CurrentDb.endTransaction();
					//dbWrkObj.close();
				}
			}
			catch(Exception ex){
				Log.d(LOG_TAG,ex.getMessage());
			}
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
			dest.writeInt(m_PersonId);
			dest.writeString(m_PersonName);
			dest.writeInt(m_GroupObj==null?1:m_GroupObj.m_GroupId);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<PersonCls> CREATOR = new Parcelable.Creator<PersonCls>() { 
		public PersonCls createFromParcel(Parcel prcl) {
	    	return new PersonCls(prcl);
		}

	    public PersonCls[] newArray(int size) {
	      return new PersonCls[size];
	    }
	};
}
