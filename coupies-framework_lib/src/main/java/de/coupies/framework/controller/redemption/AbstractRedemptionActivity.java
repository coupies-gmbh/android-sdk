package de.coupies.framework.controller.redemption;

import java.io.File;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import de.coupies.coupies_framework_lib.R;
import de.coupies.framework.controller.AbstractFrameworkActivity;

public class AbstractRedemptionActivity extends AbstractFrameworkActivity {
	
    protected int couponId;
    protected int remaining;
    
    protected RedemptionController controller;
    protected String stickerCode;
    
    protected boolean setFullscreen=false;
    
    public void onCreate(Bundle bundle) {
    	if(!setFullscreen){
	    	requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setFullscreen=true;
    	}
    	super.onCreate(bundle);
    	
    	//Set Coupon and Location
	    Bundle extras  = getIntent().getExtras(); 
	    if(extras != null){
	    	couponId = extras.getInt("couponId", -1);
	    	remaining = extras.getInt("couponRemaining", 1);
	    }else{
	    	couponId = getIntent().getIntExtra("couponId", -1);
	    	remaining = getIntent().getIntExtra("couponRemaining", 1);
	    }
    	
    	// Falls kein Coupon mitgegeben wurde, fürt dies zum beenden des Einlösevorgangs
	    if(couponId == -1){
			Bundle errorBundle = new Bundle();
			errorBundle.putString("exception", getResources().getString(R.string.cashback_image_send_error_text_1));
			finishWithResult(errorBundle, RedemptionActivity.RESULT_CONNECTION_ERROR);
		}
		controller = RedemptionController.getInstance();
    }
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode!=WALLET_NFC_RESULT){
			setResult(resultCode, data);
			finish();
		}
	}
    
    public void showNoStickerDialog() {
    	String title = getResources().getString(R.string.dialog_no_sticker_title_text);
    	String body = getResources().getString(R.string.dialog_no_sticker_body_text);
    	String yes = getResources().getString(R.string.dialog_yes_text);
    	String no = getResources().getString(R.string.dialog_no_text);
    	showChoiceDialog(title, body, 
    			yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finishNoSticker();
					}
    			}, 
    			no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finishUserCanceled();
					}
    			});
	}
    
    protected void finishNoSticker() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("couponId", couponId);
		finishWithResult(bundle, RESULT_NO_STICKER);
    }
    
    protected void finishWithSticker() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("couponId", couponId);
		bundle.putString("stickerCode", stickerCode);
		finishWithResult(bundle, RESULT_STICKER_READ);
    }
    
    protected void finishWithBadSticker() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("couponId", couponId);
		bundle.putString("stickerCode", stickerCode);
		finishWithResult(bundle, RESULT_BAD_STICKER);
    }
    
    protected void finishCashbackOK(File imageFile){
//    	Bundle bundle = new Bundle();
//    	bundle.putInt("couponId", couponId);
//		bundle.putSerializable("image", imageFile);
//		finishWithResult(bundle, REQUEST_REDEEM_CASHBACK);
    	finishCashbackOK(imageFile, 1);
    }
    
    protected void finishCashbackOK(File imageFile, int number_articles){
    	Bundle bundle = new Bundle();
    	bundle.putInt("couponId", couponId);
		bundle.putSerializable("image", imageFile);
		bundle.putInt("quantity", number_articles);
		finishWithResult(bundle, REQUEST_REDEEM_CASHBACK);
    }
    
    protected void finishMultiReceiptCashbackOK(List<File> imageFiles){
    	finishMultiReceiptCashbackOK(imageFiles, 1);
    }
    
    protected void finishMultiReceiptCashbackOK(List<File> imageFiles, int number_articles){
    	Bundle bundle = new Bundle();
    	bundle.putInt("couponId", couponId);
    	bundle.putInt("quantity", number_articles);
    	int i=0;
    	for(File imageFile: imageFiles){
    		bundle.putSerializable("receipt_image_"+i, imageFile);
    		i++;
    	}
		finishWithResult(bundle, REQUEST_REDEEM_CASHBACK_MULTIPLE_RECEIPT);
    }
    
    protected void finishUserCanceled() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("couponId", couponId);
		finishWithResult(bundle, RESULT_CANCELED);
    }
}
