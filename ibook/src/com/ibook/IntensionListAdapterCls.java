package com.ibook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class IntensionListAdapterCls extends BaseAdapter{

	private LayoutInflater m_Inflater;
	private ArrayList<IntensionCls> m_IntensionArr;
	private final String LOG_TAG="iBook - IntensionListAdapterCls";
	public ArrayList<IntensionCls> m_SelectedItemLst=new ArrayList<IntensionCls>();
	private CheckBox chkIntent=null;
	
	/** Not default constructor */
	public IntensionListAdapterCls(Context ctx,ArrayList<IntensionCls> intensionArr){
		m_IntensionArr=intensionArr;
		m_Inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_SelectedItemLst=new ArrayList<IntensionCls>();
	}
	
	/** Number of items */
	@Override
	public int getCount() {
		int result=0;
		  
		  try{
			  if(m_IntensionArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_IntensionArr.size();
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
			  if(m_IntensionArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  result=m_IntensionArr.get(position);
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
			  if(m_IntensionArr==null)
				  throw new Exception("Пустой набор данных");
			  
			  if(m_IntensionArr.size()==0&&position>=m_IntensionArr.size())
				  throw new Exception("Пустой набор данных");
			  
			  result=(m_IntensionArr.get(position)).m_IntentId;
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
				   resultView = m_Inflater.inflate(R.layout.intent_item_view, parent, false);
		
			   if((m_IntensionArr==null||m_IntensionArr.size()==0||position>=m_IntensionArr.size()))
				   throw new Exception("Несоответствие набора записей");
				   
			   IntensionCls fieldObj = getItmAsIntensionCls(position);
			   chkIntent=((CheckBox)resultView.findViewById(R.id.chkItm));
			   chkIntent.setText(fieldObj.m_IntentDescription);
			   chkIntent.setTag(position);
			   fieldObj.m_IsSelected=m_SelectedItemLst.contains(fieldObj);
			   chkIntent.setChecked(fieldObj.m_IsSelected);
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

	/** Retrieves IntensionCls object from list item at position */
	public IntensionCls getItmAsIntensionCls(int position) {
		  return ((IntensionCls) getItem(position));
	}
	
	/** Retrieves all selected items */
	public ArrayList<IntensionCls> getSelected(){
		ArrayList<IntensionCls> result=new ArrayList<IntensionCls>();
		
		try{
			if(m_IntensionArr==null)
				  throw new Exception("Пустой набор данных");
			
			for(IntensionCls intentInst :m_IntensionArr)
				if(intentInst.m_IsSelected&&!result.contains(intentInst)){
					result.add(intentInst);
				}
		}
		catch(Exception ex){
			String strErr="Ошибка метода getSelected - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Clears selected item list */
	public void clearSelectedList(){
		try{
			m_SelectedItemLst=new ArrayList<IntensionCls>();
			
			if(m_IntensionArr==null)
				  throw new Exception("Пустой набор данных");
			
			for(IntensionCls intentInst :m_IntensionArr)
				intentInst.m_IsSelected=false;
		}
		catch(Exception ex){
			String strErr="Ошибка метода getSelected - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Sets selection state to true */
	public void setIntentChecked(int position){
		try{
			IntensionCls intentInst = getItmAsIntensionCls(position);
			
			if(m_SelectedItemLst.contains(intentInst)){
				m_SelectedItemLst.remove(intentInst);
				intentInst.m_IsSelected=false;
			}
			else
			{
				m_SelectedItemLst.add(intentInst);
				intentInst.m_IsSelected=true;
			}
			
			if(chkIntent!=null)
				chkIntent.setChecked(intentInst.m_IsSelected);
		}
		catch(Exception ex){
			String strErr="Ошибка метода setIntentChecked - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
}
