package com.ibook;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CustomExpListAdapter extends BaseExpandableListAdapter {

	private List<GroupCls> m_GroupLst=null;
	private ArrayList<PersonCls> m_ContactList=null;
	private Context m_Ctx=null;
	private final static String LOG_TAG="iBook - CustomExpListAdapter";
	
	/** Default constructor */
	public CustomExpListAdapter() {}

	/** Not Default constructor */
	public CustomExpListAdapter(Context ctx) {
		m_Ctx=ctx;
	}
	
	/** Not Default constructor */
	public CustomExpListAdapter(ArrayList<PersonCls> contactList) {
		m_ContactList=contactList;
		m_GroupLst=getGroups(contactList);
	}
	
	/** Not Default constructor */
	public CustomExpListAdapter(Context ctx,ArrayList<PersonCls> contactList) {
		m_Ctx=ctx;
		m_ContactList=contactList;
		m_GroupLst=getGroups(contactList);
	}
	
	/** Parses ArrayList<ContactsCls> to  Map<GroupCls,ContactsCls>*/
	private List<GroupCls> getGroups(ArrayList<PersonCls> contactList){
		List<GroupCls> result=null;
		
		try{
			if(contactList!=null&&contactList.size()>0){
				result=new LinkedList<GroupCls>();
				
				for(int i=0;i<contactList.size();i++)
					if(!result.contains(contactList.get(i).m_GroupObj))
						result.add(contactList.get(i).m_GroupObj);
				
				if(result.size()==0)
					result.add(new GroupCls(-1,"root"));
			}
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе contactInstTranslator - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}

		return result;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Object result=null;
		
		try{
			if(m_GroupLst==null)
				throw new Exception("Список групп пуст");
			
			if(m_GroupLst.size()<=groupPosition)
				throw new Exception("Группа с указанным индексом не существует");
			
			int counter=0;
			
			for(int i=0;i<m_ContactList.size();i++)
				if(m_GroupLst.get(groupPosition).equals(m_ContactList.get(i).m_GroupObj)){
					if(counter==childPosition){
						result=m_ContactList.get(i);
						break;
					}
					
					counter++;
				}
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getChild - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		int result=0;
		
		try{
			if(m_GroupLst==null)
				throw new Exception("Список групп пуст");
			
			if(m_GroupLst.size()<=groupPosition)
				throw new Exception("Группа с указанным индексом не существует");
			
			int counter=0;
			
			for(int i=0;i<m_ContactList.size();i++)
				if(m_GroupLst.get(groupPosition).equals(m_ContactList.get(i).m_GroupObj)){
					if(counter==childPosition){
						result=m_ContactList.get(i).m_PersonId;
						break;
					}
					
					counter++;
				}
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getChildId - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return 0;
		}
		
		return result;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
		View result=null;

		try{
			if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) m_Ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.child_view, null);
		        int[] colors=new int[]{Color.parseColor("#faf7f7"),Color.parseColor("#d9d8d8")};
		        convertView.setBackgroundColor(colors[childPosition%2]);
			}

			PersonCls personInst=(PersonCls)getChild(groupPosition,childPosition);
			TextView btnBrowsePerson = (TextView) convertView.findViewById(R.id.txtBrowsePerson);
	        btnBrowsePerson.setText(personInst.m_PersonName);
	        btnBrowsePerson.setTag(personInst.m_PersonId);
	        result= convertView;
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getChildView - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int result=0;
		
		try{
			if(m_GroupLst==null)
				throw new Exception("Список групп пуст");
			
			if(m_GroupLst.size()<=groupPosition)
				throw new Exception("Группа с указанным индексом не существует");
			
			for(int i=0;i<m_ContactList.size();i++)
				if(m_GroupLst.get(groupPosition).equals(m_ContactList.get(i).m_GroupObj))
					result++;
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getChildrenCount - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return 0;
		}
		
		return result;
	}

	@Override
	public Object getGroup(int groupPosition) {
		try{
			if(m_GroupLst==null)
				throw new Exception("Список групп пуст");
			
			if(m_GroupLst.size()<=groupPosition)
				throw new Exception("Группа с указанным индексом не существует");
			
			return m_GroupLst.get(groupPosition);
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getGroup - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
	}

	@Override
	public int getGroupCount() {
		if(m_GroupLst!=null)
			return m_GroupLst.size();
		else 
			return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		int result=0;
		
		try{
			if(m_GroupLst==null)
				throw new Exception("Список групп пуст");
			
			if(m_GroupLst.size()<=groupPosition)
				throw new Exception("Группа с указанным индексом не существует");
			
			result=m_GroupLst.get(groupPosition).m_GroupId;
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getGroupId - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return 0;
		}
		
		return result;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		View result=null;

		try{
			if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) m_Ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.group_view, null);
	        }

	        if (isExpanded){
	        	convertView.setBackgroundColor(Color.parseColor("#fd851a"));
	        }
	        else{
	            int[] colors=new int[]{Color.parseColor("#fda53d"),Color.parseColor("#fed9ad")};
	            convertView.setBackgroundColor(colors[groupPosition%2]);
	        }

	        TextView textGroup = (TextView) convertView.findViewById(R.id.lblGroup);
	        textGroup.setText(((GroupCls)getGroup(groupPosition)).m_GroupName);
	        result=convertView;
		}
		catch(Exception ex){
			String strErr= "Ошибка в методе getGroupView - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
