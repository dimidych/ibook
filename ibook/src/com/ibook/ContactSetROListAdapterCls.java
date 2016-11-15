package com.ibook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactSetROListAdapterCls extends BaseAdapter {

	Context m_Ctx=null;
	LayoutInflater m_Inflater;
	ArrayList<ContactSetCls> m_ContactSetArr;
	private final String LOG_TAG="iBook - ContactSetListAdapterCls";
	public int m_SelectedRecordId=-1;
	
	/** Not default constructor */
	public ContactSetROListAdapterCls(Context context, ArrayList<ContactSetCls> contactSetArr){
		m_Ctx=context;
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
				   resultView = m_Inflater.inflate(R.layout.contact_set_ro_item_view, parent, false);
		
			   if((m_ContactSetArr==null||m_ContactSetArr.size()==0||position>=m_ContactSetArr.size()))
				   throw new Exception("Несоответствие набора записей");
				   
			   ContactSetCls contactSetObj = getItmAsContactSet(position);
			   
			   LinearLayout lytContactSetValue=(LinearLayout)resultView.findViewById(R.id.lytContactIntent);
			   
			   TextView lblContactSetType=(TextView)resultView.findViewById(R.id.lblContactType);
			   lblContactSetType.setText(contactSetObj.m_FieldDefinition.m_FieldName);
			   
			   TextView lblContactSetValue=(TextView)resultView.findViewById(R.id.lblContactValue);
			   lblContactSetValue.setText(contactSetObj.m_FieldValue);
			   lblContactSetValue.setTag(contactSetObj.m_RecordId);
			   LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(50,50);
			   lParams.leftMargin=3;
			   lParams.rightMargin=3;
			   
			   for(int i=0;i<contactSetObj.m_FieldDefinition.m_IntentActionArr.size();i++){
				   String intentName=contactSetObj.m_FieldDefinition.m_IntentActionArr.get(i).m_IntentName;
				   
				   if(!intentName.equals("None")){
					   int resId=-1;
				   
					   if(intentName.equals("ACTION_DIAL"))
						   resId=R.layout.btn_phone;
					   
					   if(intentName.equals("CATEGORY_APP_MESSAGING"))
						   resId=R.layout.btn_sms;
					   
					   if(intentName.equals("ACTION_SEND"))
						   resId=R.layout.btn_mail;
					   
					   if(intentName.equals("CATEGORY_APP_MAPS"))
						   resId=R.layout.btn_map;
					   
					   if(intentName.equals("CATEGORY_BROWSABLE"))
						   resId=R.layout.btn_search;
					   
					   View btnLl=m_Inflater.inflate(resId, (ViewGroup)resultView, false);
					   Button btnPhone=(Button)btnLl.findViewById(R.id.btn);
					   btnPhone.setTag(intentName+"~"+contactSetObj.m_FieldValue);
					   
					   btnPhone.setOnClickListener(new OnClickListener(){
						   public void onClick(View v){
							   String intentName=v.getTag().toString().split("~")[0].trim();
							   String intentUri=v.getTag().toString().split("~")[1].trim();
							   
							   if(intentUri.trim().equals("")||intentName.trim().equals(""))
								   return;
							   
							   IntensionCls intentObj=new IntensionCls(m_Ctx,null);
							   intentObj.makeIntension(intentName, intentUri);
						   }
					   });
					   
					   lytContactSetValue.addView(btnLl,lParams);
				   }
			   }
			   
			   int[] colors=new int[]{Color.parseColor("#faf7f7"),Color.parseColor("#d9d8d8")};
			   resultView.setBackgroundColor(colors[position%2]);
		   }
		   catch(Exception ex){
			   String strErr="Ошибка cоздания списка данных контактов - "+ex.getMessage();
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
