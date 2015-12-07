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

    private View rootView;

    private CoupiesWebView profileWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile, container, false);
        profileWebView = (CoupiesWebView) rootView.findViewById(R.id.profileWebView);


        if(!(getCoupiesApiKey().equals(""))) {
            profileWebView.init(getActivity(), getCoupiesSession(), getServiceFactory());


        	/* Enable JavaScript */
            profileWebView.getSettings().setJavaScriptEnabled(true);
            profileWebView.loadUrl(getCoupiesService().getUserProfileUrl(getCoupiesSession()));
            /**
             * The COUPIES-Framework will use the Internet to get lists of coupons.
             * To use the Internet connection on Android you have to start an (background thread) Off-UI-Thread.
             * After obtain the response from the COUPIES-Framework you have to load this data into the WebView
             * on UI-Thread. [runOnUiThread()]
             */
        } else {
            String msg = "please enter your coupies API Key first. see: " +
                    "de.coupies.demoapp.fragment.AbstractFragment";
            Log.e("CoupiesDemoApp", msg);
            TextView textView = (TextView) rootView.findViewById(R.id.errorMsg);
            textView.setVisibility(View.VISIBLE);
            profileWebView.setVisibility(View.GONE);


        }
        return rootView;
    }


    private AuthentificationService getCoupiesService() {
        return getServiceFactory().createAuthentificationService();
    }

    @Override
    public void refreshView() {
        profileWebView.reload();
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

    public void onBackPressed() {
        if (profileWebView.canGoBack()) {
            profileWebView.goBack();
        } else {
            getActivity().finish();
        }
    }

}