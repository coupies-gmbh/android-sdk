package de.coupies.framework.controller.redemption;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.coupies.coupies_framework_lib.R;
import de.coupies.framwork.fragments.CashbackHelpFragmentAdapter;

/**
 * @author larseimermacher
 * @since 15.02.2013
 * 
 */
public class CashbackRedemptionContextView extends Fragment implements ViewPager.OnPageChangeListener{
	
	ViewPager mViewPager;
	
	CashbackRedemption callingActivity;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * The Fragment's UI is just a viewpager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cashback_redemption_help_layout, container, false);
        
        if(callingActivity == null){
			callingActivity = (CashbackRedemption) getActivity();
		}
        
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(new CashbackHelpFragmentAdapter(getFragmentManager(),callingActivity));
        mViewPager.setOnPageChangeListener(this);
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			if(callingActivity!=null)
				callingActivity.showCameraBorder();
			break;
		case 1:
			if(callingActivity!=null)
				callingActivity.showValidationBorder();
			break;
		default:
			break;
		}
	}
	
	public void refreshViewpager(){
		if(mViewPager!=null)
			mViewPager.setCurrentItem(0, false);
	}
}

