package de.coupies.demoapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.coupies.demoapp.R;
import de.coupies.framework.services.CouponService;
import de.coupies.framework.utils.CoupiesWebView;

/** 
 * This is the simplest way to integrate coupons in your application: Display one or several coupons in
 * a WebView. Clicks on "redeem now" are intercepted and delegated to the COUPIES-framework to handle
 * redemtions using the COUPIES-Touchpoint. This example uses the HTML representation of the COUPIES-API only.
 * 
 * @author larseimermacher
 */
public class WebViewListFragment extends AbstractFragment {

	private View rootView;

	private CoupiesWebView coupiesWebView;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.coupies_web_view, container, false);
		coupiesWebView = (CoupiesWebView)rootView.findViewById(R.id.coupiesWebView);

		if(!(getCoupiesApiKey().equals(""))) {
			coupiesWebView.init(getActivity(), getCoupiesSession(), getServiceFactory());
	        coupiesWebView.loadUrl(getCoupiesService().getCouponFeedUrl(getCoupiesSession(), getCoordinate()));

		} else {
			String msg = "please enter your coupies API Key first. see: " +
					"de.coupies.demoapp.fragment.AbstractFragment";
			Log.e("CoupiesDemoApp", msg);
			TextView textView = (TextView) rootView.findViewById(R.id.errorMsg);
			textView.setVisibility(View.VISIBLE);
			coupiesWebView.setVisibility(View.GONE);

		}
		
		return rootView;
    }


	/**
	 * This method will go to the last website instead of closing the activity
	 * if the back button of the device was clicked
	 */
	public void onBackPressed() {
        if (coupiesWebView.canGoBack()) {
            coupiesWebView.goBack();
        } else {
            getActivity().finish();
        }
	}
	
	private CouponService getCoupiesService() {
		return getServiceFactory().createCouponService();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Calls the RedemptionController with the requestCode of the redemption
		coupiesWebView.handleRedemeCallback(requestCode, resultCode, data);
    }

	/**
	 * This method will reload the Current HTML after using the "refresh" button in menu
	 * If you don't want this feature you can delete the following code
	 */
	@Override
    public void refreshView() {
        coupiesWebView.reload();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_item_refresh:
	        	refreshView();
	            return true;
	        case R.id.menu_item_open_browser:
	        	String url = coupiesWebView.getUrl();
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