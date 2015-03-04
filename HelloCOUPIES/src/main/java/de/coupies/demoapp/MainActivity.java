package de.coupies.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;

import de.coupies.demoapp.fragment.CoupiesWebViewFragment;
import de.coupies.demoapp.fragment.HtmlListFragment;
import de.coupies.demoapp.fragment.HtmlProfileFragment;

/**
 * This is a simple demo-application that shows how to use the COUPIES-framework and integrate
 * coupons into your application.
 * For the easiest and fastest integration, see CoupiesHtmlListFragment.
 * In case of questions please contact felix.schul@coupies.de
 *
 * @author larseimermacher
 *
 */
public class MainActivity extends ActionBarActivity{

    IntegrationPagerAdapter integrationPagerAdapter;
    ViewPager mViewPager;

    // Set up the action bar.
    protected ActionBar actionBar;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
        
    	((PagerTabStrip)findViewById(R.id.pager_tab_strip)).setTabIndicatorColorResource(R.color.coupies_tabindicator);
    	
        // Create the adapter that will return a fragment for each of the two possible integrations
    	integrationPagerAdapter = new IntegrationPagerAdapter(getSupportFragmentManager());
    	integrationPagerAdapter.preloadFragments();
  
        // This ViewPager holds the two Fragments whitch shows the possible integrations
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(integrationPagerAdapter);

        // show ActionBar to show menuitems for refresh
        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
    }
    
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	}
	
	/*
	 * Overriding this method is not necessary if you integrate one of the two possible solutions.
	 * This method will only start the onBackPress-method of HmtlListFragment
	 */
	@Override
	public void onBackPressed() {
		Fragment fragment = integrationPagerAdapter.getFragmentFromViewPager(mViewPager.getCurrentItem());
        if(fragment != null && (fragment instanceof HtmlListFragment)){
           ((HtmlListFragment)fragment).onBackPressed();
        }else if(fragment != null && (fragment instanceof CoupiesWebViewFragment)) {
            ((CoupiesWebViewFragment) fragment).onBackPressed();
        }else if(fragment != null && (fragment instanceof HtmlProfileFragment)) {
            ((HtmlProfileFragment) fragment).onBackPressed();
        }
        else{
        	super.onBackPressed();
        }
	}
	
	/*
	 * Overriding this method is not necessary if you integrate one of the two possible solutions. 
	 * This method will only start the onActivityResult-method of the Fragments
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Fragment fragment = integrationPagerAdapter.getFragmentFromViewPager(mViewPager.getCurrentItem());
	      if(fragment != null){
	            fragment.onActivityResult(arg0, arg1, arg2);
	      }
		super.onActivityResult(arg0, arg1, arg2);
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
}
