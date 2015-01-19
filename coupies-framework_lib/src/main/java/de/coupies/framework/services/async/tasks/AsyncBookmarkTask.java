package de.coupies.framework.services.async.tasks;

import java.util.List;

import de.coupies.framework.beans.Offer;
import android.app.Dialog;
import android.os.AsyncTask;

public class AsyncBookmarkTask extends AsyncTask<Void,Void,Boolean>{
	protected List<Offer> bookmarkResult;
	Dialog dialog;
	boolean showDialog;
	protected Exception error;
	private AsyncBookmarkLoadingListener loadingListener;
	
	public AsyncBookmarkTask(Dialog dialog, boolean showDialog, AsyncBookmarkLoadingListener loadingListener){
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
			if(loadingListener != null && bookmarkResult != null)
				loadingListener.onComplete(bookmarkResult);
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
	
	public interface AsyncBookmarkLoadingListener{
        
        public abstract void onComplete(List<Offer> offerList);

        public abstract void onError(Exception e);

        public abstract void onCancel();

	}
	
}

