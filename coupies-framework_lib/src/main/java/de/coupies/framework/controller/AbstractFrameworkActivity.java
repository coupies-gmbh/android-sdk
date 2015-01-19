package de.coupies.framework.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager.BadTokenException;

public abstract class AbstractFrameworkActivity extends FragmentActivity {
    
	public static final int RESULT_STICKER_READ = 11;
	public static final int RESULT_NO_STICKER = 12;
	public static final int RESULT_CONNECTION_ERROR = 13;
	public static final int RESULT_BAD_STICKER = 14;
	public static final int RESULT_CASHBACK_OK = 15;
    public static final int WALLET_NFC_RESULT = 16;
    public static final int RESULT_HTML_OK = 17;
    public static final int RESULT_OUT_OF_MEMORY = 18;
    public static final int RESULT_CAMERA_ERROR = 19;
    public static final int RESULT_RECEIPT_OK = 20;
	
	public static final int REQUEST_REDEEM_NO_STICKER = 1;
	public static final int REQUEST_REDEEM_WITH_STICKER = 2;
	public static final int REQUEST_REDEEM_CASHBACK = 3;
	public static final int REQUEST_UPLOAD_RECEIPT = 4;
	public static final int REQUEST_REDEEM_CASHBACK_MULTIPLE_RECEIPT = 5;
	
	public void finishWithResult(Bundle bundle, int resultCode) {
		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		setResult(resultCode, intent);
		
		finish();
	}
	
	protected void redirectForResult(Class<? extends Activity> target, Bundle bundle, int requestCode) {
		Intent intent = createIntent(target, bundle, -1);
		startActivityForResult(intent, requestCode);
	}
	
	private Intent createIntent(Class<? extends Activity> target, Bundle bundle, int flags) {
		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (flags != -1) {
			intent.setFlags(flags);
		}
		if (target != null) {
			intent.setClass(this, target);
		}
		return intent;
	}
	
	public void showChoiceDialog(final String inTitle, final String inText, 
			final String positiveText,
			final DialogInterface.OnClickListener positiveListener,
			final String negativeText,
			final DialogInterface.OnClickListener negativeListener) {
		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(AbstractFrameworkActivity.this);
				if(positiveListener !=null && positiveText !=null && positiveText.length()>0){
					builder.setPositiveButton(
							positiveText, 
							positiveListener);
				}
				if(negativeListener !=null && negativeText !=null && negativeText.length()>0){
					builder.setNegativeButton(
							negativeText, 
							negativeListener);
				}
				builder.setTitle(inTitle).setMessage(inText);
				try {
					builder.create().show();
				} catch (BadTokenException e) {
					e.printStackTrace();
					finishWithResult(null, RESULT_CAMERA_ERROR);
				}
				
			}
		});
	}
}
