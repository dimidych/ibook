package com.ibook;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

class IntensionCls implements Comparator<IntensionCls>, Parcelable {
	private final String TABLE="INTENT_TBL";
	private final String LOG_TAG="iBook - IntensionCls";
	private Context m_Ctx=null;
	private DbWorkerCls m_DbWrkObj=null;
	public int m_IntentId=-1;
	public String m_IntentName="";
	public String m_IntentImg="";
	public String m_IntentDescription="";
	public boolean m_IsSelected=false;
	
	/** Default constructor */
	public IntensionCls(){}

	/** Not Default constructor */
	public IntensionCls(Context ctx,DbWorkerCls dbWrkObj){
		m_Ctx=ctx;
		m_DbWrkObj=dbWrkObj;
	}
	
	/** Not Default constructor */
	public IntensionCls(Context ctx,DbWorkerCls dbWrkObj,int intentId,String intentName,String intentDescription,String intentImg){
		m_Ctx=ctx;
		m_DbWrkObj=dbWrkObj;
		m_IntentId=intentId;
		m_IntentName=intentName;
		m_IntentDescription=intentDescription;
		m_IntentImg=intentImg;
	}

	/** Not Default constructor */
	public IntensionCls(int intentId,String intentName,String intentDescription,String intentImg){
		m_IntentId=intentId;
		m_IntentName=intentName;
		m_IntentDescription=intentDescription;
		m_IntentImg=intentImg;
	}
	
	/** Not Default constructor */
	public IntensionCls(Parcel prcl){
		m_IntentId=prcl.readInt();
		m_IntentName=prcl.readString();
		m_IntentDescription=prcl.readString();
		m_IntentImg=prcl.readString();
	}

