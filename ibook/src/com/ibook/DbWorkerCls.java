package com.ibook;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper; 
import android.util.Log;
import android.widget.Toast;

class DbWorkerCls extends SQLiteOpenHelper {
	public String m_DbName="eBook";
	public int m_DbVersion=1;
	private Context m_Ctx=null;
	public SQLiteDatabase m_CurrentDb=null;
	private final String LOG_TAG="iBook - DbWorkerCls";
	public CryptographyCls m_CryptInst=new CryptographyCls();
	public String m_CryptKey=new String("");
	
	/** Not default constructor*/
	public DbWorkerCls(Context context,String dbName,int currentVersion,String cryptKey){
		super(context,dbName,null,currentVersion);

		try{
			m_CryptKey=cryptKey;
			m_DbName=dbName;
			m_DbVersion=currentVersion;
			m_Ctx=context;
			m_CryptInst=new CryptographyCls();
			m_CurrentDb=this.getWritableDatabase();
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка инициализации DbWorkerCls - "+ex.getMessage());
		}
	}
	
	/** Closes all opened connections */
	public void destroyInstance(){
		try{
			this.close();
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"DbWorkerCls destroyInstance error - "+ex.getMessage());
		}
	}
	
	/** Finalizes object resources */
	@Override
	protected void finalize(){
		destroyInstance();
	}
	
	/** Creates new schema */
	@Override
	public void onCreate(SQLiteDatabase db){
		try{
			Toast.makeText(m_Ctx, "Попытка создать БД eBook", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка создать БД eBook");
			////////////////////////////////// Table creation //////////////////////////////
			db.beginTransactionNonExclusive();
			
			try{
				//INTENT_TBL
				db.execSQL("create table INTENT_TBL (intent_id integer primary key autoincrement, intent_name text, intent_img text, intent_desc text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу INTENT_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу INTENT_TBL");
				
				//FIELD_TYPE_TBL
				db.execSQL("create table FIELD_TYPE_TBL (type_id integer primary key autoincrement, type_name text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу FIELD_TYPE_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу FIELD_TYPE_TBL");
				
				//FIELD_TBL
				db.execSQL("create table FIELD_TBL (field_id integer primary key autoincrement, field_name text,type_id integer,intent_id text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу FIELD_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу FIELD_TBL");

				//USER_GROUP_TBL
				db.execSQL("create table PERSON_GROUP_TBL (group_id integer primary key autoincrement, group_name text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу PERSON_GROUP_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу PERSON_GROUP_TBL");
				
				//PERSON_TBL
				db.execSQL("create table PERSON_TBL (person_id integer primary key autoincrement, person_name text, group_id integer);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу PERSON_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу PERSON_TBL");
				
				//CONTACTS_TBL
				db.execSQL("create table CONTACTS_TBL (contact_id integer primary key autoincrement, person_id integer, field_id integer, field_value text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу CONTACTS_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу CONTACTS_TBL");
			
				//PERSON_PHOTO_TBL
				//db.execSQL("create table PERSON_PHOTO_TBL (person_id integer primary key, person_photo blob);");
				
				db.setTransactionSuccessful();
			}
			catch(Exception ex){}
			finally{
				db.endTransaction();
			}
			
			//////////////////////////////// Values set //////////////////////////////////////
			
			//db.beginTransaction();
			m_CurrentDb=db;
			
			//INTENT_TBL
			ArrayList<HashMap<String,String>> paramTypeValueCollection=new ArrayList<HashMap<String,String>>();
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("intent_name",m_CryptInst.Crypt("None",m_CryptKey));
				put("intent_img",m_CryptInst.Crypt("",m_CryptKey));put("intent_desc",m_CryptInst.Crypt("Не назначено",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("intent_name",m_CryptInst.Crypt("ACTION_DIAL",m_CryptKey));
				put("intent_img",m_CryptInst.Crypt("telephone",m_CryptKey));put("intent_desc",m_CryptInst.Crypt("Телефон",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("intent_name",m_CryptInst.Crypt("ACTION_SEND",m_CryptKey));
				put("intent_img",m_CryptInst.Crypt("gmail",m_CryptKey));put("intent_desc",m_CryptInst.Crypt("Почта",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("intent_name",m_CryptInst.Crypt("CATEGORY_APP_MESSAGING",m_CryptKey));
				put("intent_img",m_CryptInst.Crypt("comment",m_CryptKey));put("intent_desc",m_CryptInst.Crypt("СМС",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("intent_name",m_CryptInst.Crypt("CATEGORY_APP_MAPS",m_CryptKey));
				put("intent_img",m_CryptInst.Crypt("house",m_CryptKey));put("intent_desc",m_CryptInst.Crypt("Гугл Карты",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("intent_name",m_CryptInst.Crypt("CATEGORY_BROWSABLE",m_CryptKey));
				put("intent_img",m_CryptInst.Crypt("world",m_CryptKey));put("intent_desc",m_CryptInst.Crypt("Поиск в вебе",m_CryptKey));}});
			makePackageInsert("INTENT_TBL",paramTypeValueCollection );
			paramTypeValueCollection.clear();
			Toast.makeText(m_Ctx, "Попытка внесения начальных данных в таблицу INTENT_TBL", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка внесения начальных данных в таблицу INTENT_TBL");
			
			//FIELD_TYPE_TBL
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("type_name",m_CryptInst.Crypt("string",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("type_name",m_CryptInst.Crypt("int",m_CryptKey));}});
			makePackageInsert("FIELD_TYPE_TBL",paramTypeValueCollection );
			paramTypeValueCollection.clear();
			Toast.makeText(m_Ctx, "Попытка внесения начальных данных в таблицу FIELD_TYPE_TBL", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка внесения начальных данных в таблицу FIELD_TYPE_TBL");
			
			//USER_GROUP_TBL
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Прочие",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Собутыльники",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Одногруппники",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Одноклассники",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Родственники",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Барыги",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Подруги",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Такси",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Коллеги",m_CryptKey));}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("group_name",m_CryptInst.Crypt("Менты",m_CryptKey));}});
			makePackageInsert("PERSON_GROUP_TBL",paramTypeValueCollection );
			paramTypeValueCollection.clear();
			Toast.makeText(m_Ctx, "Попытка внесения начальных данных в таблицу PERSON_GROUP_TBL", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка внесения начальных данных в таблицу PERSON_GROUP_TBL");
			
			//FIELD_TBL
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Прочие",m_CryptKey));put("type_id","1");put("intent_id","1");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Адрес",m_CryptKey));put("type_id","1");put("intent_id","5,6");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Мыло",m_CryptKey));put("type_id","1");put("intent_id","3");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Ася",m_CryptKey));put("type_id","1");put("intent_id","1");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Скайп",m_CryptKey));put("type_id","1");put("intent_id","1");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Домашний телефон",m_CryptKey));put("type_id","1");put("intent_id","2");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Мобильный телефон",m_CryptKey));put("type_id","1");put("intent_id","2,4");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Рабочий телефон",m_CryptKey));put("type_id","1");put("intent_id","2,4");}});
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("field_name",m_CryptInst.Crypt("Еще телефон",m_CryptKey));put("type_id","1");put("intent_id","2,4");}});
			makePackageInsert("FIELD_TBL",paramTypeValueCollection );
			paramTypeValueCollection.clear();
			Toast.makeText(m_Ctx, "Попытка внесения начальных данных в таблицу FIELD_TBL", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка внесения начальных данных в таблицу FIELD_TBL");
			Toast.makeText(m_Ctx, "БД eBook создана", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"БД eBook создана");
		}
		catch(Exception ex){
			Log.d(LOG_TAG,ex.getMessage());
			Toast.makeText(m_Ctx, "Ошибка при создании БД - "+ex.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
		finally{
			//db.endTransaction();
		}
	}
	
	/** Upgrades schema */
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
		try{
			
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка при пересоздании БД - "+ex.getMessage());
			return;
		}
	}
	
	/** Drops current db */
	public boolean dropDatabase(){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует!");
			
			m_CurrentDb.beginTransactionNonExclusive();
			m_CurrentDb.execSQL("delete from CONTACTS_TBL");
			m_CurrentDb.execSQL("delete from PERSON_TBL");
			m_CurrentDb.execSQL("delete from PERSON_GROUP_TBL");
			m_CurrentDb.execSQL("delete from FIELD_TBL");
			m_CurrentDb.setTransactionSuccessful();
			result=true;
			Toast.makeText(m_Ctx, "БД eBook удалена", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"БД eBook удалена");
		}
		catch(Exception ex){
			String strErr="Ошибка в методе dropDatabase - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		finally{
			if(m_CurrentDb!=null)
				m_CurrentDb.endTransaction();
		}
		
		return result;
	}
	
	/** Makes insert into table*/
	public boolean makePackageInsert(String strTableName,ArrayList<HashMap<String,String>> paramTypeValueCollection ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			if(paramTypeValueCollection==null||paramTypeValueCollection.isEmpty())
				throw new Exception("Не заданы параметры для вставки");
			
			ContentValues cv = new ContentValues();
			
			for(int i=0;i<paramTypeValueCollection.size();i++){
				try{
					HashMap<String,String> paramAttr=paramTypeValueCollection.get(i);
				
					if(paramAttr==null||paramAttr.isEmpty())
						continue;
				
					Object[] keyArr=paramAttr.keySet().toArray();
				
					for(int j=0;j<keyArr.length;j++){
						String key=(String)(keyArr[j]);
						cv.put(key,paramAttr.get(key));
					}
				
					m_CurrentDb.insert(strTableName, null, cv);
				}
				catch(Exception ex){
					Log.d(LOG_TAG,ex.getMessage());
				}
			}
			
			result = true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе MakePackageInsert - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Makes update into table*/
	public boolean makeUpdate(String strTableName,ArrayList<HashMap<String,String>> paramTypeValueCollection ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			if(paramTypeValueCollection==null||paramTypeValueCollection.isEmpty())
				throw new Exception("Не заданы параметры для изменения");
			
			result= true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе MakeUpdate - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Makes delete from table*/
	public boolean makeDelete(String strTableName,ArrayList<HashMap<String,String>> paramTypeValueCollection ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			if(paramTypeValueCollection==null||paramTypeValueCollection.isEmpty())
				throw new Exception("Не заданы параметры для удаления");
			
			result= true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе makeDelete - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Makes delete from table*/
	public boolean checkRecordExistance(String strTableName,String strCondition, String[] conditionArgs ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			Cursor reader = m_CurrentDb.query(strTableName, new String[]{"count(1) as cnt"}, strCondition, conditionArgs, null, null, null);
			
			if (reader != null) {
			      if (reader.moveToFirst())
			    		  result=(reader.getInt(0)>0);

			      reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в методе CheckRecordExistance - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Makes selection from table*/
	public boolean makeSelection(String strQuery, String[] params ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			Cursor reader = m_CurrentDb.rawQuery(strQuery, params);
			
			if (reader != null) {
			      if (reader.moveToFirst())
			    		  result=(reader.getInt(0)>0);

			      reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в методе MakeSelection - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
}
