package de.coupies.demoapp.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.coupies.demoapp.R;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.AuthentificationService;
import de.coupies.framework.utils.CoupiesWebView;

/** 
 * This is the simplest way to integrate coupons in your application: Display one or several coupons in
 * a WebView. Clicks on "redeem now" are intercepted and delegated to the COUPIES-framework to handle
 * redemtions using the COUPIES-Touchpoint. This example uses the HTML representation of the COUPIES-API only.
 * 
 * @author larseimermacher
 */
public class HtmlProfileFragment extends AbstractFragment {
		
	private static final String ENCODING = "UTF-8";
	private static final String MIME_TYPE = "text/html";
	
	private View rootView;

	private String profileHTML;
	
	private CoupiesWebView profileWebView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.profile, container, false);
		
        if(checkForApiCode()) {
        	profileWebView = (CoupiesWebView)rootView.findViewById(R.id.profileWebView);
            profileWebView.init(getActivity(), getCoupiesSession(), getServiceFactory());


        	/* Enable JavaScript */
        	profileWebView.getSettings().setJavaScriptEnabled(true);
				/**
				 * The COUPIES-Framework will use the Internet to get lists of coupons.
				 * To use the Internet connection on Android you have to start an (background thread) Off-UI-Thread.
				 * After obtain the response from the COUPIES-Framework you have to load this data into the WebView
				 * on UI-Thread. [runOnUiThread()]
				 */
				new Thread() {
					public void run() {
						try {
							profileHTML = getCoupiesService().getUserProfile_html(getCoupiesSession());
						} catch (CoupiesServiceException e) {
							e.printStackTrace();
						}
						
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								profileWebView.loadCoupiesContent(profileHTML);
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

	private AuthentificationService getCoupiesService() {
		return getServiceFactory().createAuthentificationService();
	}
	
	@Override
    public void refreshView() {
		new Thread() {
			public void run() {
				try {
					profileHTML = getCoupiesService().getUserProfile_html(getCoupiesSession());
				} catch (CoupiesServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						profileWebView.loadCoupiesContent(profileHTML);
					}
				});
			}
		}.start();		
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_refresh:
                refreshView();
                return true;
            case R.id.menu_item_open_browser:
                String url = profileWebView.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}