package de.coupies.framwork.fragments;

import de.coupies.coupies_framework_lib.R;
import de.coupies.framework.services.async.tasks.BackgroundTask;
import de.coupies.framework.services.async.tasks.Executable.BackgroundProcess;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class CashbackHelpFragmentPageOne extends Fragment {
    Bitmap bonImage;
    ImageView howToImageView;
	
    /**
     * Create a new instance of CashbackHelpFragmentPageOne
     */
    static CashbackHelpFragmentPageOne newInstance() {
        return new CashbackHelpFragmentPageOne();
    }

    /**
     * When creating, loading the images to display
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * The Fragment's UI is just a simple image and some text
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cashback_help_page_one_layout, container, false);
        howToImageView = (ImageView)v.findViewById(R.id.cashback_redemption_help);
        
        v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(container instanceof ViewPager){
					((ViewPager)container).setCurrentItem(1, true);
				}
			}
		});
        
        BackgroundTask loadImageTask= new BackgroundTask(new BackgroundProcess() {
			
			@Override
			public void run() throws Exception {
				// loading btimap before View is created
		        bonImage = BitmapFactory.decodeResource(getResources(), R.drawable.cashback_redemption_overlay_one);
			}
			
			@Override
			public void runOnUiThread() {
				if(howToImageView != null && bonImage != null){
		        	howToImageView.setImageBitmap(bonImage);
					AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
					fadeInAnimation.setDuration(400);
					fadeInAnimation.setFillAfter(true);
					howToImageView.startAnimation(fadeInAnimation);
				}else{
					Log.d(getTag(), "Image loading still in progress");
				}
			}
			
			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
		}, null, false);
        
        loadImageTask.execute();
        
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

