package de.coupies.framework.controller.redemption;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.coupies.coupies_framework_lib.R;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Receipt;
import de.coupies.framework.beans.Redemption;
import de.coupies.framework.http.HttpClient.HttpStatusException;
import de.coupies.framework.services.ReceiptService;
import de.coupies.framework.services.RedemtionService;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoordinateImpl;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.utils.DeviceUtils;

public class RedemptionActivity extends AbstractRedemptionActivity {
	
    protected Barcode barcode;
    private boolean hasSticker;
    private List<File> imageFileList;

    ProgressBar progSpinnerBar;
    private LinearLayout progressLayout;
    private RelativeLayout progressSpinnerLayout;
    
    private static TextView progressbar_update_Byte;
    private static TextView progressbar_update_percentage;
    private static SaundProgressBar shownProgressBar;
    private static int fileSize;
    private static UploadImageInBackground newBackgroundTask;
    
    private String problem_text;
    
    private boolean wantHtml = false;
    private boolean withProgress = false;
    private String requested_html;
    private Receipt resultReceipt;
    
    CoupiesServiceException fehlermeldung;
    
    private View cardBack;
    
    private int number_articles = 1;
    
    public void onCreate(Bundle bundle) {
    	super.onCreate(bundle);
    	initLayout();
    	Bundle extras  = getIntent().getExtras();
    	if(extras.containsKey("html_requested"))
    		wantHtml = extras.getBoolean("html_requested");
    	if(extras.containsKey("quantity"))
    		number_articles = extras.getInt("quantity");
    	
    	imageFileList = new ArrayList<File>();
    	for(int i=0;i<3;i++){
    		if(extras.containsKey("receipt_image_"+i))
				imageFileList.add((File)extras.getSerializable("receipt_image_"+i));
    	}
    	if(imageFileList.isEmpty() && extras.containsKey("image"))
    		imageFileList.add((File)extras.getSerializable("image"));
    	if(!imageFileList.isEmpty()){
    		if(progressSpinnerLayout!=null && progressLayout!=null ) {
	    		progressSpinnerLayout.setVisibility(View.GONE);
	    		progressLayout.setVisibility(View.VISIBLE);
    		}
	    	redeemCashback(withProgress);
    	}else{
    		if(progressLayout!=null)
    			progressLayout.setVisibility(View.GONE);
    		//Set Coupon and Location 
		    hasSticker = extras.getBoolean("hasSticker");
		    if (hasSticker) {
		    	stickerCode = (String) extras.getString("stickerCode");
		    }
		    else {
		    	stickerCode = null;	
		    }
		    if(wantHtml){
	    		redeemCoupon_html();
	    	}else{
	    		redeemCoupon();
	    	}
    	}
    }
    
    private void redeemCashback(boolean withProgress){
		try {
			uploadImage(withProgress);
		}
		catch (final Exception e) {
			finishError(e);
		}
    }
    
    private void initLayout() {
		setContentView(R.layout.coupies_custom_progress_dialog);

		progressSpinnerLayout = (RelativeLayout) this.findViewById(R.id.custom_dialog_placeholder_layout);
		
		progSpinnerBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
		progSpinnerBar.setIndeterminate(true);
		
		if(progressSpinnerLayout!=null && progSpinnerBar!=null)
			progressSpinnerLayout.addView(progSpinnerBar);
		
		progressLayout = (LinearLayout) this.findViewById(R.id.custom_dialog_progress_layout);
		if(progressLayout==null)
			withProgress=false;
		else 
			withProgress= true;
	}
    
    protected void redeemCoupon_html() {
		new Thread() {
			public void run() {
				try {
					requested_html = getRequestedHtml();
				}
				catch (final Exception e) {
					finishError(e);
				}
				finishSucessHTML();
			}
		}.start();
	}
    
    protected void redeemCoupon() {
		new Thread() {
			public void run() {
				try {
					barcode = getBarcode();
				}
				catch (final Exception e) {
					finishError(e);
				}
				finishSucess();
			}
		}.start();
	}
    
    public String getRequestedHtml() throws Exception {
		String htmlResponse;
		RedemtionService service = controller.getServiceFactory().createRedemtionService();
		
	    Location pos = DeviceUtils.getLastBestLocation(this, 60 * 5);
	    Coordinate position = new CoordinateImpl((float)pos.getLatitude(), (float)pos.getLongitude());
	    int accuracy = (int)pos.getAccuracy();
		if (hasSticker) {
			htmlResponse = service.redeemCoupon_html(controller.getCoupiesSession(), position, couponId, stickerCode);
		}
		else {
			htmlResponse = service.redeemCoupon_html(controller.getCoupiesSession(), position, accuracy, couponId);
		}
		return htmlResponse;
	}
    
