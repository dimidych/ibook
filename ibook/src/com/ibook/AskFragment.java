package com.ibook;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AskFragment extends DialogFragment implements OnClickListener {

	public static byte m_DialogResult=ConstCls.DIALOG_CANCEL;
	private String m_Message="";
	
	public AskFragment(String message){
		super();
		m_Message=message;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		    getDialog().setTitle("Вопросительный вопрос...");
		    View v = inflater.inflate(R.layout.ask_fragment, null);
		    ((TextView)(v.findViewById(R.id.lblMsg))).setText(m_Message);
		    v.findViewById(R.id.btnOkAbout).setOnClickListener(this);
		    v.findViewById(R.id.btnCancel).setOnClickListener(this);
		    return v;
		  }
	
	@Override
	public void onClick(View vw) {
		
		try{
			switch (vw.getId()) {
	         case R.id.btnOk:
	        	 m_DialogResult=ConstCls.DIALOG_OK; 
	           break;
	         case R.id.btnCancel:
	        	 m_DialogResult=ConstCls.DIALOG_CANCEL;
	           break;
	         }
		}
		finally{
			dismiss();
		}
	}

}
