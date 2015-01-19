package de.coupies.framwork.fragments;

import de.coupies.framework.controller.redemption.CashbackRedemption;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CashbackHelpFragmentAdapter extends FragmentPagerAdapter {
    Fragment 	pageOneFragment,
    			pageTwoFragment;
    
    CashbackRedemption callingActivity;
    
	public CashbackHelpFragmentAdapter(FragmentManager fm) {
		super(fm);
    }
	
	public CashbackHelpFragmentAdapter(FragmentManager fm, CashbackRedemption callingActivity) {
		super(fm);
		this.callingActivity = callingActivity;
    }
	
	public CashbackRedemption getCashbackRedemptionActivity(){
		return callingActivity;
	}

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
    	switch (position) {
		case 0:
			if(pageOneFragment==null){
				pageOneFragment = CashbackHelpFragmentPageOne.newInstance();
			}
			return pageOneFragment;
		case 1:
			if(pageTwoFragment==null){
				pageTwoFragment = CashbackHelpFragmentPageTwo.newInstance();
			}
			return pageTwoFragment;
		default:
			if(pageOneFragment==null){
				pageOneFragment = CashbackHelpFragmentPageOne.newInstance();
			}
			return pageOneFragment;
		}
    }
}
