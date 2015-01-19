package de.coupies.framework.services.async.tasks;

import android.app.Dialog;
import android.os.AsyncTask;
import de.coupies.framework.beans.User;
import de.coupies.framework.session.CoupiesSession;

public class AsyncAuthenticationTask extends AsyncTask<Void,Void,Boolean>{
	protected CoupiesSession sessionResult;
	protected boolean booleanResult,
					  showDialog;
//	protected String stringResult;
	protected Integer integerResult;
	protected User userResult;
	
	Dialog dialog;

	protected Exception error;
	private AbstractAsyncLoadingListener loadingListener;
	
	private AsyncAuthenticationTask(Dialog dialog, boolean showDialog){
		this.dialog = dialog;
		this.showDialog = showDialog;
	}
	
	public AsyncAuthenticationTask(Dialog dialog, boolean showDialog, AsyncCoupiesSessionLoadingListener loadingListener){
		this(dialog, showDialog);
		this.loadingListener = loadingListener;
	}
	
	public AsyncAuthenticationTask(Dialog dialog, boolean showDialog, AsyncBooleanLoadingListener loadingListener){
		this(dialog, showDialog);
		this.loadingListener = loadingListener;
	}
	
	public AsyncAuthenticationTask(Dialog dialog, boolean showDialog, AsyncUserLoadingListener loadingListener){
		this(dialog, showDialog);
		this.loadingListener = loadingListener;
	}
	
//	public AsyncAuthenticationTask(Dialog dialog, boolean showDialog, AsyncStringLoadingListener loadingListener){
//		this(dialog, showDialog);
//		this.loadingListener = loadingListener;
//	}
	
	public AsyncAuthenticationTask(Dialog dialog, boolean showDialog, AsyncIntegerLoadingListener loadingListener){
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
			if(loadingListener instanceof AsyncCoupiesSessionLoadingListener)
				((AsyncCoupiesSessionLoadingListener)loadingListener).onComplete(sessionResult);
//			else if(loadingListener instanceof AsyncStringLoadingListener)
//				((AsyncStringLoadingListener)loadingListener).onComplete(stringResult);
			else if(loadingListener instanceof AsyncIntegerLoadingListener)
				((AsyncIntegerLoadingListener)loadingListener).onComplete(integerResult);
			else if(loadingListener instanceof AsyncUserLoadingListener)
				((AsyncUserLoadingListener)loadingListener).onComplete(userResult);
			else
				((AsyncBooleanLoadingListener)loadingListener).onComplete(booleanResult);
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
	
	public interface AsyncCoupiesSessionLoadingListener extends AbstractAsyncLoadingListener{
        
        public abstract void onComplete(CoupiesSession sessionResult);

	}
	
	public interface AsyncBooleanLoadingListener extends AbstractAsyncLoadingListener{
        
        public abstract void onComplete(boolean booleanResult);

	}
	
	public interface AsyncIntegerLoadingListener extends AbstractAsyncLoadingListener{
		public abstract void onComplete(Integer integerResult);
	}
	
	public interface AsyncUserLoadingListener extends AbstractAsyncLoadingListener{
		public abstract void onComplete(User userResult);
	}
	
}

