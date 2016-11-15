package com.ibook;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

public class UtilsCls {
	
	private final static String LOG_TAG="iBook - UtilsCls";
	
	/** Checks whether application with specified name exists */
	public static boolean checkApplicationExistance(String appName,Context ctx){
		boolean result=false;
		
		try{
			PackageManager pm = ctx.getPackageManager();
			pm.getPackageInfo(appName, 0);
			result=true;
		}
		catch(Exception ex){
			//Log.d(LOG_TAG,"Ошибка получения информации о приложении - "+ex.getMessage());
			return false;
		}
		
		return result;
	}
	
	/** Checks whether related to application intent is available */
	public static boolean isIntentAvailable(Context ctx,Intent intent){
		boolean result=false;
		
		try{
			final PackageManager packageManager = ctx.getPackageManager();
		    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		    result=( list.size() > 0);
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка получения информации о приложении - "+ex.getMessage());
			return false;
		}
		
		return result;
	}
	
	/** Sends message to log */
	public static void LogMessage(Context ctx,String logTag,String message,byte showDialog,byte rethrow) throws Exception{
		Log.d(logTag,message);
		
		if(showDialog==1)
			Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
		
		if(rethrow==1)
			throw new Exception(message);
	}
}
