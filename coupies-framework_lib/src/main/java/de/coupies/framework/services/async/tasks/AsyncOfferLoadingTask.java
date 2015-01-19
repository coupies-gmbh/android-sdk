package de.coupies.framework.services.async.tasks;

import java.util.List;

import android.app.Dialog;
import android.os.AsyncTask;
import de.coupies.framework.beans.Offer;

public class AsyncOfferLoadingTask extends AsyncTask<Void,Void,Boolean>{
	protected List<Offer> listResult;
	protected Offer offerResult;
	Dialog dialog;
	boolean showDialog;
	protected Exception error;
	private AbstractAsyncLoadingListener loadingListener;
	
	private AsyncOfferLoadingTask(Dialog dialog, boolean showDialog){
		this.dialog = dialog;
		this.showDialog = showDialog;
	}
	
	public AsyncOfferLoadingTask(Dialog dialog, boolean showDialog, AsyncOfferListLoadingListener loadingListener){
		this(dialog, showDialog);
		this.loadingListener = loadingListener;
	}
	
	public AsyncOfferLoadingTask(Dialog dialog, boolean showDialog, AsyncOfferLoadingListener loadingListener){
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
			if(loadingListener instanceof AsyncOfferListLoadingListener)
				((AsyncOfferListLoadingListener)loadingListener).onComplete(listResult);
			else
				((AsyncOfferLoadingListener)loadingListener).onComplete(offerResult);
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
	
	public interface AsyncOfferListLoadingListener extends AbstractAsyncLoadingListener{
        
        public abstract void onComplete(List<Offer> offerList);

	}
	
	public interface AsyncOfferLoadingListener extends AbstractAsyncLoadingListener{
        
        public abstract void onComplete(Offer offer);

	}
	
}

