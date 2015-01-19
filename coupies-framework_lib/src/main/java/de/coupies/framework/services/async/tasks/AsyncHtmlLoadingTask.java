package de.coupies.framework.services.async.tasks;

import android.app.Dialog;
import android.os.AsyncTask;

public class AsyncHtmlLoadingTask extends AsyncTask<Void,Void,Boolean>{
	protected String htmlResult;
	Dialog dialog;
	boolean showDialog;
	protected Exception error;
	private AsyncHtmlLoadingListener loadingListener;
	
	public AsyncHtmlLoadingTask(Dialog dialog, boolean showDialog, AsyncHtmlLoadingListener loadingListener){
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
			loadingListener.onComplete(htmlResult);
		}else
			loadingListener.onError(error);
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
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
	
	public interface AsyncHtmlLoadingListener{
        
        public abstract void onComplete(String html);

        public abstract void onError(Exception e);

        public abstract void onCancel();

	}
	
}

