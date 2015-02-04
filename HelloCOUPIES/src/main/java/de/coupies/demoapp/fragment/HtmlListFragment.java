package de.coupies.demoapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.MailTo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.coupies.demoapp.R;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.controller.redemption.RedemptionController;
import de.coupies.framework.controller.redemption.RedemptionController.RedemptionListener;
import de.coupies.framework.services.CouponService;

/** 
 * This is the simplest way to integrate coupons in your application: Display one or several coupons in
 * a WebView. Clicks on "redeem now" are intercepted and delegated to the COUPIES-framework to handle
 * redemtions using the COUPIES-Touchpoint. This example uses the HTML representation of the COUPIES-API only.
 * 
 * @author larseimermacher
 */
public class HtmlListFragment extends AbstractFragment {
	
	/**
	 * This class implements the RedemptionListener.
	 * Methods of this listener will be called by the COUPIES-Framework 
	 * after completed an redemption or if an error occurred.
	 */
	private class CouponRedemptionListener implements RedemptionListener {
		Activity listenerActivity;
		
		public CouponRedemptionListener(Activity activity) {
			listenerActivity = activity;
		}
		
		public void onComplete(String html) {
			/* This method is called after the redemption was successfully completed */
			listWebView.loadDataWithBaseURL(baseUrl, html, MIME_TYPE, ENCODING, baseUrl);
		}
		
		public void onComplete(Barcode barcode, int couponid) {
			// never gets called
		}
		
		public void onComplete(int couponId){
			// never gets called
		}

