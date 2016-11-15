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

public class ContactSetListAdapterCls extends BaseAdapter	{
	LayoutInflater m_Inflater;
	ArrayList<ContactSetCls> m_ContactSetArr;
	private final String LOG_TAG="iBook - ContactSetListAdapterCls";
	public int m_SelectedRecordId=-1;
	 
	/** Not default constructor */
	public ContactSetListAdapterCls(Context context, ArrayList<ContactSetCls> contactSetArr){
		 m_ContactSetArr = contactSetArr;
		 m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/** Number of items */
	@Override
	public int getCount() {
		int result=0;
		  
		  try{
			  if(m_ContactSetArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_ContactSetArr.size();
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
			  if(m_ContactSetArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_ContactSetArr.get(position);
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
			  if(m_ContactSetArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  if(m_ContactSetArr.size()==0&&position>=m_ContactSetArr.size())
				  throw new Exception("Пустой набор данных");
			  
			  result=m_ContactSetArr.get(position).m_RecordId;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View resultView = convertView;
		   
		   try{
			   if (resultView == null)
				   resultView = m_Inflater.inflate(R.layout.contact_set_item_view, parent, false);
		
			   if((m_ContactSetArr==null||m_ContactSetArr.size()==0||position>=m_ContactSetArr.size()))
				   throw new Exception("Несоответствие набора записей");
				   
			   ContactSetCls contactSetObj = getItmAsContactSet(position);
			   TextView lblContactSetType=(TextView)resultView.findViewById(R.id.lblContactType);
			   lblContactSetType.setText(contactSetObj.m_FieldDefinition.m_FieldName);
			   RadioButton rbtnContactValue=((RadioButton)resultView.findViewById(R.id.rbtnContactSetValue));
			   rbtnContactValue.setText(contactSetObj.m_FieldValue);
			   rbtnContactValue.setTag(contactSetObj.m_RecordId);
			   contactSetObj.m_IsSelected=(position==m_SelectedRecordId);
			   rbtnContactValue.setChecked(contactSetObj.m_IsSelected);
			   int[] colors=new int[]{Color.parseColor("#faf7f7"),Color.parseColor("#d9d8d8")};
			   resultView.setBackgroundColor(colors[position%2]);
		   }
		   catch(Exception ex){
			   String strErr="Ошибка оздания списка данных контактов - "+ex.getMessage();
			   Log.d(LOG_TAG,strErr);
			   return null;
		   }
		   
		   return resultView;
	}

	/** Retrieves group object from list item at position */
	private ContactSetCls getItmAsContactSet(int position) {
		  return ((ContactSetCls) getItem(position));
	}
	
	/** Retrieves all selected items */
	public ArrayList<ContactSetCls> getSelected(){
		ArrayList<ContactSetCls> result=new ArrayList<ContactSetCls>();
		
		try{
			if(m_ContactSetArr==null)
				  throw new Exception("Пустой набор данных");
			
			for(ContactSetCls contactSetInst :m_ContactSetArr)
				if(contactSetInst.m_IsSelected){
					result.add(contactSetInst);
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
