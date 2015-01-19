package de.coupies.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.controller.redemption.RedemptionActivity;
import de.coupies.framework.controller.redemption.RedemptionController;
import de.coupies.framework.controller.redemption.RedemptionController.RedemptionListener;
import de.coupies.framework.services.ServiceFactory;
import de.coupies.framework.session.CoupiesSession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.util.AttributeSet;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class CoupiesWebView extends WebView{

    private static final String ENCODING = "UTF-8",
                                MIME_TYPE = "text/html";

    private String  baseUrl,
                    htmlListContent,
                    successFunction,
                    errorFunction;

    private ServiceFactory coupiesServiceFactory;
    private CoupiesSession coupiesSession;
    private static RedemptionController controller;
    private Coupon coupon;
    private Activity mActivity;

    private int actionId,
                couponId,
                remaining;

    private boolean acceptsSticker,
                    base64ToJavaScript = false,
                    onDetailPage = false;


    public CoupiesWebView(Context context) {
        super(context);
    }

    public CoupiesWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Activity activity, CoupiesSession session, ServiceFactory ServiceFactory){
        this.coupiesServiceFactory = ServiceFactory;
        this.coupiesSession = session;
        this.baseUrl = coupiesServiceFactory.getAPIBaseUrl();
        this.mActivity = activity;

		/* Enable JavaScript */
        getSettings().setJavaScriptEnabled(true);
//		/* Enable ZoomControls */
//		getSettings().setBuiltInZoomControls(true);
		/* Enable WideViewPort */
//		getSettings().setUseWideViewPort(true);
		/* Register a new JavaScript interface called HTMLOUT */
        addJavascriptInterface(new CoupiesJavaScriptInterface(), "COUPIES");

        setWebViewClient(new WebViewClient() {
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
                }else if(url.startsWith("market://")){
                    Intent goToMarket = null;
                    goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mActivity.startActivity(goToMarket);

                    return true;
                }else if(url.contains("play.google.com/store")){
                    Intent goToMarket = null;
                    goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mActivity.startActivity(goToMarket);

                    return true;
                }else if(url.contains("redemptions/new") ){
                    stopLoading();
                    usingCoupiesRedemption();
                    return true;
                }else if(url.contains("action/touchpoint")){
                    coupon = new Coupon();
                    coupon.setAction(1);
                    coupon.setClosestLocationAcceptsSticker(true);
                    // vorerst noch notwendig
                    coupon.setId(0);

                    setSuccessAndErrorFunctions(url);
                    stopLoading();
                    usingCoupiesRedemption();
                    return true;
                }else if(url.contains("action/camera")){
                    //test coupon for camera redemption
                    coupon = new Coupon();
                    coupon.setAction(3);
                    coupon.setRemaining(1);
                    // vorerst noch notwendig
                    coupon.setId(0);

                    setSuccessAndErrorFunctions(url);
                    stopLoading();
                    usingCoupiesRedemption();
                    return true;
                }else if(url.contains("action/share")){
                    // Open an action.share Intent
                    int startIndexShareText = url.indexOf("?shareText=")+11;
                    int endIndexShareText = url.indexOf("&", startIndexShareText);
                    String shareText = null;

                    int startIndexShareUrl = url.indexOf("&shareUrl=")+10;
                    int endIndexShareUrl = url.indexOf("&", startIndexShareUrl);
                    if(endIndexShareUrl == -1){
                        // if shareUrl is the last parameter
                        endIndexShareUrl = url.length();
                    }
                    String shareUrl = null;

                    try {
                        shareText = URLDecoder.decode(url.substring(startIndexShareText, endIndexShareText), "UTF-8");
                        shareUrl = URLDecoder.decode(url.substring(startIndexShareUrl, endIndexShareUrl), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

//					int startIndexShareImage = url.indexOf("&shareImage=")+12;
//					int endIndexShareImage = url.indexOf("&", startIndexShareImage);
//					if(endIndexShareImage == -1){
//						// if shareImage is the last parameter
//						endIndexShareImage = url.length();
//					}
//					@SuppressWarnings("unused")
//					String shareImageUrl = url.substring(startIndexShareImage, endIndexShareImage);
//
//
//					String shareUrl = url.substring(startIndexShareUrl, endIndexShareUrl);

                    Intent shareLink = getDefaultIntent(shareText+"\n"+shareUrl);
                    mActivity.startActivity(shareLink);

                    stopLoading();
                    return true;
                }else if(url.contains("/redemptions/cashback/preview") ){
                    stopLoading();
                    usingCoupiesRedemption();
                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if(url.contains(baseUrl+"/coupons/")){
                    // Starts the Javascript-methods to get the coupon information from meta-data
                    coupon = new Coupon();
                    loadUrl("javascript:window.COUPIES.setCouponId(document.getElementsByName('cp:coupon_id')[0].getAttribute('content'));");
                    loadUrl("javascript:window.COUPIES.setClosestLocationAcceptSticker(document.getElementsByName('cp:closest_location_accepts_sticker')[0].getAttribute('content'));");
                    loadUrl("javascript:window.COUPIES.setCouponAction(document.getElementsByName('cp:action')[0].getAttribute('content'));");
                    loadUrl("javascript:window.COUPIES.setRemaining(document.getElementsByName('cp:remaining')[0].getAttribute('content'));");
                    onDetailPage = true;
                }

                super.onPageFinished(view, url);
            }

        });
//
//		setWebChromeClient(new WebChromeClient(){
//			public boolean onConsoleMessage(ConsoleMessage cm)
//		    {
//		        Log.d("ShowMote", cm.message() + " -- From line "
//		                             + cm.lineNumber() + " of "
//		                             + cm.sourceId() );
//		        return true;
//		    }
//		 });

    }

    public void setSuccessAndErrorFunctions(String url){
        base64ToJavaScript = true;
        int startIndexOnSuccess = url.indexOf("?onSuccess=")+11;
        int endIndexOnSuccess = url.indexOf("&", startIndexOnSuccess);
        successFunction = url.substring(startIndexOnSuccess, endIndexOnSuccess);
        int startIndexOnError = url.indexOf("&onError=")+9;
        int endIndexOnError = url.indexOf("&", startIndexOnError);
        if(endIndexOnError == -1){
            // if onError is the last parameter
            endIndexOnError = url.length();
        }
        errorFunction = url.substring(startIndexOnError, endIndexOnError);
    }

    public void loadCoupiesContent(String htmlContent){
        if(htmlListContent == null)
            this.htmlListContent = htmlContent;
        loadDataWithBaseURL(baseUrl, htmlContent, MIME_TYPE, ENCODING, baseUrl);
    }

    public void handleRedemeCallback(int requestCode,int resultCode,Intent data){
        if(base64ToJavaScript && resultCode == RedemptionActivity.REQUEST_REDEEM_CASHBACK){
            Bundle extras = data.getExtras();
            File image = (File)extras.getSerializable("image");

            try{
                String base64ImageString = decodeFileToBase64(image);
                loadUrl("javascript:"+successFunction+"('"+base64ImageString+"')");
            }catch(IOException e){
                loadUrl("javascript:"+errorFunction+"('"+e.getMessage()+"')");
            }

        }else if(base64ToJavaScript && resultCode == RedemptionActivity.RESULT_STICKER_READ){
            Bundle extras = data.getExtras();
            String stickerCode = (String) extras.getSerializable("stickerCode");

            loadUrl("javascript:"+successFunction+"('"+stickerCode+"')");
        }else if(base64ToJavaScript && resultCode == RedemptionActivity.RESULT_NO_STICKER){
            Bundle extras = data.getExtras();
            String stickerCode = (String) extras.getSerializable("stickerCode");

            loadUrl("javascript:"+successFunction+"('"+stickerCode+"')");

        }else if(base64ToJavaScript && resultCode == RedemptionActivity.RESULT_CONNECTION_ERROR){
            if(data.getExtras().getSerializable("exception") instanceof Exception)
                loadUrl("javascript:"+errorFunction+"('"+((Exception)data.getExtras().getSerializable("exception")).getMessage()+"')");
            else
                loadUrl("javascript:"+errorFunction+"('"+data.getExtras().getString("exception")+"')");
        }else{
            controller.redeemCallback(mActivity, requestCode, resultCode, data, new CouponRedemptionListener());
        }
    }

    public boolean isDetailView(){
        return onDetailPage;
    }

    public void clearHtmlListContent(){
        htmlListContent = null;
    }

    private String decodeFileToBase64(File file) throws IOException{
        InputStream inputStream = new FileInputStream(file.getAbsolutePath());//You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;
    }

    /**
     * This method will be used if the "redeem"-button on WebView is clicked.
     * The COUPIES-Framework is used to redeem an coupon with a COUPIES-Touchpoint
     * or by taking a picture
     */
    private void usingCoupiesRedemption(){
        try{
            if(coupon!=null){
                controller = RedemptionController.createInstance(coupiesSession, coupiesServiceFactory);
                controller.redeemCoupon_html(mActivity, coupon, true);
            }else{
                new AlertDialog.Builder(mActivity).setTitle("Ein Fehler ist aufgetreten").setMessage(
                        "Der ausgewählte Coupon konnte nicht eingelöst werden.").create().show();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is an JavaScript-Interface for the WebView.
     * With this interface we are able to read  information
     * about the coupon being shown in the WebView
     */
    class CoupiesJavaScriptInterface{
        @JavascriptInterface
        public void setCouponId(String id){
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
        public void setCouponAction(String action){
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
        public void setClosestLocationAcceptSticker(String mAcceptSticker){
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

    /**
     * This class implements the RedemptionListener.
     * Methods of this listener will be called by the COUPIES-Framework
     * after completed an redemption or if an error occurred.
     */
    private class CouponRedemptionListener implements RedemptionListener {

        public CouponRedemptionListener() {
        }

        public void onComplete(String html) {
			/* This method is called after the redemption was successfully completed */
            loadCoupiesContent(html);
        }

        public void onComplete(Barcode barcode, int couponid) {
            // never gets called
        }

        public void onComplete(int couponId){
            // never gets called
        }

        public void onError(Exception e) {
            new AlertDialog.Builder(mActivity).setTitle("Ein Problem ist aufgetreten")
                    .setMessage(e.getMessage()).create().show();
        }

        public void onCancel() {
            // nothing to do
        }

        public void onBadStickerRead() {
            new AlertDialog.Builder(mActivity).setTitle("Fehlerhafte Einlösung")
                    .setMessage("Der Coupon gehört nicht zu dem Coupies-Touchpoint").create().show();
        }
    }

    private Intent getDefaultIntent(String shareText) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(mActivity)
                .setText(shareText)
                .setType("text/plain")
                .getIntent();

        return shareIntent;
    }

    /**
     * This method will reload the HTML-List instead of closing the activity
     * if the back button of the device was clicked
     */
    public boolean onBackPressed() {
        if(coupon!=null && onDetailPage){
            loadCoupiesContent(htmlListContent);
            coupon=null;
            onDetailPage = false;
            return false;
        }else{
            return true;
        }
    }

}
