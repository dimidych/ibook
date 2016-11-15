package com.ibook;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TabHost;

public class SettingsActivity extends TabActivity {
	
	private final String LOG_TAG="iBook - SettingsActivity";
	private InitializerCls m_InitInst=null;
	
	/** Occurs on current activity loading */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		this.setTitle(R.string.title_activity_settings);
		
		try{
		    Intent intent=getIntent();
			m_InitInst=(InitializerCls)(intent.getParcelableExtra(InitializerCls.class.getCanonicalName()));
			TabHost tabHost = getTabHost();
	        TabHost.TabSpec tabSpec;
	        
	        /** Extra tab */
	        tabSpec = tabHost.newTabSpec("Extra");
	        tabSpec.setIndicator("Extra", getResources().getDrawable(R.drawable.exclamation_octagon_fram));
	        Intent extraSettingsInt=new Intent(this, ExtraSettingsTab.class);
	        extraSettingsInt.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
	        tabSpec.setContent(extraSettingsInt);
	        tabHost.addTab(tabSpec);
	        
	        /** Group tab */
	        tabSpec = tabHost.newTabSpec("Groups");
	        tabSpec.setIndicator("Группы", getResources().getDrawable(R.drawable.cli_adium));
	        Intent groupSettingsInt=new Intent(this, UserGroupSettingsTab.class);
	        groupSettingsInt.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
	        tabSpec.setContent(groupSettingsInt);
	        tabHost.addTab(tabSpec);
	        
	        /** Field tab */
	        tabSpec = tabHost.newTabSpec("Fields");
	        tabSpec.setIndicator("Поля", getResources().getDrawable(R.drawable.cli_fring));
	        Intent fieldSettingsInt=new Intent(this, FieldSettingsTab.class);
	        fieldSettingsInt.putExtra(InitializerCls.class.getCanonicalName(),m_InitInst);
	        tabSpec.setContent(fieldSettingsInt);
	        tabHost.addTab(tabSpec);
	    }
	    catch(Exception ex){
			String strErr="Ошибка при загрузке - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

}