    public Barcode getBarcode() throws Exception {
		Barcode barcode;
		RedemtionService service = controller.getServiceFactory().createRedemtionService();
		
	    Location pos = DeviceUtils.getLastBestLocation(this, 60 * 5);
	    Coordinate position = new CoordinateImpl((float)pos.getLatitude(), (float)pos.getLongitude());
	    int accuracy = (int)pos.getAccuracy();
		if (hasSticker) {
			barcode = service.redeemCoupon(
				controller.getCoupiesSession(), position, accuracy, couponId, stickerCode);
		}
		else {
			barcode = service.redeemCoupon(
					controller.getCoupiesSession(), position, accuracy, couponId);
		}
		return barcode;
	}
    
    public void uploadImage(boolean withProgress) throws DocumentParseException, HttpStatusException, UnsupportedEncodingException, Exception{
    	try{
			problem_text = getResources().getString(R.string.cashback_image_send_error_text_2);
		}catch (Exception e) {
			// passiert bei alten Versionen in denen diese Texte nicht vorhanden sind 
		}
    	
    	RedemtionService service = controller.getServiceFactory().createRedemtionService();
    	ReceiptService receiptService = controller.getServiceFactory().createUploadReceiptService();
		
	    Location pos = DeviceUtils.getLastBestLocation(this, 60 * 5);
	    Coordinate position = new CoordinateImpl((float)pos.getLatitude(), (float)pos.getLongitude());
	    int accuracy = (int)pos.getAccuracy();
	    
	    newBackgroundTask = new UploadImageInBackground();
	    newBackgroundTask.execute(new AsyncHelper(service, controller.getCoupiesSession(),position,accuracy,couponId,imageFileList, withProgress, receiptService));
    }
	
	private void finishSucess() {
		Bundle bundle = new Bundle();
		bundle.putInt("couponId", couponId);
		bundle.putSerializable("barcode", barcode);
		finishWithResult(bundle, RESULT_OK);
	}
	
	private void finishSucessReceiptUpload() {
		Bundle bundle = new Bundle();
		bundle.putInt("receiptId", resultReceipt.getId());
		bundle.putString("receiptImageUrl", resultReceipt.getImageUrl());
		finishWithResult(bundle, RESULT_RECEIPT_OK);
	}
	
	private void finishSucessHTML(){
		Bundle bundle = new Bundle();
		bundle.putString("resultHtml", requested_html);
		finishWithResult(bundle, RESULT_HTML_OK);
	}
	
	private void finishCashbackSucess(){
		Bundle bundle = new Bundle();
		bundle.putInt("couponId", couponId);
		bundle.putInt("quantity", number_articles);
		finishWithResult(bundle, RESULT_CASHBACK_OK);
	}
	
