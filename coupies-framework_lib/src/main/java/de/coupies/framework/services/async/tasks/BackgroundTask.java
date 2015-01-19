package de.coupies.framework.services.async.tasks;

import de.coupies.framework.services.async.tasks.Executable.BackgroundProcess;
import android.app.Dialog;
import android.os.AsyncTask;

public class BackgroundTask extends AsyncTask<Void,Void,Boolean>{
	BackgroundProcess process;
	Dialog dialog;
	boolean showDialog;
	Throwable error;
	
	public BackgroundTask(BackgroundProcess process, Dialog dialog, boolean showDialog){
		this.process = process;
		this.dialog = dialog;
		this.showDialog = showDialog;
	}
	
	@Override
	protected void onPreExecute() {
		if(dialog!=null && showDialog)
			dialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		try{
			process.run();
		}catch(Exception e){
			e.printStackTrace();
			error = e;
			return false;
		}

		return true;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		// Hier könnte ein Progress angezeigt werden
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(dialog!=null && showDialog && dialog.isShowing()){
			try{
				dialog.dismiss();
				dialog=null;
			}catch(Exception e){
				// Problems with disable dialog but not critical
			}
		}
		if(result){
			process.runOnUiThread();
		}else
			process.onError(error);
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		if(dialog!=null && showDialog && dialog.isShowing()){
			try{
				dialog.dismiss();
				dialog=null;
			}catch(Exception e){
				// Problems with disable dialog but not critical
			}
		}
		super.onCancelled();
	}
	
}
