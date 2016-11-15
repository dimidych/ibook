package com.ibook;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
import android.content.Context;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

class InitializerCls extends XmlUtilsCls implements Parcelable {
	
	private Context m_Ctx;
	public String m_DbPath=new String("");
	public int m_DbVersion=-1;
	public String m_XmlPath=new String("");
	private String m_ULogin=new String("");
	private String m_UPwd=new String("");
	public String m_CryptKey=new String("");
	private boolean m_FillPwd=false;
	private final String LOG_TAG="iBook - InitializerCls";
	
	/** Not Default constructor */
	public InitializerCls(Parcel prcl){
		super();
		m_DbPath=prcl.readString();
		m_DbVersion=prcl.readInt();
		m_XmlPath=prcl.readString();
		m_CryptKey=prcl.readString();
	}
	
	/** Not Default constructor */
	public InitializerCls(Context ctx){
		super(ctx);
		m_Ctx=ctx;
	}

	/**Reads startup settings from xml*/
	public boolean readIniXmlData(){
		boolean result=false;
		
		try{
			XmlPullParser parser = createParserInstFromResources(R.xml.ini);// createParserFromFile("Books","ini.xml");
			
			while (parser.getEventType()!= XmlPullParser.END_DOCUMENT){  
				if (parser.getEventType() == XmlPullParser.START_TAG){
					if(parser.getName().equalsIgnoreCase("dbpath")) 
						m_DbPath=parser.nextText();
					
					if(parser.getName().equalsIgnoreCase("dbversion")) 
						m_DbVersion=Integer.parseInt(parser.nextText());
					
					if(parser.getName().equalsIgnoreCase("xmlpath")) 
						m_XmlPath=parser.nextText();
				
					if(parser.getName().equalsIgnoreCase("userlogin")) 
						m_ULogin=parser.nextText();
					
					if(parser.getName().equalsIgnoreCase("userpassword")) 
						m_UPwd=parser.nextText();
					
					if(parser.getName().equalsIgnoreCase("cryptkey")) 
						m_CryptKey=parser.nextText();
					
					if(parser.getName().equalsIgnoreCase("fillpassword")) 
						m_FillPwd=Boolean.parseBoolean(parser.nextText());
				}
				
				parser.next();
			}
			
			result=true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе GetSettings - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(m_Ctx, "Ошибка чтения настроек", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return result;
	}
	
	/**Writes startup settings into xml*/
	public boolean writeIniXmlData(){
		boolean result=false;
		
		try{
			XmlSerializer serializer = Xml.newSerializer();
		    serializer.setOutput(writeFile("Books","ini.xml"));
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "Settings");
	        serializer.startTag("", "DbPath");
	        serializer.text(m_DbPath.trim());
	        serializer.endTag("", "DbPath");
	        serializer.startTag("", "DbVersion");
	        serializer.text(""+m_DbVersion);
	        serializer.endTag("", "DbVersion");
	        serializer.startTag("", "XmlPath");
	        serializer.text(m_XmlPath.trim());
	        serializer.endTag("", "XmlPath");
	        serializer.startTag("", "UserLogin");
	        serializer.text(m_ULogin.trim());
	        serializer.endTag("", "UserLogin");
	        serializer.startTag("", "UserPassword");
	        serializer.text(m_UPwd.trim());
	        serializer.endTag("", "UserPassword");
	        serializer.startTag("", "CryptKey");
	        serializer.text(m_CryptKey.trim());
	        serializer.endTag("", "CryptKey");
	        serializer.startTag("", "FillPassword");
	        serializer.text(String.valueOf(m_FillPwd));
	        serializer.endTag("", "FillPassword");
	        serializer.endTag("", "Settings");
	        serializer.endDocument();
	        serializer.flush();
	        result=true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе WriteSettings - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(m_Ctx, "Ошибка записи настроек", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return result;
	}
	
	/** Checks program authorization  */
	public boolean checkAuthorization(String strLogin, String strPwd){
		boolean result=false;
		
		try{
			if(m_DbPath.trim().equalsIgnoreCase("")||m_DbVersion==-1||
					m_CryptKey.trim().equalsIgnoreCase("")||m_ULogin.trim().equalsIgnoreCase("")||m_UPwd.trim().equalsIgnoreCase(""))
				throw new Exception("Ошибка чтения настроек приложения");
			
			if(strLogin.trim().equalsIgnoreCase("")||strPwd.trim().equalsIgnoreCase(""))
				throw new Exception("Неверные логин или пароль");
			
			result=(strLogin.trim().equals(m_ULogin.trim())&&m_UPwd.trim().equals((new CryptographyCls()).Crypt(strPwd.trim(), m_CryptKey)));	
		}
		catch(Exception ex){
			String strErr="Ошибка в методе checkAuthorization - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(m_Ctx, "Авторизация не выполнена", Toast.LENGTH_LONG).show();
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
	public void writeToParcel(Parcel prcl, int flags) {
		try{
			prcl.writeString(m_DbPath);
			prcl.writeInt(m_DbVersion);
			prcl.writeString(m_XmlPath);
			prcl.writeString(m_CryptKey);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе writeToParcel - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	public static final Parcelable.Creator<InitializerCls> CREATOR = new Parcelable.Creator<InitializerCls>() { 
		public InitializerCls createFromParcel(Parcel prcl) {
	    	return new InitializerCls(prcl);
		}

	    public InitializerCls[] newArray(int size) {
	      return new InitializerCls[size];
	    }
	};
}
