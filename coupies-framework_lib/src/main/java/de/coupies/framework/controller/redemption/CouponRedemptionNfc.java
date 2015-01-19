package de.coupies.framework.controller.redemption;

import java.util.List;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.google.zxing.client.android.BeepManager;
import de.coupies.coupies_framework_lib.R;

public class CouponRedemptionNfc extends AbstractRedemptionActivity {
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private BeepManager beepManager;

	private Button errorButton;
    static final String TAG = "ViewTag";
    static final int ACTIVITY_TIMEOUT_MS = 1 * 1000;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Intent nfcIntent = getIntent();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction())){
			resolveIntent(nfcIntent);
		}

		setContentView(R.layout.coupon_redemption_nfc);
		
		errorButton = (Button) findViewById(R.id.coupon_redemption_error);
		errorButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showNoStickerDialog();
			}
		});
		
		beepManager = new BeepManager(this);
		
	    //NFC
	    mAdapter = NfcAdapter.getDefaultAdapter(this);
	    /*mPendingIntent = PendingIntent.getActivity(this, 0,
	            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);*/
	    
	    //Geändert um auch mit der mWallet NFC-einlösungen machen zu können! 
	    mPendingIntent = this.createPendingResult(WALLET_NFC_RESULT, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	    
	    // Setup an intent filter for all MIME based dispatches
	    IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	    try {
	        ndef.addDataType("*/*");
	    } catch (MalformedMimeTypeException e) {
	        throw new RuntimeException("fail", e);
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@Override
	protected void onResume() {
		super.onResume();
		beepManager.updatePrefs();
		//NFC
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
	}		
	
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
        //throw new RuntimeException("onPause not implemented to fix build");
    }

	
    void resolveIntent(Intent intent) {
        // Parse the intent
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            // When a tag is discovered we send it to the service to be save. We
            // include a PendingIntent for the service to call back onto. This
            // will cause this activity to be restarted with onNewIntent(). At
            // that time we read it from the database and view it.
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            // Setup the views
            processTag(msgs);
        } else {
            Log.e(TAG, "Unknown intent " + intent);
            finish();
            return;
        }
    }

    void processTag(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        if (size > 0) {
            ParsedNdefRecord record = records.get(0);
            stickerCode = record.getUri().toString();
            
    		if (stickerCode != null && stickerCode.contains("coupies.de")) {
    			int lastSlash = stickerCode.lastIndexOf("/");
    			if (lastSlash > 0 && lastSlash < stickerCode.length() - 1) {
    				stickerCode = stickerCode.substring(lastSlash + 1);
            		finishWithSticker();
    			}			
    		}
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode == WALLET_NFC_RESULT){
    		resolveIntent(data);
    	}
    	
    }
}	