	/** Returns all intent info*/
	public ArrayList<IntensionCls> getAllIntentInfo(){
		ArrayList<IntensionCls> result=null;

		try{
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select intent_id, intent_name, intent_desc, intent_img from "+
					TABLE+" order by intent_name", new String[]{});
			
			if(reader!=null)
			{
				if(reader.moveToFirst()){
					result=new ArrayList<IntensionCls>();
					
					do{
						result.add(new IntensionCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey),
								m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(2),m_DbWrkObj.m_CryptKey),
								m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(3),m_DbWrkObj.m_CryptKey)));
					}
					while(reader.moveToNext());
				}
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных инфы о действии - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}

	/** Returns intent by its id or name*/
	public IntensionCls getIntent(int intentId,String intentName){
		IntensionCls result=null;
		
		try{
			String[] queryParams=intentId!=-1?new String[]{""+intentId}:(intentName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(intentName,m_DbWrkObj.m_CryptKey)});
			String strCondition=intentId!=-1?" where intent_id=?":(intentName.trim().equals("")?"":" where trim(lower(intent_name))=trim(lower(?))");
			Cursor reader = m_DbWrkObj.m_CurrentDb.rawQuery("select intent_id, intent_name, intent_desc, intent_img from "+
					TABLE+strCondition, queryParams);
			
			if(reader!=null)
			{
				if(reader.moveToFirst()){
					result=new IntensionCls(reader.getInt(0),m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(1),m_DbWrkObj.m_CryptKey),
							m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(2),m_DbWrkObj.m_CryptKey),
							m_DbWrkObj.m_CryptInst.Decrypt(reader.getString(3),m_DbWrkObj.m_CryptKey));
				}
				
				reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка выборки данных инфы о действии - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}
		
		return result;
	}
	
	/** Adds new intent info */
	public boolean addIntent(String intentName,String intentDesc,String intentImg){
		boolean result=false;
		
		try{
			ContentValues cv = new ContentValues();
			cv.put("intent_name", m_DbWrkObj.m_CryptInst.Crypt(intentName,m_DbWrkObj.m_CryptKey));
			cv.put("intent_desc", m_DbWrkObj.m_CryptInst.Crypt(intentDesc,m_DbWrkObj.m_CryptKey));
			cv.put("intent_img", m_DbWrkObj.m_CryptInst.Crypt(intentImg,m_DbWrkObj.m_CryptKey));
			result=(m_DbWrkObj.m_CurrentDb.insert(TABLE, null, cv)>-1);
		}
		catch(Exception ex){
			String strErr="Ошибка добавления данных инфы о действии - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Updates intent info */
	public boolean updateIntent(int intentId,String oldIntentName,String newIntentName,String intentDesc,String intentImg){
		boolean result=false;
		
		try{
			ContentValues cv = new ContentValues();
			cv.put("intent_name", m_DbWrkObj.m_CryptInst.Crypt(newIntentName,m_DbWrkObj.m_CryptKey));
			cv.put("intent_desc", m_DbWrkObj.m_CryptInst.Crypt(intentDesc,m_DbWrkObj.m_CryptKey));
			cv.put("intent_img", m_DbWrkObj.m_CryptInst.Crypt(intentImg,m_DbWrkObj.m_CryptKey));
			String[] queryParams=intentId!=-1?new String[]{""+intentId}:(oldIntentName.trim().equals("")?null:
				new String[]{m_DbWrkObj.m_CryptInst.Crypt(oldIntentName,m_DbWrkObj.m_CryptKey)});
			String strCondition=intentId!=-1?"intent_id=?":(oldIntentName.trim().equals("")?"":"trim(lower(intent_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.update(TABLE, cv, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка обновления данных инфы о записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Deletes intent info */
	public boolean deleteIntent(int intentId,String intentName){
		boolean result=false;
		
		try{
			String[] queryParams=intentId!=-1?new String[]{""+intentId}:(intentName.trim().equals("")?null:new String[]{m_DbWrkObj.m_CryptInst.Crypt(intentName,m_DbWrkObj.m_CryptKey)});
			String strCondition=intentId!=-1?"intent_id=?":(intentName.trim().equals("")?"":"trim(lower(intent_name))=trim(lower(?))");
			result=(m_DbWrkObj.m_CurrentDb.delete(TABLE, strCondition, queryParams)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка удаления данных инфы о записи - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}

		return result;
	}
	
	/** Makes intension defined by identifier */
	public void makeIntension(String intentId,String intensionUri){
		try{
			if(intensionUri.trim().equals("")||intentId.trim().equalsIgnoreCase("none"))
				return;
			
			if(intentId.trim().equalsIgnoreCase("ACTION_DIAL")){
				Intent intnt=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+intensionUri.trim()));
				m_Ctx.startActivity(intnt);
			}
			
			if(intentId.trim().equalsIgnoreCase("ACTION_SEND")){
				Intent intnt = new Intent(Intent.ACTION_SEND);
				intnt.setType("text/plain");
				intnt.putExtra(Intent.EXTRA_EMAIL  , new String[]{intensionUri.trim()});
				intnt.putExtra(Intent.EXTRA_SUBJECT, "Добавьте заголовок");
				intnt.putExtra(Intent.EXTRA_TEXT   , "Добавьте сообщение");
				m_Ctx.startActivity(intnt);		
			}
			
			if(intentId.trim().equalsIgnoreCase("CATEGORY_APP_MESSAGING")){
				Intent intent = new Intent(m_Ctx,SendSmsActivity.class);
				intent.putExtra("uri",intensionUri.trim());
				m_Ctx.startActivity(intent);
			}
			
			if(intentId.trim().equalsIgnoreCase("CATEGORY_APP_MAPS")){
				if(!UtilsCls.checkApplicationExistance("com.google.android.apps.maps", m_Ctx)){
					Toast.makeText(m_Ctx, "Приложение Google Maps не установлено!", Toast.LENGTH_LONG).show();
					throw new Exception("Приложение Google Maps не установлено!"); 
				}
				
				Intent intnt=new Intent(android.content.Intent.ACTION_VIEW);
				intnt.setData(Uri.parse("geo:0,0?q=my+"+intensionUri.trim().replace(' ', '+')));
				m_Ctx.startActivity(intnt);
			}
			
			if(intentId.trim().equalsIgnoreCase("CATEGORY_BROWSABLE")){
				Intent intnt=new Intent(Intent.ACTION_VIEW);
				intnt.setData(Uri.parse("https://www.google.com/#q="+URLEncoder.encode(intensionUri.trim(), "UTF-8")));
				m_Ctx.startActivity(intnt);
			}
			
			if(intentId.trim().equalsIgnoreCase("CATEGORY_SKYPE")){
				final String SKYPE_PATH_GENERAL = "com.skype.raider";

				if(!UtilsCls.checkApplicationExistance(SKYPE_PATH_GENERAL, m_Ctx)
						||UtilsCls.checkApplicationExistance(SKYPE_PATH_GENERAL+"-1", m_Ctx))
					throw new Exception("Приложение Skype не установлено!");	
				
				final String SKYPE_PATH_OLD = "com.skype.raider.contactsync.ContactSkypeOutCallStartActivity";
				final String SKYPE_PATH_NEW = "com.skype.raider.Main";
				Intent skypeIntent=new Intent().setAction("android.intent.action.CALL_PRIVILEGED");;
				skypeIntent.setData(Uri.parse("tel:" + intensionUri.trim()));

				if (UtilsCls.isIntentAvailable(m_Ctx, skypeIntent.setClassName(SKYPE_PATH_GENERAL, SKYPE_PATH_NEW))) {
					m_Ctx.startActivity(skypeIntent);
				} else if (UtilsCls.isIntentAvailable(m_Ctx, skypeIntent.setClassName(SKYPE_PATH_GENERAL, SKYPE_PATH_OLD))) {
					m_Ctx.startActivity(skypeIntent);
				} else {
				    Toast.makeText(m_Ctx, "Приложение Skype не установлено.", Toast.LENGTH_LONG).show();
				}
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в makeIntension - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		try{
			dest.writeInt(m_IntentId);
			dest.writeString(m_IntentName);
			dest.writeString(m_IntentDescription);
			dest.writeString(m_IntentImg);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<IntensionCls> CREATOR = new Parcelable.Creator<IntensionCls>() { 
		public IntensionCls createFromParcel(Parcel prcl) {
	    	return new IntensionCls(prcl);
		}

	    public IntensionCls[] newArray(int size) {
	      return new IntensionCls[size];
	    }
	};

	
	@Override
	public int compare(IntensionCls lhs, IntensionCls rhs) {
		int result=1;
		
		try{
			if(lhs.m_IntentName.trim().equalsIgnoreCase(rhs.m_IntentName))
				result=0;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе compare - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return 1;
		}
		
		return result;
	}

	@Override
	public boolean equals(Object inst){
		boolean result=false;
		
		try{
			IntensionCls fieldInst=(IntensionCls)inst;
			result=(this.m_IntentName.trim().equalsIgnoreCase(fieldInst.m_IntentName));
		}
		catch(Exception ex){
			String strErr="Ошибка в методе equals - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	
}
