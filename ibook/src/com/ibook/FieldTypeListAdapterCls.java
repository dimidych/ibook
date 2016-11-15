package com.ibook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FieldTypeListAdapterCls extends BaseAdapter {

	private LayoutInflater m_Inflater;
	private ArrayList<FieldTypeCls> m_FieldTypeArr;
	private final String LOG_TAG="iBook - FieldTypeListAdapterCls";
	public int m_SelectedIndex = -1;
	
	/** Not default constructor */
	public FieldTypeListAdapterCls(Context context, ArrayList<FieldTypeCls> fieldTypeArr){
		m_FieldTypeArr = fieldTypeArr;
		 m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/** Number of items */
	@Override
	public int getCount() {
		int result=0;
		  
		  try{
			  if(m_FieldTypeArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_FieldTypeArr.size();
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
	public Object getItem(int position) {
		Object result=0;
		  
		  try{
			  if(m_FieldTypeArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_FieldTypeArr.get(position);
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
	public long getItemId(int position) {
		long result=0;
		  
		  try{
			  if(m_FieldTypeArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  if(m_FieldTypeArr.size()==0&&position>=m_FieldTypeArr.size())
				  throw new Exception("Пустой набор данных");
			  
			  result=(m_FieldTypeArr.get(position)).m_FieldTypeId;
		  }
		  catch(Exception ex){
			  String strErr="Ошибка получения ид элемента - "+ex.getMessage();
			  Log.d(LOG_TAG,strErr);
			   return 0;
		  }
		  
		  return result;
	}

	/** Defines element list view */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View resultView = convertView;
		   
		   try{
			   if (resultView == null)
				   resultView = m_Inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		
			   if((m_FieldTypeArr==null||m_FieldTypeArr.size()==0||position>=m_FieldTypeArr.size()))
				   throw new Exception("Несоответствие набора записей");
				
			   FieldTypeCls fieldTypeObj = getItmAsFieldCls(position);
			   resultView.setTag(fieldTypeObj.m_FieldTypeId);
			   ((TextView)resultView).setText(fieldTypeObj.m_FieldTypeName);
			   fieldTypeObj.m_IsSelected=(position==m_SelectedIndex);
			   int[] colors=new int[]{Color.parseColor("#faf7f7"),Color.parseColor("#d9d8d8")};
			   resultView.setBackgroundColor(colors[position%2]);
		   }
		   catch(Exception ex){
			   String strErr="Ошибка cоздания списка типов полей - "+ex.getMessage();
			   Log.d(LOG_TAG,strErr);
			   return null;
		   }
		   
		   return resultView;
	}
	
	/** Retrieves field type object from list item at position */
	public FieldTypeCls getItmAsFieldCls(int position) {
		  return ((FieldTypeCls) getItem(position));
	}
	
	/** Retrieves all selected items */
	public ArrayList<FieldTypeCls> getSelected(){
		ArrayList<FieldTypeCls> result=new ArrayList<FieldTypeCls>();
		
		try{
			if(m_FieldTypeArr==null)
				  throw new Exception("Пустой набор данных");
			
			for(FieldTypeCls fieldTypeInst :m_FieldTypeArr)
				if(fieldTypeInst.m_IsSelected){
					result.add(fieldTypeInst);
					break;
				}
		}
		catch(Exception ex){
			String strErr="Ошибка метода getSelected - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	

}
