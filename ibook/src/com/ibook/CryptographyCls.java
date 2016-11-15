package com.ibook;

import java.util.*;

import android.util.Log;

class CryptographyCls {
	
	private final String LOG_TAG="iBook - CryptographyCls";
	
	/** Default constructor */
	public CryptographyCls(){}
	
    /** Crypts current string.*/
 	public String Crypt(String line,String ckey){
    	String result = new String("");

        try{
        	if(line.trim().equals(""))
        		return "";
        	
            int l = 0, s = 0;
            int s0 = Integer.parseInt(ckey.split("~")[1]);
            int c = Integer.parseInt(ckey.split("~")[2]);
            int a = Integer.parseInt(ckey.split("~")[0]);

            Map<Integer,Integer> hs = new HashMap<Integer,Integer>();
            boolean val = true;

            while (val)
            {
                if (l == 0)
                {
                    hs.put(l, s0);
                    s = hs.get(l);
                }
                else
                {
                    hs.put(l, (a * hs.get(l - 1) + c) % 2048);
                    s = hs.get(l);

                    if (s == s0)
                        val = false;
                }

                l++;
            }

            hs.remove(l - 1);
            int[] m_Gamma = new int[l - 1];
            Object[] keyArr=hs.keySet().toArray(); 
            
            for(int i=0;i<keyArr.length;i++){
            	int key=(Integer)keyArr[i];
            	m_Gamma[key]=hs.get(key);
            }

            char[] liter = line.toCharArray();
            
            for (int i = 0; i < line.length(); i++)
                result += (char)(((int)liter[i]) ^ m_Gamma[i]);
        }
        catch (Exception ex){
        	String strErr="Ошибка в методе Crypt - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
        	return "";
        }

        return result;
    }

    /** Decrypts current string. */
    public String Decrypt(String line, String ckey){
    	String result = new String("");

        try{
        	if(line.trim().equals(""))
        		return "";
        	
            int l = 0, s = 0;
            int s0 = Integer.parseInt(ckey.split("~")[1]);
            int c = Integer.parseInt(ckey.split("~")[2]);
            int a = Integer.parseInt(ckey.split("~")[0]);

            Map<Integer,Integer> hs = new HashMap<Integer,Integer>();
            boolean val = true;

            while (val)
            {
                if (l == 0)
                {
                    hs.put(l, s0);
                    s = hs.get(l);
                }
                else
                {
                    hs.put(l, (a * hs.get(l - 1) + c) % 2048);
                    s = hs.get(l);
                    
                    if (s == s0)
                        val = false;
                }

                l++;
            }

            hs.remove(l - 1);
            int[] m_Gamma = new int[l - 1];
            Object[] keyArr=hs.keySet().toArray();
            
            for(int i=0;i<keyArr.length;i++){
            	int key=(Integer)keyArr[i];
            	m_Gamma[key] = hs.get(key);
            }
            	
            char[] liter = line.toCharArray();

            for (int i = 0; i < line.length(); i++)
                result += (char)(((int)liter[i]) ^ m_Gamma[i]);
        }
        catch (Exception ex){
        	String strErr="Ошибка в методе Decrypt - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
        	return "";
        }

        return result;
    }

    /** Selects key for Crypting/Decrypting method */
    public String GetKey(String line){
    	String result = new String("");

        try{
            byte tmp = 0;
            int a = 0;
            int s0 = 0;
            int c = 0;

            for (int i = 0; i < line.length(); i++)
            {
                tmp = (byte)line.toCharArray()[i];

                if (tmp % 2 == 0)
                    break;
            }

            a = tmp;

            while (a % 4 != 0)
                a++;

            a += 1;
            s0 = tmp * 3 + 13;
            c = tmp + 7;
            result = ""+a + "~" + s0 + "~" + c;
        }
        catch (Exception ex){
        	String strErr="Ошибка в методе GetKey - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
        	return "";
        }

        return result;
    }
}
