package de.coupies.framework.controller.redemption;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.coupies.coupies_framework_lib.R;

/**
 * @author thomas.volk@denkwerk.com
 * @since 24.08.2010
 * 
 */
public class CouponRedemptionContextView extends LinearLayout {
	private Button hintButton;
	private Button errorButton;
	private ImageView helpView;
	private TextView helpText;
	private AbstractRedemptionActivity activity;

	public CouponRedemptionContextView(Context context) {
		super(context);
		setLayout(context);
	}

	public CouponRedemptionContextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayout(context);
	}
    
	private void setLayout(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    inflater.inflate(R.layout.coupon_redemption_context, this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();


		}

	public boolean isHelpVisible(){
		if (helpView.getVisibility()==GONE) {
			return false;			
		}
		return true;
	}
	
	public void setHelpVisibility(boolean showHelp) {
		if (showHelp) {
			helpView.setVisibility(VISIBLE);
			helpText.setVisibility(VISIBLE);			
		} else {
			helpView.setVisibility(GONE);
			helpText.setVisibility(GONE);
			
		}

	}

	public void init(AbstractRedemptionActivity inAbstractActivity) {
		this.activity = inAbstractActivity;
		hintButton = (Button) findViewById(R.id.coupon_redemption_hint);
		errorButton = (Button) findViewById(R.id.coupon_redemption_error);
		helpText = (TextView) findViewById(R.id.coupon_redemption_help_text);
		helpView = (ImageView) findViewById(R.id.coupon_redemption_help);
		hintButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isHelpVisible()) {
					setHelpVisibility(false);
				} else {
					setHelpVisibility(true);
				}
			}
		});
		errorButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CouponRedemptionContextView.this.activity.showNoStickerDialog();
			}
		});
	}
}

