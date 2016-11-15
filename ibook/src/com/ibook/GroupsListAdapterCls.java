package com.ibook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class GroupsListAdapterCls extends BaseAdapter{
	
	private byte m_IsSelectable=0;
	private LayoutInflater m_Inflater;
	private ArrayList<GroupCls> m_GroupArr;
	private final String LOG_TAG="iBook - GroupsListAdapterCls";
	public int m_SelectedRecordId=-1;

	  /** Not default constructor */
	  public GroupsListAdapterCls(Context context, ArrayList<GroupCls> groupArr,byte isSelectable){
		  m_GroupArr = groupArr;
		  m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  m_IsSelectable=isSelectable;
	  }

	  /** Number of items */
	  @Override
	  public int getCount() {
		  int result=0;
		  
		  try{
			  if(m_GroupArr==null)
				  throw new Exception("Пустая группа");
			  
			  result=m_GroupArr.size();
		  }
		  catch(Exception ex){
			  String strErr="Ошибка получения количества - "+ex.getMessage();
			  Log.d(LOG_TAG,strErr);
			   return 0;
		  }
		  
		  return result;
	  }

	  /** Retrieves item by index */
	  @Override
	  public Object getItem(int position){
		  Object result=0;
		  
		  try{
			  if(m_GroupArr==null)
				  throw new Exception("Пустая группа");
			  
			  result=m_GroupArr.get(position);
		  }
		  catch(Exception ex){
			  String strErr="Ошибка получения элемента - "+ex.getMessage();
			  Log.d(LOG_TAG,strErr);
			   return null;
		  }
		  
		  return result;
	  }

	  /** Retrieves item id by position */
	  @Override
	  public long getItemId(int position){
		  long result=0;
		  
		  try{
			  if(m_GroupArr==null)
				  throw new Exception("Пустая группа");
			  
			  if(m_GroupArr.size()==0&&position>=m_GroupArr.size())
				  throw new Exception();
			  
			  result=m_GroupArr.get(position).m_GroupId;
		  }
		  catch(Exception ex){
			  String strErr="Ошибка получения ид элемента - "+ex.getMessage();
			  Log.d(LOG_TAG,strErr);
			   return 0;
		  }
		  
		  return result;
	  }

	  /** Customizes list item view */
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent){
		   View resultView = convertView;
		   
		   try{
			   if((m_GroupArr==null||m_GroupArr.size()==0||position>=m_GroupArr.size()))
				   throw new Exception("Несоответствие набора записей");
				   
			   GroupCls grpObj = getItmAsGroup(position);
			   
			   if(m_IsSelectable==0){
				   if (resultView == null)
					   resultView = m_Inflater.inflate(R.layout.group_view, parent, false);
				   
				   TextView grpItm=(TextView)resultView.findViewById(R.id.lblGroup);
				   grpItm.setText(grpObj.m_GroupName);
				   grpItm.setTag(grpObj.m_GroupId);
			   }
			   
			   if(m_IsSelectable==1){
				   if (resultView == null)
					   resultView = m_Inflater.inflate(R.layout.contact_set_view, parent, false);
				   
				   RadioButton grpItm=((RadioButton)resultView.findViewById(R.id.rbtnItm));
				   grpItm.setText(grpObj.m_GroupName);
				   grpItm.setTag(grpObj.m_GroupId);
				   grpObj.m_IsSelected=(position==m_SelectedRecordId);
				   grpItm.setChecked(grpObj.m_IsSelected);
			   }

			   int[] colors=new int[]{Color.parseColor("#faf7f7"),Color.parseColor("#d9d8d8")};
			   resultView.setBackgroundColor(colors[position%2]);
		   }
		   catch(Exception ex){
			   String strErr="Ошибка оздания списка группы - "+ex.getMessage();
			   Log.d(LOG_TAG,strErr);
			   return null;
		   }
		   
		   return resultView;
	  }

	  /** Retrieves group object from list item at position */
	  public GroupCls getItmAsGroup(int position) {
		  return ((GroupCls) getItem(position));
	  }

	}