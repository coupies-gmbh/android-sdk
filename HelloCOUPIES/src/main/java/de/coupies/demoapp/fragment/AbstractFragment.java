package de.coupies.demoapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import de.coupies.demoapp.R;
import de.coupies.framework.CoupiesManager;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.ServiceFactory;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoordinateImpl;
import de.coupies.framework.session.CoupiesSession;

/**
 * abstract base Fragment
 * 
 * @author lars.eimermacher
 *
 */
public abstract class AbstractFragment extends Fragment {

	private static String PARTNERS_USER_TOKEN = null;
	private static final String API_KEY = ""; // TODO: please insert your COUPIES API Key here
	private static final String API_LEVEL = "4";
	
	private static final float LONGITUDE = 6.958237F;
	private static final float LATITUDE = 50.937056F;
	private static final Coordinate COORDINATE = new CoordinateImpl(LATITUDE, LONGITUDE);	
	private static final int DEFAULT_RADIUS = 1000000;
	 
	public static final String PREF_UUID = "my_unique_id";
	public static final String ANDROID_AID = "android_aid";

	private ServiceFactory serviceFactory;
	private static ServiceFactory staticServiceFactory;
	private CoupiesSession coupiesSession;
	private static CoupiesSession staticCoupiesSession;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    /*
	     * Use this method to add the callback onOptionItemSelected to the Fragments 
	     */
	    setHasOptionsMenu(true);
	}
	
	protected String getCoupiesApiKey() {
		return API_KEY;
	}
	
    /*
    Set this parameter to any unique user ID (e.g. a hashed version of your internal user ID) or leave null.
    If you do not set this parameter, COUPIES will generate a unique user ID automatically.
    If you experience an error when redeeming a coupon, please contact felix.schul@coupies.de to check the backend settings.
    */
	protected String getPartnersUserToken() {
		
		if (PARTNERS_USER_TOKEN == null) {
			//Example of use: randomly generated UUID, persisted in SharedPreferences
			/*
			SharedPreferences sharedPrefs = this.getSharedPreferences(PREF_UUID, Context.MODE_PRIVATE);
			PARTNERS_USER_TOKEN = sharedPrefs.getString(PREF_UUID, null);
	        if (PARTNERS_USER_TOKEN == null) {
	        	PARTNERS_USER_TOKEN = UUID.randomUUID().toString();
	            Editor editor = sharedPrefs.edit();
	            editor.putString(PREF_UUID, PARTNERS_USER_TOKEN);
	            editor.commit();
	        }
	        */
		}
		return PARTNERS_USER_TOKEN;
	}

	protected ServiceFactory getServiceFactory() {
		if(serviceFactory == null) {
			//Access the sandbox environment. Use createLiveServiceFactory for live environment
			serviceFactory = CoupiesManager.createTestServiceFactory(getActivity(), getCoupiesApiKey(), API_LEVEL); //sandbox
			//serviceFactory = CoupiesManager.createLiveServiceFactory(getActivity(), getCoupiesApiKey(), API_LEVEL); //production
		}
		staticServiceFactory = serviceFactory;
		return serviceFactory;
	}
	
	public static ServiceFactory getStaticServiceFactory(){
		return staticServiceFactory;
	}
	
	public static CoupiesSession getStaticCoupiesSession(){
		return staticCoupiesSession;
	}
	
	public static Coordinate getStaticCoordinate() {
		return COORDINATE;
	}
	
	protected Coordinate getCoordinate() {
		return COORDINATE;
	}
	
	protected CoupiesSession getCoupiesSession() {
		if(coupiesSession == null) {
			try {
				coupiesSession = getServiceFactory().createAuthentificationService()
						.createPartnerSession(getPartnersUserToken());
			} catch (CoupiesServiceException e) {
				Log.e("COUPIES_SESSION", "getCoupiesSession", e);
				coupiesSession = null;
			}
		}
		staticCoupiesSession = coupiesSession;
		return coupiesSession;
	}
	
	public static void alert(Activity activity,final String inTitle, final String inText, 
			final OnClickListener positiveListener) {
		final SpannableString ss = new SpannableString(inText);
	    Linkify.addLinks(ss, Linkify.ALL);
		
		Builder builder = new AlertDialog.Builder(activity)
				.setPositiveButton(activity.getResources().getString(R.string.okButtonText), positiveListener)
				.setTitle(inTitle)
				.setMessage(ss);
		AlertDialog d = builder.create();
		d.show();
		// Make the textview clickable. Must be called after show()
	    ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public void redirect(Class<? extends Activity> target, Bundle bundle, int flags) {
		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (flags != -1) {
			intent.setFlags(flags);
		}
		intent.setClass(getActivity(), target);
		startActivity(intent);
	}
	
	public int getRadius() {
		return DEFAULT_RADIUS;
	}
	
	public abstract void refreshView();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_item_refresh:
	        	refreshView();
	            return true;
	        case R.id.menu_item_open_browser:
	        	System.out.println("URL im Browser Öffnen");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}