package com.ibook;

import java.util.ArrayList;
import java.util.Date;
import java.io.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;

class CollectedWMValues{
	public String m_GrpName="",m_Name="",m_HomePhone="",m_WorkPhone="",
			m_MobilePhone="",m_ElsePhone="",m_Mail="",m_Street="",m_Home="",m_Apartments="";
}

class CollectedValues{
	public String m_GrpName="",m_Name="";
	public ArrayList<ContactSetCls> m_ContactSetArr=new ArrayList<ContactSetCls>();
}

class XmlDbWorkerCls extends XmlUtilsCls {
	
	private final String LOG_TAG="iBook - XmlDbWorkerCls";
	private DbWorkerCls m_DbWrkObj=null;
	private String m_XmlPath="";
	
	/** Not default constructor */
	public XmlDbWorkerCls(DbWorkerCls dbWrkObj,String xmlPath){
		m_DbWrkObj=dbWrkObj;
		m_XmlPath=xmlPath;
	}
	
	/** Adds data from new format ebook.xml - crypted or non-crypted */
	public boolean AddDataFromEbook(boolean isCrypted){
		boolean result=false;
		
		try{
			XmlPullParser parser = createParserFromFile(m_XmlPath.trim().split("~")[0],m_XmlPath.trim().split("~")[1]);
			String nodeName="";
			CollectedValues collValsInst=null;
			//Toast.makeText(m_Ctx, "Начало обработки XML", Toast.LENGTH_LONG).show();
			//int counter=0;
			
			while (parser.getEventType()!= XmlPullParser.END_DOCUMENT){  
				switch(parser.getEventType()){
					case XmlPullParser.START_TAG:{
						nodeName=parser.getName().trim();
					
						if(nodeName.trim().equals("eBook")){
							collValsInst=new CollectedValues();
						}
						else if(parser.getName().trim().equals("Имя")){
							String strName=parser.nextText();
							collValsInst.m_Name=isCrypted?m_DbWrkObj.m_CryptInst.Decrypt(strName,m_DbWrkObj.m_CryptKey):strName;
							//Toast.makeText(m_Ctx, "Контакт - "+collValsInst.m_Name, Toast.LENGTH_LONG).show();
						}
						else if(parser.getName().trim().equals("Группа")){
							String strGroup=parser.nextText();
							collValsInst.m_GrpName=isCrypted?m_DbWrkObj.m_CryptInst.Decrypt(strGroup,m_DbWrkObj.m_CryptKey):strGroup;
						}
						else if(parser.getName().trim().equals("Контактная_запись")){
							if(parser.getAttributeCount()>0){
								FieldCls fieldObj=new FieldCls(m_DbWrkObj);
								String cntctSetType=parser.getAttributeValue(0);
								fieldObj=fieldObj.getField(-1, isCrypted?m_DbWrkObj.m_CryptInst.Decrypt(cntctSetType,m_DbWrkObj.m_CryptKey):cntctSetType);
								String cntctSetVal=parser.nextText();
								ContactSetCls cntctSetObj=new ContactSetCls(-1,-1,fieldObj,isCrypted?m_DbWrkObj.m_CryptInst.Decrypt(cntctSetVal,m_DbWrkObj.m_CryptKey):cntctSetVal);
								collValsInst.m_ContactSetArr.add(cntctSetObj);
							}
						}
					}
					break;
					
					case XmlPullParser.END_TAG:{
						nodeName=parser.getName().trim();
						
						if(nodeName.trim().equals("eBook")&&collValsInst!=null){
							ProcessXmlData(collValsInst);
							//counter++;
						}
					}
					break;
				}
				
				parser.next();
			}
				
			//Toast.makeText(m_Ctx, "Обработка XML завершена. Обработано записей - "+counter, Toast.LENGTH_LONG).show();
			result=true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе AddDataFromEbook - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Adds data from wm ebook.xml */
	public boolean AddDataFromWmEbook(){
		boolean result=false;
		
		try{
			XmlPullParser parser = createParserFromFile(m_XmlPath.trim().split("~")[0],m_XmlPath.trim().split("~")[1]);
			String nodeName="";
			CollectedWMValues collValsInst=null;
			//Toast.makeText(m_Ctx, "Начало обработки XML", Toast.LENGTH_LONG).show();
			//int counter=0;
			
			while (parser.getEventType()!= XmlPullParser.END_DOCUMENT){  
				switch(parser.getEventType()){
					case XmlPullParser.START_TAG:{
						nodeName=parser.getName().trim();
						
						if(nodeName.trim().equals("eBook")){
							collValsInst=new CollectedWMValues();
						}
						else if(parser.getName().trim().equals("Имя")){
							collValsInst.m_Name=parser.nextText();
							//Toast.makeText(m_Ctx, "Контакт - "+collValsInst.m_Name, Toast.LENGTH_LONG).show();
						}
						else if(parser.getName().trim().equals("Домашний_Телефон"))
							collValsInst.m_HomePhone=parser.nextText();
						else if(parser.getName().trim().equals("Мобильный_Телефон"))
							collValsInst.m_MobilePhone=parser.nextText();
						else if(parser.getName().trim().equals("Рабочий_Телефон"))
							collValsInst.m_WorkPhone=parser.nextText();
						else if(parser.getName().trim().equals("Ещё_Телефон"))
							collValsInst.m_ElsePhone=parser.nextText();
						else if(parser.getName().trim().equals("Мыло"))
							collValsInst.m_Mail=parser.nextText();
						else if(parser.getName().trim().equals("Улица"))
							collValsInst.m_Street=parser.nextText();
						else if(parser.getName().trim().equals("Дом"))
							collValsInst.m_Home=parser.nextText();
						else if(parser.getName().trim().equals("Квартира"))
							collValsInst.m_Apartments=parser.nextText();
						else if(parser.getName().trim().equals("Инфа"))
							collValsInst.m_GrpName=parser.nextText();
					}
					break;
				
					case XmlPullParser.END_TAG:{
						nodeName=parser.getName().trim();
						
						if(nodeName.trim().equals("eBook")&&collValsInst!=null){
							ProcessWmXmlData(collValsInst);
							//counter++;
						}
					}
					break;
				}

				parser.next();
			}
			
			//Toast.makeText(m_Ctx, "Обработка XML завершена. Обработано записей - "+counter, Toast.LENGTH_LONG).show();
			result=true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе AddDataFromWmEbook - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Processes contact data from wm xml */
	private boolean ProcessWmXmlData(CollectedWMValues collValsInst){
		boolean result=false;
		
		try{
			if(collValsInst.m_Name!=null&&!collValsInst.m_Name.trim().equals("")){
				PersonCls personObj=new PersonCls(m_DbWrkObj);
				
				if(!personObj.checkPersonExistence(-1, collValsInst.m_Name)){
					ArrayList<ContactSetCls> personContactSet=new ArrayList<ContactSetCls>();

					if(collValsInst.m_HomePhone!=null&&!collValsInst.m_HomePhone.trim().equals(""))
							personContactSet.add(new ContactSetCls(-1,-1,
									(new FieldCls(m_DbWrkObj)).getField(-1, "Домашний телефон"),collValsInst.m_HomePhone.trim()));

					if(collValsInst.m_MobilePhone!=null&&!collValsInst.m_MobilePhone.trim().equals(""))
							personContactSet.add(new ContactSetCls(-1,-1,
									(new FieldCls(m_DbWrkObj)).getField(-1, "Мобильный телефон"),collValsInst.m_MobilePhone.trim()));

					if(collValsInst.m_WorkPhone!=null&&!collValsInst.m_WorkPhone.trim().equals(""))
							personContactSet.add(new ContactSetCls(-1,-1,
									(new FieldCls(m_DbWrkObj)).getField(-1, "Рабочий телефон"),collValsInst.m_WorkPhone.trim()));

					if(collValsInst.m_ElsePhone!=null&&!collValsInst.m_ElsePhone.trim().equals(""))
							personContactSet.add(new ContactSetCls(-1,-1,
									(new FieldCls(m_DbWrkObj)).getField(-1, "Еще телефон"),collValsInst.m_ElsePhone.trim()));

					if(collValsInst.m_Mail!=null&&!collValsInst.m_Mail.trim().equals(""))
							personContactSet.add(new ContactSetCls(-1,-1,
									(new FieldCls(m_DbWrkObj)).getField(-1, "Мыло"),collValsInst.m_Mail.trim()));
					
					String address="";
					
					if(collValsInst.m_Street!=null&&!collValsInst.m_Street.trim().equals(""))
						address+=collValsInst.m_Street+" ";
					
					if(collValsInst.m_Home!=null&&!collValsInst.m_Home.trim().equals(""))
						address+=collValsInst.m_Home+" ";
						
					if(collValsInst.m_Apartments!=null&&!collValsInst.m_Apartments.trim().equals(""))
						address+=collValsInst.m_Apartments;
					
					if(!address.trim().equals(""))
						personContactSet.add(new ContactSetCls(-1,-1,
							(new FieldCls(m_DbWrkObj)).getField(-1, "Адрес"),address.trim()));

					GroupCls groupInst=new GroupCls(m_DbWrkObj);
					
					if(collValsInst.m_GrpName!=null&&!collValsInst.m_GrpName.trim().equals("")){	
						if(!groupInst.checkGroupExistence(-1, collValsInst.m_GrpName))
							groupInst.addGroup(collValsInst.m_GrpName);
						
						groupInst=groupInst.getGroupInfo(-1, collValsInst.m_GrpName);
					}
					else
						groupInst=groupInst.getGroupInfo(-1, "Прочие");
					
					personObj.addPerson(collValsInst.m_Name, groupInst, personContactSet);
				}
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в методе AddDataFromWmEbook - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Processes contact data from xml */
	private boolean ProcessXmlData(CollectedValues collValsInst){
		boolean result=false;
		
		try{
			if(collValsInst.m_Name!=null&&!collValsInst.m_Name.trim().equals("")){
				PersonCls personObj=new PersonCls(m_DbWrkObj);
				
				if(!personObj.checkPersonExistence(-1, collValsInst.m_Name)){
					GroupCls groupInst=new GroupCls(m_DbWrkObj);
					
					if(collValsInst.m_GrpName!=null&&!collValsInst.m_GrpName.trim().equals("")){	
						if(!groupInst.checkGroupExistence(-1, collValsInst.m_GrpName))
							groupInst.addGroup(collValsInst.m_GrpName);
						
						groupInst=groupInst.getGroupInfo(-1, collValsInst.m_GrpName);
					}
					else
						groupInst=groupInst.getGroupInfo(-1, "Прочие");
					
					personObj.addPerson(collValsInst.m_Name, groupInst, collValsInst.m_ContactSetArr);
				}
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в методе AddDataFromWmEbook - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/**Writes startup settings into xml*/
	public boolean writeXmlData(boolean isCrypted){
		boolean result=false;
		
		try{
			PersonCls contactObj=new PersonCls(m_DbWrkObj);
			ArrayList<PersonCls> personList=contactObj.getAllContactList();
			String strDirName="";

			if(personList!=null&&personList.size()>0){
				strDirName="Documents/ebook"+DateFormat.format("dd-MM-yyyy-hh-mm-ss", (new Date()));
				FileOutputStream targetFile=writeFileStream(strDirName,"eBook.xml");				
				XmlSerializer serializer = Xml.newSerializer();
				serializer.setOutput(targetFile,"utf-8");
		        serializer.startDocument("windows-1251", Boolean.valueOf(true));
		        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

		        for(int i=0;i<personList.size();i++){
			        serializer.startTag("", "eBook");
			        serializer.startTag("", "Имя");
			        serializer.text(isCrypted?m_DbWrkObj.m_CryptInst.Crypt(personList.get(i).m_PersonName.trim(),m_DbWrkObj.m_CryptKey):
			        	personList.get(i).m_PersonName.trim());
			        serializer.endTag("", "Имя");
			        serializer.startTag("", "Группа");
			        serializer.text(isCrypted?m_DbWrkObj.m_CryptInst.Crypt(personList.get(i).m_GroupObj.m_GroupName.trim(),m_DbWrkObj.m_CryptKey):
			        	personList.get(i).m_GroupObj.m_GroupName.trim());
			        serializer.endTag("", "Группа");

			        ArrayList<ContactSetCls> cnctSetLst=(new ContactSetCls(m_DbWrkObj)).getPersonRelatedFields(personList.get(i).m_PersonId); 
			        
			        if(cnctSetLst!=null&&cnctSetLst.size()>0)
			        	for(int j=0;j<cnctSetLst.size();j++){
			        		serializer.startTag("", "Контактная_запись");
			        		serializer.attribute("", "тип", isCrypted?m_DbWrkObj.m_CryptInst.Crypt(cnctSetLst.get(j).m_FieldDefinition.m_FieldName.trim(),m_DbWrkObj.m_CryptKey):
			        			cnctSetLst.get(j).m_FieldDefinition.m_FieldName.trim());
					        serializer.text(isCrypted?m_DbWrkObj.m_CryptInst.Crypt(cnctSetLst.get(j).m_FieldValue.trim(),m_DbWrkObj.m_CryptKey):
					        	cnctSetLst.get(j).m_FieldValue.trim());
					        serializer.endTag("", "Контактная_запись");
			        	}

			        serializer.endTag("", "eBook");
		        }
		        
		        serializer.endDocument();
		        serializer.flush();
		        targetFile.close();
		        result=true;
	        }
		}
		catch(Exception ex){
			String strErr="Ошибка в методе WriteSettings - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		Log.d(LOG_TAG,"xml writing finish");
		return result;
	}
	
}
