package de.coupies.demoapp;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.coupies.demoapp.fragment.NativeListFragment;
import de.coupies.demoapp.fragment.WebViewListFragment;
import de.coupies.demoapp.fragment.HtmlProfileFragment;

/**
 * Using this PagerAdapter is not mandatory to integrate the COUPIES-Service.
 */
public class IntegrationPagerAdapter extends FragmentPagerAdapter {
	private HtmlProfileFragment profileHTML;
	private NativeListFragment nativeList;
	private WebViewListFragment coupiesWebView;
	private List<Fragment> fragmentList;

	
    public IntegrationPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    public void preloadFragments(){
    	nativeList = new NativeListFragment();
    	profileHTML = new HtmlProfileFragment();
    	coupiesWebView = new WebViewListFragment();
    	
    	fragmentList = new ArrayList<Fragment>();
        fragmentList.add(coupiesWebView);
    	fragmentList.add(nativeList);
    	fragmentList.add(profileHTML);
    }
    
    public Fragment getFragmentFromViewPager(int position){
    	return fragmentList.get(position);
    }
    
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                if(coupiesWebView==null){
                    coupiesWebView = new WebViewListFragment();
                    coupiesWebView.setRetainInstance(true);
                }
                return coupiesWebView;
            case 1:
            	if(nativeList==null){
            		nativeList = new NativeListFragment();
            		nativeList.setRetainInstance(true);
            	}
                return nativeList;
            case 2:
            	if(profileHTML==null){
            		profileHTML = new HtmlProfileFragment();
            		profileHTML.setRetainInstance(true);
            	}
                return profileHTML;
            default:
            	return new WebViewListFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
            	return "HTML List";
            case 1:
            	return "Native List";
            case 2:
            	return "Profile";
            default:
            	return "Coupons";
        }

    }
}
