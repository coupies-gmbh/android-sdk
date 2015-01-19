package de.coupies.framework.services.async.tasks;

import java.util.List;

import android.app.Dialog;
import android.os.AsyncTask;
import de.coupies.framework.beans.Location;

public class AsyncLocationLoadingTask extends AsyncTask<Void,Void,Boolean>{
	protected List<Location> listResult;
	protected Location locationResult;
	Dialog dialog;
	boolean showDialog;
	protected Exception error;
	private AbstractAsyncLoadingListener loadingListener;
	
	private AsyncLocationLoadingTask(Dialog dialog, boolean showDialog){
		this.dialog = dialog;
		this.showDialog = showDialog;
	}
	
	public AsyncLocationLoadingTask(Dialog dialog, boolean showDialog, AsyncLocationListLoadingListener loadingListener){
		this(dialog, showDialog);
		this.loadingListener = loadingListener;
	}
	
	public AsyncLocationLoadingTask(Dialog dialog, boolean showDialog, AsyncLocationLoadingListener loadingListener){
		this(dialog, showDialog);
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
			if(loadingListener instanceof AsyncLocationListLoadingListener)
				((AsyncLocationListLoadingListener)loadingListener).onComplete(listResult);
			else
				((AsyncLocationLoadingListener)loadingListener).onComplete(locationResult);
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
	
	public interface AbstractAsyncLoadingListener{
		
		public abstract void onError(Exception e);

        public abstract void onCancel();
	}
	
	public interface AsyncLocationListLoadingListener extends AbstractAsyncLoadingListener{
        
        public abstract void onComplete(List<Location> locationList);

	}
	
	public interface AsyncLocationLoadingListener extends AbstractAsyncLoadingListener{
        
        public abstract void onComplete(Location location);

	}
	
}

