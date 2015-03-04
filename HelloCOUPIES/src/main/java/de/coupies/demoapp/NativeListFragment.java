package de.coupies.demoapp;

import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.coupies.demoapp.fragment.AbstractFragment;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.services.CouponService;

/**
 * This example demonstrates how to use the native list representation of the COUPIES-API (retrieve objects instead
 * of HTML-code). To keep it simple, we mix the native and HTML-representation by displaying the list of
 * coupons natively but the coupon details in a WebView. Of course everything can be implemented natively as well.
 * When a coupon is clicked in the ListView, we open the details in "NativeListDetailHtml".
 *
 *@author larseimermacher
 *
 */
public class NativeListFragment extends AbstractFragment {

	private List<Offer> offers;

	private View rootView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.native_list_fragment_layout, container, false);
		
        if(checkForApiCode()) {
//				final int limit = 50;//25;
				
				/**
				 * The COUPIES-Framework will use the Internet to get lists of coupons.
				 * To use the Internet connection on Android you have to start an (background thread) Off-UI-Thread.
				 * After obtain the response from the COUPIES-Framework you have to load the coupons into the ListView
				 * on UI-Thread. [runOnUiThread()]
				 */
				new Thread() {
					public void run() {
						try {
							offers = getCoupiesService().getCouponFeed(
									getCoupiesSession(), getCoordinate());
						} catch (CoupiesServiceException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								createListView(offers,rootView);
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

	private void createListView(List<Offer> offers, View view) {
		ListView listView= (ListView) view.findViewById(R.id.coupon_list_view);
        listView.setAdapter(new ArrayAdapter<Offer>(getActivity(), 
        		android.R.layout.simple_list_item_1, offers));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				final Offer clickedOffer = (Offer) adapter.getItemAtPosition(position);

                String couponUrl = getCoupiesService().getCouponUrl(getCoupiesSession(), getCoordinate(), clickedOffer.getId());
                Intent intent = new Intent();
                intent.putExtra("url", couponUrl);
                intent.setClass(getActivity(), NativeListDetailHtml.class);
				startActivity(intent);
			}
		});
	}

	private CouponService getCoupiesService() {
		return getServiceFactory().createCouponService();
	}
	
	@Override
    public void refreshView() {
		// No WebView to refresh
	}
}