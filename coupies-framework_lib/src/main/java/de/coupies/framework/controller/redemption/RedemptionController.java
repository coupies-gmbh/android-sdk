package de.coupies.framework.controller.redemption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;

import com.google.zxing.client.android.CaptureActivity;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.beans.Reservation;
import de.coupies.framework.controller.AbstractController;
import de.coupies.framework.services.RedemtionService;
import de.coupies.framework.services.ServiceFactory;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public class RedemptionController extends AbstractController {

	public static final int REQUEST_REDEEM_WITH_STICKER = 1;
	public static final int REQUEST_REDEEM_NO_STICKER = 2;
	public static final int REQUEST_REDEEM_CASHBACK = 3;
	public static final int REQUEST_UPLOAD_RECEIPT = 4;
	public static final int REQUEST_REDEEM_CASHBACK_MULTIPLE_RECEIPT = 5;
	
	public static boolean IS_FROM_WALLET=false;
	private static boolean WANT_HTML=false;

	private static RedemptionController instance = null;
	
	private RedemptionController(CoupiesSession session, ServiceFactory factory) {
		super(session, factory);
		instance = this;
	}
	
	public static synchronized RedemptionController createInstance(CoupiesSession session, ServiceFactory factory) {
		instance = new RedemptionController(session, factory);
		return instance;
	}
	
	public static RedemptionController getInstance() {
		return instance;
	}
	
	/**
	 * @author larseimermacher
	 * @param context
	 * @param couponId
	 * @return
	 */
	public Reservation reserveCoupon(Activity context, int couponId) throws CoupiesServiceException{
		RedemtionService service = getServiceFactory().createRedemtionService();
		Reservation reservation = (Reservation) service.newReserve(getCoupiesSession(),couponId);
		return reservation;
	}
	
	/**
	 * @author larseimermacher
	 * @param context
	 * @param couponId
	 * @return
	 */
	public void deleteReservedCoupon(Activity context, int couponId) throws CoupiesServiceException{
		RedemtionService service = getServiceFactory().createRedemtionService();
		service.deleteReservation(getCoupiesSession(),couponId);
	}
	
	/**
	 * @author larseimermacher
	 * @param context
	 * @param couponId
	 * @throws CoupiesServiceException 
	 */
	public void redeemCoupon(Activity context,Coordinate position, int couponId) throws CoupiesServiceException{
		RedemtionService service = getServiceFactory().createRedemtionService();
		service.redeemCoupon(getCoupiesSession(), position, couponId);
	}
	
	public void redeemCoupon_html(Activity context, Coupon coupon, boolean wantHtml){
		WANT_HTML=wantHtml;
		redeemCoupon(context, coupon);
	}
	
	public void redeemCoupon(Activity context, Coupon coupon, boolean wallet){
		IS_FROM_WALLET=wallet;
		redeemCoupon(context, coupon);
	}

	public void redeemCoupon(Activity context, Coupon coupon) {
		//Create bundle to attach to intent
		final Bundle bundle = new Bundle();
		
		bundle.putInt("couponId", coupon.getId());
		
		if(WANT_HTML){
			bundle.putBoolean("html_requested", true);
//			WANT_HTML = false;
		}
		if(coupon.getAction()==1){
			if (isNfcAvailable(context) && coupon.getClosestLocationAcceptsSticker() /*|| IS_FROM_WALLET)*/) {			
				redirectForResult(context, CouponRedemptionNfc.class, bundle, REQUEST_REDEEM_WITH_STICKER);
			}
			else if (isCameraAvailable(context) && coupon.getClosestLocationAcceptsSticker() /*|| IS_FROM_WALLET)*/) {
				redirectForResult(context, CaptureActivity.class, bundle, REQUEST_REDEEM_WITH_STICKER);	
			}
			else {
				redirectForResult(context, RedemptionActivity.class, bundle, REQUEST_REDEEM_NO_STICKER);	
			}
		}else if(coupon.getAction()==2){
			String url = coupon.getTargetUrl();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.getApplication().startActivity(i);
		}else if(coupon.getAction()==3 && isCameraAvailable(context)){
			if(coupon.getRemaining() != null)
				bundle.putInt("couponRemaining", coupon.getRemaining());
			redirectForResult(context, CashbackRedemption.class, bundle, REQUEST_REDEEM_CASHBACK);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private boolean isNfcAvailable(Context context) {
		try {
			NfcAdapter adapter = null;
			NfcManager manager = (NfcManager)context.getSystemService(Context.NFC_SERVICE);
			adapter = manager.getDefaultAdapter();
			//check if NFC is enabled
			if (adapter != null && adapter.isEnabled()) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (java.lang.NoClassDefFoundError e) {
			return false;
		}catch (java.lang.SecurityException e) {
			// No NFC Permissions
			return false;
		}
	}
	
	private boolean isCameraAvailable(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
	
	public interface RedemptionListener{

        public abstract void onComplete(Barcode barcode, int couponId);
        
        public abstract void onComplete(int quantity);
        
        public abstract void onComplete(String html);

        public abstract void onError(Exception e);

        public abstract void onCancel();
        
        public abstract void onBadStickerRead();
	}
	
	public void redeemCallback(Activity context, int requestCode, int resultCode, Intent data, RedemptionListener listener) {
		int couponId = -1;
		int number_articles = 1;
		switch (resultCode) {
		case RedemptionActivity.RESULT_OK:
			Barcode barcode = (Barcode) data.getExtras().getSerializable("barcode");
			couponId = data.getExtras().getInt("couponId");
			listener.onComplete(barcode, couponId);
			break;
		case RedemptionActivity.RESULT_CASHBACK_OK:
			couponId = data.getExtras().getInt("couponId");
			int quantity = 1;
			if(data.getExtras().containsKey("quantity"))
				quantity = data.getExtras().getInt("quantity");
			
			listener.onComplete(quantity);
			break;
		case RedemptionActivity.RESULT_HTML_OK:
			String html = data.getExtras().getString("resultHtml");
			listener.onComplete(html);
			break;
		case RedemptionActivity.RESULT_STICKER_READ:
			Bundle extras = data.getExtras();
			couponId = data.getExtras().getInt("couponId");
			String stickerCode = (String) extras.getSerializable("stickerCode");
			startRedemptionActivity(context, couponId, stickerCode);
			break;
		case RedemptionActivity.RESULT_NO_STICKER:
			extras = data.getExtras();
			couponId = data.getExtras().getInt("couponId");
			startRedemptionActivity(context, couponId);
			break;
		case RedemptionActivity.REQUEST_REDEEM_CASHBACK:
			extras = data.getExtras();
			couponId = data.getExtras().getInt("couponId");
			if(data.getExtras().containsKey("quantity"))
				number_articles = data.getExtras().getInt("quantity");
			File image = (File)extras.getSerializable("image");
			startRedemptionActivity(context, image, couponId, number_articles);
			break;
		case RedemptionActivity.REQUEST_UPLOAD_RECEIPT:
			extras = data.getExtras();
			File receipt = (File)extras.getSerializable("image");
			startRedemptionActivity(context, receipt);
			break;
		case RedemptionActivity.REQUEST_REDEEM_CASHBACK_MULTIPLE_RECEIPT:
			extras = data.getExtras();
			couponId = data.getExtras().getInt("couponId");
			if(data.getExtras().containsKey("quantity"))
				number_articles = data.getExtras().getInt("quantity");
			List<File> tempList = new ArrayList<File>();
			for(int i=0;i<3;i++){
				if(extras.containsKey("receipt_image_"+i))
				tempList.add((File)extras.getSerializable("receipt_image_"+i));
			}
			startRedemptionActivity(context, tempList, couponId, number_articles);
			break;
		case RedemptionActivity.RESULT_CANCELED:
			listener.onCancel();
			break;
		case RedemptionActivity.RESULT_BAD_STICKER:
			listener.onBadStickerRead();
			break;
		case RedemptionActivity.RESULT_CONNECTION_ERROR:
			Exception e = null;
			if(data.getExtras().getSerializable("exception") instanceof String)
				e = new Exception((String)data.getExtras().getSerializable("exception"));
			else if(data.getExtras().getSerializable("exception") instanceof Exception)
				e = (Exception)data.getExtras().getSerializable("exception");
			listener.onError(e);
			break;
		}
	}
	
	private void startRedemptionActivity(Activity context, int couponId, String stickerCode) {
    	if (stickerCode == null) {
    		startRedemptionActivity(context,couponId);
    	}
    	else {
			Bundle bundle = new Bundle();
			if(WANT_HTML){
				bundle.putBoolean("html_requested", true);
				WANT_HTML = false;
			}
	    	bundle.putSerializable("couponId", couponId);
	    	bundle.putString("stickerCode", stickerCode);
	    	bundle.putBoolean("hasSticker", true);
	    	redirectForResult(context, RedemptionActivity.class, bundle, REQUEST_REDEEM_WITH_STICKER);
    	}
    }
	/**
	 * Methode zum Einlösen von Cashbackcoupons
	 * @param context
	 * @param file	Imagefile zum hochladen
	 * @param coupon	Das zugehörige Couponobjekt
	 */
	private void startRedemptionActivity(Activity context, File imageFile, int couponId, int number_articles){
		Bundle bundle = new Bundle();
		if(WANT_HTML){
			bundle.putBoolean("html_requested", true);
			WANT_HTML = false;
		}
		bundle.putInt("couponId", couponId);
		bundle.putInt("quantity", number_articles);
		bundle.putSerializable("image", imageFile);
		redirectForResult(context, RedemptionActivity.class, bundle, REQUEST_REDEEM_CASHBACK);
	}
	
	private void startRedemptionActivity(Activity context, List<File> imageFiles, int couponId, int number_articles){
		Bundle bundle = new Bundle();
		if(WANT_HTML){
			bundle.putBoolean("html_requested", true);
			WANT_HTML = false;
		}
		bundle.putInt("couponId", couponId);
		bundle.putInt("quantity", number_articles);
		int i=0;
		for(File tempFile : imageFiles){
			bundle.putSerializable("receipt_image_"+i, tempFile);
			i++;
		}
		redirectForResult(context, RedemptionActivity.class, bundle, REQUEST_REDEEM_CASHBACK);
	}
	
	private void startRedemptionActivity(Activity context, File imageFile){
		Bundle bundle = new Bundle();
		if(WANT_HTML){
			bundle.putBoolean("html_requested", true);
			WANT_HTML = false;
		}
		bundle.putInt("couponId", -2);
		bundle.putSerializable("image", imageFile);
		redirectForResult(context, RedemptionActivity.class, bundle, REQUEST_UPLOAD_RECEIPT);
	}
	
	private void startRedemptionActivity(Activity context, int couponId) {
    	Bundle bundle = new Bundle();
    	if(WANT_HTML){
			bundle.putBoolean("html_requested", true);
			WANT_HTML = false;
		}
    	bundle.putInt("couponId", couponId);
    	bundle.putBoolean("hasSticker", false);
    	redirectForResult(context, RedemptionActivity.class, bundle, REQUEST_REDEEM_NO_STICKER);
    }
	
}
