package de.coupies.demoapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.coupies.demoapp.fragment.AbstractFragment;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.controller.redemption.RedemptionController;
import de.coupies.framework.controller.redemption.RedemptionController.RedemptionListener;

/**
 * This example works together with "NativeListTableViewController". When a coupon is clicked on in this list, we open
 * the details in this controller in a simple WebView. For a very simple example with the list and details together,
 * refer "HtmlListViewController".
 *
 *@author larseimermacher
 *
 */
public class NativeListDetailHtml extends Activity {
	
	/**
	 * This class implements the RedemptionListener.
	 * Methods of this listener will be called by the COUPIES-Framework 
	 * after successful completion of a redemption or if an error occurred.
	 */
	private class CouponRedemptionListener implements RedemptionListener {
		Activity listenerActivity;
		
		public CouponRedemptionListener(Activity activity) {
			listenerActivity = activity;
		}
		
		public void onComplete(String html) {
			/* This Method is called after the Redemption was successfully completet */
			webView.loadDataWithBaseURL(baseUrl, html, MIME_TYPE, ENCODING, baseUrl);
		}
		
		public void onComplete(Barcode barcode, int couponId) {
			// never get called
		}
		
		public void onComplete(int couponId){
			// never get called
		}

		public void onError(Exception e) {
			e.printStackTrace();
			AbstractFragment.alert(listenerActivity, "Ein Problem ist aufgetreten", e.getMessage(), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
		}

		public void onCancel() {
			System.out.println("Cancel...");
		}
		
		public void onBadStickerRead() {
			AbstractFragment.alert(listenerActivity, "Fehlerhafte Einlösung", "Der Coupon gehört nicht zu dem COUPIES-Touchpoint", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
		}

		
	}
	
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
        public void getRemaining(String mRemaining){
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

	private int actionId,
                couponId,
                remaining;
	private boolean acceptsSticker;
	
	private String baseUrl;
	
	private WebView webView;
	String couponListHTML;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity=this;
		listener = new CouponRedemptionListener(this);

		/**
		 * Get the HTML-Code from the extras 
		 */
		Bundle extras = getIntent().getExtras();
		baseUrl = AbstractFragment.getStaticServiceFactory().getAPIBaseUrl();
		webView = new WebView(this);
		
		/* Enable JavaScript */
		webView.getSettings().setJavaScriptEnabled(true);
		/* Register a new JavaScript interface called HTMLOUT */
		webView.addJavascriptInterface(new CoupiesJavaScriptInterface(), "COUPON");
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			/* Stop loading the URL if the Redemption-Button is clicked */
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.contains("redemptions/new") ){
					webView.stopLoading();	
					usingCoupiesRedemption();
					return true;
				}else if(url.contains("/redemptions/cashback/preview") ){
					webView.stopLoading();
					usingCoupiesRedemption();
					return true;
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// Starts the Javascript-methods to get coupon information from meta-data 
				coupon = new Coupon();
				webView.loadUrl("javascript:window.COUPON.getCouponId(document.getElementsByName('cp:coupon_id')[0].getAttribute('content'));");
				webView.loadUrl("javascript:window.COUPON.getClosestLocationAcceptSticker(document.getElementsByName('cp:closest_location_accepts_sticker')[0].getAttribute('content'));");
				webView.loadUrl("javascript:window.COUPON.getCouponAction(document.getElementsByName('cp:action')[0].getAttribute('content'));");
                webView.loadUrl("javascript:window.COUPON.getRemaining(document.getElementsByName('cp:remaining')[0].getAttribute('content'));");
                super.onPageFinished(view, url);
			}
		});
		
		/**
		 * Load the HTML-Code with the WebView
		 */
		webView.loadUrl(extras.getString("url"));
		setContentView(webView);
	}
	
	private void usingCoupiesRedemption(){
		try{
			if(coupon!=null){
				controller = RedemptionController.createInstance(AbstractFragment.getStaticCoupiesSession(), AbstractFragment.getStaticServiceFactory());
				controller.redeemCoupon_html(mActivity, coupon, true);
			}else{
				AbstractFragment.alert(this, "Ein Fehler ist aufgetreten", "Der ausgewählte Coupon konnte nicht eingelöst werden.", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        webView.loadUrl(couponListHTML);
                    }
                });
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Calls the RedemptionController with the requestCode of the redemption
		controller.redeemCallback(this, requestCode, resultCode, data, listener);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_item_refresh:
                webView.reload();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    /**
     * This method will go to the last website instead of closing the activity
     * if the back button of the device was clicked
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
