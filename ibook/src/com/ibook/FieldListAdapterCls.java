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

public class FieldListAdapterCls extends BaseAdapter {
	
	private LayoutInflater m_Inflater;
	private ArrayList<FieldCls> m_FieldArr;
	private final String LOG_TAG="iBook - FieldListAdapterCls";
	public int m_SelectedIndex = -1;
	private RadioButton rbtnField=null;
	
	/** Not default constructor */
	public FieldListAdapterCls(Context context, ArrayList<FieldCls> fieldArr){
		 m_FieldArr = fieldArr;
		 m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/** Number of items */
	@Override
	public int getCount() {
		int result=0;
		  
		  try{
			  if(m_FieldArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_FieldArr.size();
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
			  if(m_FieldArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_FieldArr.get(position);
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
			  if(m_FieldArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  if(m_FieldArr.size()==0&&position>=m_FieldArr.size())
				  throw new Exception("Пустой набор данных");
			  
			  result=(m_FieldArr.get(position)).m_FieldId;
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
				   resultView = m_Inflater.inflate(R.layout.contact_set_view, parent, false);
		
			   if((m_FieldArr==null||m_FieldArr.size()==0||position>=m_FieldArr.size()))
				   throw new Exception("Несоответствие набора записей");
				   
			   FieldCls fieldObj = getItmAsFieldCls(position);
			   rbtnField=((RadioButton)resultView.findViewById(R.id.rbtnItm));
			   rbtnField.setText(fieldObj.m_FieldName);
			   rbtnField.setTag(position /*fieldObj.m_FieldId*/);
			   fieldObj.m_IsSelected=(position==m_SelectedIndex);
			   rbtnField.setChecked(fieldObj.m_IsSelected);
			   int[] colors=new int[]{Color.parseColor("#faf7f7"),Color.parseColor("#d9d8d8")};
			   resultView.setBackgroundColor(colors[position%2]);
		   }
		   catch(Exception ex){
			   String strErr="Ошибка cоздания списка полей - "+ex.getMessage();
			   Log.d(LOG_TAG,strErr);
			   return null;
		   }
		   
		   return resultView;
	}

	/** Retrieves group object from list item at position */
	public FieldCls getItmAsFieldCls(int position) {
		  return ((FieldCls) getItem(position));
	}
	
	/** Retrieves all selected items */
	public ArrayList<FieldCls> getSelected(){
		ArrayList<FieldCls> result=new ArrayList<FieldCls>();
		
		try{
			if(m_FieldArr==null)
				  throw new Exception("Пустой набор данных");
			
			for(FieldCls fieldInst :m_FieldArr)
				if(fieldInst.m_IsSelected){
					result.add(fieldInst);
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
	
	/** Sets selection state to true */
	public void setFieldChecked(int position){
		try{
			FieldCls fieldObj = getItmAsFieldCls(position);
			fieldObj.m_IsSelected=true;
			m_SelectedIndex=position;
			
			if(rbtnField!=null)
				rbtnField.setChecked(true);
		}
		catch(Exception ex){
			String strErr="Ошибка метода setFieldChecked - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
}
