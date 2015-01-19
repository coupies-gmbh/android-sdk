package de.coupies.framework.services.async.tasks;

import android.app.Dialog;
import android.os.AsyncTask;

public class AsyncPayoutTask extends AsyncTask<Void,Void,Boolean>{
	Dialog dialog;
	boolean showDialog;
	protected Exception error;
	private AsyncPayoutListener loadingListener;
	
	public AsyncPayoutTask(Dialog dialog, boolean showDialog, AsyncPayoutListener loadingListener){
		this.dialog = dialog;
		this.showDialog = showDialog;
		this.loadingListener = loadingListener;
	}
	
	@Override
	protected void onPreExecute() {
		if(dialog!=null && showDialog)
			dialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		return true;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
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
			loadingListener.onComplete();
		}else
			loadingListener.onError(error);
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		if(loadingListener != null)
			loadingListener.onCancel();
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
	
	public interface AsyncPayoutListener{
        
        public abstract void onComplete();

        public abstract void onError(Exception e);

        public abstract void onCancel();

	}
	
}