	private void finishError(Exception e) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("exception", e);
		finishWithResult(bundle, RESULT_CONNECTION_ERROR);
	}
	
	private class AsyncHelper{
		
		public AsyncHelper(RedemtionService service,
						CoupiesSession session,
						Coordinate position,
						int acc,
						int id,
						List<File> imageFiles,
						boolean withProgressBool,
						ReceiptService receiptService){
			newServiceHelper = service;
			newSessionHelper = session;
			positionHelper= position;
			accuracyHelper= acc;
			couponIdHelper = id;
			imageHelper = imageFiles;
			withProgress = withProgressBool;
			newReceiptService = receiptService;
		}
		
		ReceiptService newReceiptService = null;
		RedemtionService newServiceHelper=null;
		CoupiesSession newSessionHelper=null;
		Coordinate positionHelper = null;
		int accuracyHelper = 0;
		int couponIdHelper = 0;
		List<File> imageHelper = null;
		boolean withProgress = false;
	}

	private class UploadImageInBackground extends AsyncTask<AsyncHelper,Integer,Redemption> {
	    
		
		@Override
		protected void onPostExecute(Redemption redemption) {
			if(couponId == -2 && resultReceipt != null){
				// just for receipt uplaoding for loreal
				flipCard(RESULT_RECEIPT_OK);
			}else if(redemption == null && !wantHtml || wantHtml && (requested_html==null || requested_html.length()<=0)){
				if(fehlermeldung!=null)
					finishError(fehlermeldung);
				else
					finishError(new CoupiesServiceException(problem_text));
			}else if(wantHtml){
				flipCard(RESULT_HTML_OK);
			}else{
				flipCard(RESULT_CASHBACK_OK);
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress)
		{
			final int UIProgress = progress[0];
			final int UIFileSize = fileSize;
			
			new Thread() {
				public void run() {
					runOnUiThread(new Runnable() {
							public void run() {
								if(withProgress) {
									shownProgressBar.setMax(UIFileSize);
									shownProgressBar.setProgress(UIProgress);
									if(UIProgress<=UIFileSize){
										progressbar_update_Byte.setText(String.format("%s/%s KBytes",String.valueOf(UIProgress/1000) ,String.valueOf(UIFileSize/1000)));
										progressbar_update_percentage.setText(String.valueOf((int)(((double)UIProgress/UIFileSize)*100))+"%");
									}else{
										progressbar_update_Byte.setText(String.format("%s/%s KBytes",String.valueOf(UIFileSize/1000) ,String.valueOf(UIFileSize/1000)));
										progressbar_update_percentage.setText("100%");
									}
								}
							}
						});
					}
			}.start();
		}

		@Override
		protected void onPreExecute()
		{
			if(withProgress) {
				shownProgressBar = (SaundProgressBar) findViewById(R.id.custom_dialog_progress_bar);
				progressbar_update_Byte = (TextView)findViewById(R.id.progressbar_upload_file_size);
				progressbar_update_percentage = (TextView)findViewById(R.id.progressbar_percentage_progress);
				
				// Die Felder vorbesetzen
				shownProgressBar.setMax(fileSize);
				progressbar_update_Byte.setText(String.format("0/%s KBytes", fileSize/1000));
				progressbar_update_percentage.setText("0%");
			}
		}
		
		@Override
		protected Redemption doInBackground(AsyncHelper... params) {
			try {			
				if(wantHtml && couponId>-2){
					requested_html = params[0].newServiceHelper.redeemCashbackCoupon_html(params[0].newSessionHelper, params[0].positionHelper, params[0].accuracyHelper, params[0].couponIdHelper, params[0].imageHelper, params[0].withProgress, number_articles);
					return new Redemption();
				}else if(couponId == -2){
					resultReceipt = params[0].newReceiptService.uploadReceipt(params[0].newSessionHelper, params[0].imageHelper, params[0].withProgress);
					return new Redemption();
				}else
					return params[0].newServiceHelper.redeemCashbackCoupon(params[0].newSessionHelper, params[0].positionHelper, params[0].accuracyHelper, params[0].couponIdHelper, params[0].imageHelper,params[0].withProgress, number_articles);
			} catch (CoupiesServiceException e) {
				e.printStackTrace();
				fehlermeldung = e;
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}

	public static void notifyProgressBar(int progress, int httpFileSize){
		fileSize=httpFileSize;
		newBackgroundTask.onProgressUpdate(progress);
	}

	private void flipCard(int results)
	{
//		int layoutId = ResourceUtils.getResourceIdByName(getPackageName(), "id", "custom_dialog_root_layout");
//	    View rootLayout = (View) findViewById(layoutId);
	    View cardFace;
	    if(progressSpinnerLayout != null && progressSpinnerLayout.isShown())
	    	cardFace = (View)progressSpinnerLayout;
	    else
	    	cardFace = (View)progressLayout;
	    
	    switch (results) {
		case RESULT_CASHBACK_OK:
			cardBack = (View) findViewById(R.id.transfer_success_layout);
			cardBack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finishCashbackSucess();
				}
			});
			break;
		case RESULT_HTML_OK:
			cardBack = (View) findViewById(R.id.transfer_success_layout);
			cardBack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finishSucessHTML();
				}
			});
			break;
		case RESULT_RECEIPT_OK:
			cardBack = (View) findViewById(R.id.transfer_success_layout);
			cardBack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finishSucessReceiptUpload();
				}
			});
			break;
		default:
			break;
		}
	    
	    cardFace.setVisibility(View.GONE);
	    cardBack.setVisibility(View.VISIBLE);
	    
//	    FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

//	    if (cardFace.getVisibility() == View.GONE)
//	    {
//	        flipAnimation.reverse();
//	    }
//	    rootLayout.startAnimation(flipAnimation);
	}
	
	
}
