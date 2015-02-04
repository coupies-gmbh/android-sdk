package de.coupies.demoapp;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.coupies.demoapp.fragment.CoupiesWebViewFragment;
import de.coupies.demoapp.fragment.HtmlListFragment;
import de.coupies.demoapp.fragment.HtmlProfileFragment;

/**
 * Using this PagerAdapter is not mandatory to integrate the COUPIES-Service.
 */
public class IntegrationPagerAdapter extends FragmentPagerAdapter {
	private HtmlListFragment listHTML;
	private HtmlProfileFragment profileHTML;
	private NativeListFragment nativeList;
	private CoupiesWebViewFragment coupiesWebView;
	private List<Fragment> fragmentList;

	
    public IntegrationPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    public void preloadFragments(){
    	listHTML = new HtmlListFragment();
    	nativeList = new NativeListFragment();
    	profileHTML = new HtmlProfileFragment();
    	coupiesWebView = new CoupiesWebViewFragment();
    	
    	fragmentList = new ArrayList<Fragment>();
    	fragmentList.add(listHTML);
    	fragmentList.add(nativeList);
    	fragmentList.add(profileHTML);
    	fragmentList.add(coupiesWebView);
    }
    
    public Fragment getFragmentFromViewPager(int position){
    	return fragmentList.get(position);
    }
    
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
            	if(listHTML==null){
            		listHTML = new HtmlListFragment();
            		listHTML.setRetainInstance(true);
            	}
                return listHTML;
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
            case 3:
            	if(coupiesWebView==null){
            		coupiesWebView = new CoupiesWebViewFragment();
            		coupiesWebView.setRetainInstance(true);
            	}
                return coupiesWebView;
            default:
            	return new HtmlListFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
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
            case 3:
            	return "CoupiesWebView";
            default:
            	return "Coupons";
        }

    }
}
