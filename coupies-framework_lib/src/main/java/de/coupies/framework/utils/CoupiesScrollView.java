package de.coupies.framework.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CoupiesScrollView extends ScrollView {
	private boolean scrollEnabled = true;
	
    public CoupiesScrollView(Context context) {
        super(context);
    }

    public CoupiesScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setScrollingEnabled(boolean enabled) {
    	scrollEnabled = enabled;
    }

    public boolean isScrollable() {
        return scrollEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (scrollEnabled) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return scrollEnabled; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if 
        // we are not scrollable
        if (!scrollEnabled) return false;
        else return super.onInterceptTouchEvent(ev);
    }
}
