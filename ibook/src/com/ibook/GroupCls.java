package com.ibook;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class GroupCls implements Comparator<GroupCls>, Parcelable {
	public int m_GroupId=-1;
	public String m_GroupName=new String("");
	private DbWorkerCls m_DbWrkObj=null;
	private static final String TABLE="PERSON_GROUP_TBL";
	private final String LOG_TAG="iBook - GroupCls";
	public boolean m_IsSelected=false;
	
	/** Default constructor */
	public GroupCls(){}

	/** Not Default constructor */
	public GroupCls(DbWorkerCls dbWrkObj){
		m_DbWrkObj=dbWrkObj;
	}
	
	/** Not Default constructor */
	public GroupCls(Parcel prcl){
		m_GroupId=prcl.readInt();
		m_GroupName=prcl.readString();
	}
	
	/** Not default constructor */
	public GroupCls(int groupId, String groupName){
		m_GroupId=groupId;
		m_GroupName=groupName;
	}
	
	/** Not default constructor */
	public GroupCls(DbWorkerCls dbWrkObj,int groupId, String groupName){
		m_DbWrkObj=dbWrkObj;
		m_GroupId=groupId;
		m_GroupName=groupName;
	}
	
	/** Retrieves cursor with all groups */
	public Cursor getAllGroupsAsCursor(){
		Cursor result=null;
		
		try{
			result= m_DbWrkObj.m_CurrentDb.rawQuery("select group_id,group_name from "+TABLE+" order by group_name",new String[]{});
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных группы1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns group info by its id or name*/
	public ArrayList<GroupCls> getAllGroups(){
		ArrayList<GroupCls> result=null;
		
		try{
			Cursor reader = getAllGroupsAsCursor();//m_DbWrkObj.m_CurrentDb.rawQuery("select group_id,group_name from "+TABLE+" order by group_name",new String[]{});
			
			if(reader!=null)
			{
				result=new ArrayList<GroupCls>();
				
				if(reader.moveToFirst())
					do{
						result.add(new GroupCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey)));
					}
					while(reader.moveToNext());
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns group info by its id or name*/
	public Cursor getGroupInfoAsCursor(int groupId,String groupName){
		Cursor result=null;
		
		try{
			String[] queryParams=groupId!=-1?new String[]{""+groupId}:(groupName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(groupName,m_DbWrkObj.m_CryptKey)});
			String strCondition=groupId!=-1?" where group_id=?":(groupName.trim().equals("")?"":" where trim(lower(group_name))=trim(lower(?))");
			result = m_DbWrkObj.m_CurrentDb.rawQuery("select group_id,group_name from "+TABLE+strCondition, queryParams);
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных группы2 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Returns group info by its id or name*/
	public GroupCls getGroupInfo(int groupId,String groupName){
		GroupCls result=null;

		try{
			Cursor reader=getGroupInfoAsCursor(groupId,groupName);
			
			if(reader!=null)
			{
				if(reader.moveToFirst())
					result=new GroupCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey));
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Checks group existence by its id or name*/
	public boolean checkGroupExistence(int groupId,String groupName){
		boolean result=false;
		
		try{
			String[] queryParams=groupId!=-1?new String[]{""+groupId}:(groupName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(groupName,m_DbWrkObj.m_CryptKey)});
			String strCondition=groupId!=-1?" where group_id=?":(groupName.trim().equals("")?"":" where trim(lower(group_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select count(1) as cnt from "+TABLE+strCondition, queryParams);
			
			if(reader!=null){
				if(reader.moveToFirst())
					result=(reader.getInt(0)>0);
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка проверки данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Adds new group */
	public boolean addGroup(String groupName){
		boolean result=false;

		try{
			ContentValues cv = new ContentValues();
			cv.put("group_name", m_DbWrkObj.m_CryptInst.Crypt(groupName,m_DbWrkObj.m_CryptKey));
			result=(m_DbWrkObj.m_CurrentDb.insert(TABLE, null, cv)>-1);
		}
		catch(Exception ex){
			String strErr="Ошибка добавления данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Updates group */
	public boolean updateGroup(int groupId,String oldGroupName,String newGroupName){
		boolean result=false;

		try{
			ContentValues cv = new ContentValues();
			cv.put("group_name", m_DbWrkObj.m_CryptInst.Crypt(newGroupName,m_DbWrkObj.m_CryptKey));
			String[] queryParams=groupId!=-1?new String[]{""+groupId}:(oldGroupName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(oldGroupName,m_DbWrkObj.m_CryptKey)});
			String strCondition=groupId!=-1?"group_id=?":(oldGroupName.trim().equals("")?"":"trim(lower(group_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.update(TABLE, cv, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка обновления данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}

	/** Deletes group */
	public boolean deleteGroup(int groupId,String groupName){
		boolean result=false;

		try{
			String[] queryParams=groupId!=-1?new String[]{""+groupId}:(groupName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(groupName,m_DbWrkObj.m_CryptKey)});
			String strCondition=groupId!=-1?"group_id=?":(groupName.trim().equals("")?"":"trim(lower(group_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.delete(TABLE, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка удаления данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}

	/** Checks whether group contains persons */
	public boolean isNotEmptyGroup(int groupId,String groupName){
		boolean result=false;

		try{
			String[] queryParams=groupId!=-1?new String[]{""+groupId}:(groupName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(groupName,m_DbWrkObj.m_CryptKey)});
			String strCondition=groupId!=-1?"group_id=?":(groupName.trim().equals("")?"":"trim(lower(group_name))=trim(lower(?))");
			Cursor reader=m_DbWrkObj.m_CurrentDb.rawQuery("select count(1) as cnt from PERSON_TBL where group_id="+
					"(select group_id from "+TABLE+" where "+strCondition+")", queryParams);
			
			if(reader!=null)
			{
				if(reader.moveToFirst())
					result=reader.getInt(0)>0;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка удаления данных группы - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}
	
	@Override
	public int compare(GroupCls lhs, GroupCls rhs) {
		int result=1;
		
		try{
			if(lhs.m_GroupId==rhs.m_GroupId&&lhs.m_GroupName.trim().equalsIgnoreCase(rhs.m_GroupName))
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
			GroupCls groupInst=(GroupCls)inst;
			result=(this.m_GroupId==groupInst.m_GroupId&&this.m_GroupName.trim().equalsIgnoreCase(groupInst.m_GroupName));
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
			dest.writeInt(m_GroupId);
			dest.writeString(m_GroupName);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<GroupCls> CREATOR = new Parcelable.Creator<GroupCls>() { 
		public GroupCls createFromParcel(Parcel prcl) {
	    	return new GroupCls(prcl);
		}

	    public GroupCls[] newArray(int size) {
	      return new GroupCls[size];
	    }
	};
}