		public void onError(Exception e) {
			e.printStackTrace();
			alert(listenerActivity,"Ein Problem ist aufgetreten", e.getMessage(), new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}

		public void onCancel() {
			System.out.println("Cancel...");
		}
		
		public void onBadStickerRead() {
			alert(listenerActivity,"Fehlerhafte Einl�sung", "Der Coupon geh�t nicht zu dem Coupies-Touchpoint", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
	}
	
	/**
	 * This is an JavaScript-Interface for the WebView.
	 * With this interface we are able to read  information
	 * about the coupon being shown in the WebView 
	 */
	class CoupiesJavaScriptInterface{
		@JavascriptInterface
		 public void getCouponId(String id){
	    	if(id!=null){
		    	try{
		    		couponId = Integer.parseInt(id);
		    		coupon.setId(couponId);
		    	}catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
		@JavascriptInterface
	    public void getCouponAction(String action){
	    	if(action != null){
		    	try{
		    		actionId = Integer.parseInt(action);
		    		coupon.setAction(actionId);
		    	}catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
		@JavascriptInterface
	    public void getClosestLocationAcceptSticker(String mAcceptSticker){
	    	if(mAcceptSticker != null){
		    	try{
		    		acceptsSticker = mAcceptSticker.equals("1");
		    		coupon.setClosestLocationAcceptsSticker(acceptsSticker);
		    	}catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
		
		@JavascriptInterface
	    public void setRemaining(String mRemaining){
	    	if(mRemaining != null){
		    	try{
		    		remaining = Integer.parseInt(mRemaining);
		    		coupon.setRemaining(remaining);
		    	}catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	private static final String ENCODING = "UTF-8";
	private static final String MIME_TYPE = "text/html";
	
	private static RedemptionController controller;
	private CouponRedemptionListener listener;
	private Coupon coupon;
	private Activity mActivity;
	
	private View rootView;
	
	private int actionId;
	private int couponId;
	private int remaining;
	private boolean acceptsSticker;
	private boolean onDetailPage = false;
	
	private String baseUrl;
	
	private WebView listWebView;
	String couponListHTML;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.main, container, false);
		
		mActivity=getActivity();
		listener = new CouponRedemptionListener(getActivity());
		
        if(checkForApiCode()) {
				listWebView = (WebView)rootView.findViewById(R.id.couponListWebView);
				
				baseUrl = getServiceFactory().getAPIBaseUrl();
				
				/* Enable JavaScript */
				listWebView.getSettings().setJavaScriptEnabled(true);
				/* Register a new JavaScript interface called HTMLOUT */
				listWebView.addJavascriptInterface(new CoupiesJavaScriptInterface(), "COUPON");
				
				listWebView.setWebViewClient(new WebViewClient() {
					@Override
					/* Stop loading the URL if the Redemption-Button is clicked */
					public boolean shouldOverrideUrlLoading(WebView view, String url) {

						if(url.startsWith("mailto:")){
			                MailTo mt = MailTo.parse(url);
			                Intent intent = new Intent(Intent.ACTION_SEND);	                
			                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
			                intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
			                intent.setType("message/rfc822");
			                mActivity.startActivity(intent);

			                return true;
			            }else if(url.contains("redemptions/new") ){
								listWebView.stopLoading();
								usingCoupiesRedemption();
								return true;
						}else if(url.contains("/redemptions/cashback/preview") ){
							listWebView.stopLoading();
							usingCoupiesRedemption();
							return true;
						}
												
						return false;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						if(url.contains("api.php/coupons/")){
							// Starts the Javascript-methods to get the coupon information from meta-data 
							coupon = new Coupon();
							listWebView.loadUrl("javascript:window.COUPON.getCouponId(document.getElementsByName('cp:coupon_id')[0].getAttribute('content'));");
							listWebView.loadUrl("javascript:window.COUPON.getClosestLocationAcceptSticker(document.getElementsByName('cp:closest_location_accepts_sticker')[0].getAttribute('content'));");
							listWebView.loadUrl("javascript:window.COUPON.getCouponAction(document.getElementsByName('cp:action')[0].getAttribute('content'));");
							listWebView.loadUrl("javascript:window.COUPON.setRemaining(document.getElementsByName('cp:remaining')[0].getAttribute('content'));");
							onDetailPage = true;
						}
						super.onPageFinished(view, url);
					}
				});
				
				/**
				 * The COUPIES-Framework will use the Internet to get lists of coupons.
				 * To use the Internet connection on Android you have to start an (background thread) Off-UI-Thread.
				 * After obtain the response from the COUPIES-Framework you have to load this data into the WebView
				 * on UI-Thread. [runOnUiThread()]
				 */
				new Thread() {
					public void run() {
						try {
							couponListHTML = getCoupiesService().getCouponFeed_html(
									getCoupiesSession(), getCoordinate());
						} catch (CoupiesServiceException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								listWebView.loadDataWithBaseURL(baseUrl, couponListHTML, MIME_TYPE, ENCODING, baseUrl);
							}
						});
					}
				}.start();
			
        }
        else {
        	String msg = "please enter your coupies API Key first. see: " +
					"de.coupies.demoapp.fragment.AbstractFragment";
    		Log.e("CoupiesDemoApp", msg);
			new AlertDialog.Builder(getActivity()).setMessage(
        	    msg).create().show();
        }
		return rootView;
    }

	private boolean checkForApiCode() {
		return getCoupiesApiKey() != null;
	}
	
	/**
	 * This method will reload the HTML-List instead of closing the activity
	 * if the back button of the device was clicked
	 */
	public void onBackPressed() {
		if(coupon!=null){
			listWebView.loadDataWithBaseURL(baseUrl, couponListHTML, MIME_TYPE, ENCODING, baseUrl);
			coupon=null;
			onDetailPage = false;
		}else{
			getActivity().finish();
		}
	}
	
	/**
	 * This method will be used if the "redeem"-button on WebView is clicked.
	 * The COUPIES-Framework is used to redeem an coupon with a COUPIES-Touchpoint
	 * or by taking a picture
	 */
	private void usingCoupiesRedemption(){
		try{
			if(coupon!=null){
				controller = RedemptionController.createInstance(getCoupiesSession(), getServiceFactory());
				controller.redeemCoupon_html(mActivity, coupon, true);
			}else{
				alert(getActivity(),"Ein Fehler ist aufgetreten", "Der ausgewaehlte Coupon konnte nicht eingeloest werden.", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();	
						listWebView.loadUrl(couponListHTML);
					}
				});
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CouponService getCoupiesService() {
		return getServiceFactory().createCouponService();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Calls the RedemptionController with the requestCode of the redemption
		controller.redeemCallback(getActivity(), requestCode, resultCode, data, listener);
    }
	
	@Override
    public void refreshView() {
		if(onDetailPage)
			listWebView.reload();
		else{
			new Thread() {
				public void run() {
					try {
						couponListHTML = getCoupiesService().getCouponFeed_html(
								getCoupiesSession(), getCoordinate());
					} catch (CoupiesServiceException e) {
						e.printStackTrace();
					}
					
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							listWebView.loadDataWithBaseURL(baseUrl, couponListHTML, MIME_TYPE, ENCODING, baseUrl);
						}
					});
				}
			}.start();		
		}
	}
}