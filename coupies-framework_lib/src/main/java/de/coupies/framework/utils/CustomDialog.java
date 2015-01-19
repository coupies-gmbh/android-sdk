package de.coupies.framework.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import de.coupies.coupies_framework_lib.R;

public class CustomDialog extends Dialog {
	Context calledActivity;

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		this.calledActivity = context;
	}
	
	public void setNewActivity(Context caller){
		this.calledActivity = caller;
	}

	@Override
	public View findViewById(int id) {
		// TODO Auto-generated method stub
		return super.findViewById(id);
	}

	@Override
	public void setContentView(View view) {
		// TODO Auto-generated method stub
		super.setContentView(view);
	}

	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		super.setTitle(title);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(this.calledActivity instanceof Activity){
			return ((Activity)calledActivity).dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
		
	}

	@Override
	public void show() {
		try{
			RelativeLayout progressBarLayout = (RelativeLayout)findViewById(R.id.custom_dialog_placeholder_layout);
			ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
			progressBar.setIndeterminate(true);
			progressBarLayout.addView(progressBar);
			super.show();
		}catch(IllegalArgumentException ie){
			ie.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
	 
