package de.coupies.framework.controller.redemption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.coupies.coupies_framework_lib.R;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.utils.CoupiesScrollView;

@SuppressWarnings("deprecation")
public final class CashbackRedemption extends AbstractRedemptionActivity implements SensorEventListener{
	
	private enum CameraState {
		  C_STATE_FOCUS,
		  C_STATE_PREVIEW,
		  C_STATE_PICTURE,
		  C_STATE_VALIDATE,
		  C_STATE_PICTURE_IN_PROGRESS;
	}
	
	private enum FlashState {
		  C_STATE_FLASH_ON,
		  C_STATE_FLASH_OFF,
		  C_STATE_FLASH_AUTO,
		  C_STATE_FLASH_TORCH;;
	}
	
	private CameraState mCameraState;
	private FlashState mFlashState;
	
	private static final String TAG = CashbackRedemption.class.getSimpleName();
  
	private Camera mCamera=null;

	private CameraPreview 	mPreview;
	
	private CashbackRedemption myCashbackRedemption;
	private Camera.Parameters mCameraParams;
	
	private ImageButton flashButton,
						triggerButton;

	private SensorManager mSensorManager;
	private Sensor mAccel;
  
	private float 	mLastX = 0,
					mLastY = 0,
					mLastZ = 0,
					previewAlpha;
  
	private Button	helpButton,
					redemptionNumberPositive,
					redemptionNumberNegative,
					sendRedemption,
					addReceiptPictureButton,
					validation_ok,
					validation_again;
	
	private EditText redemptionNumbers;
  
	private FrameLayout preview,
						contextView;
  
	private ImageView 	border_view_top,
						border_view_left,
						border_view_right,
						help_image;
	
	TextView 	helpTextViewTitle,
				helpTextViewText;
	
	private List<ImageView> cropedPreviews;
	
	private CoupiesScrollView receipt_preview_layout;
	
	private RelativeLayout 	validation_layout,
							redemption_Layout,
  							redemptionNumberLayout,
  							complete_validation_layout;
	
  	Fragment redemptionHint;
	
	private Bitmap 	bMap,
					previewSampleBitmap;
  
	private File newPictureFile = null;
	
	private List<File> pictureFiles = null;

	private boolean	  	readyForAutofocus = false,
						hasFlashlight = false,
						mInitialized = false,
						showPicturesAfterBackPressed=false,
						showHintOnCreateCamera = true;
  
	public final static int MEDIA_TYPE_IMAGE = 1;
  
	private int numberArticles = 1,
				picturesTaken = 0,
				displayWidth,
				displayHeight;
	
	protected Size 	bestPreviewSize,
					bestPictureSize;
	
	private float 	currentFade;
  
	protected DisplayMetrics displaymetrics;
	
	private Handler smoothScrollHandler,
					smoothFadeHandler;
	private long mStartTime;
	
	private int scrolled,
				scrollTo,
				animationDpiFactor;

/**
 * Start implementing system callback methods 
 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	      
	    displaymetrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	    displayWidth = displaymetrics.widthPixels;
	    displayHeight = displaymetrics.heightPixels;
	    
	    switch (displaymetrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			animationDpiFactor = 1;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			animationDpiFactor = 1;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			animationDpiFactor = 2;
			break;
		default:
			animationDpiFactor = 3;
			break;
		}
	    smoothScrollHandler = new Handler();
	    smoothFadeHandler = new Handler();
	    
	    pictureFiles = new ArrayList<File>();
	      
	    myCashbackRedemption = this;   
	    
	    // Keep screen on until activity finished
	    Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
	    // Create our Preview view and set it as the content of our activity.
	    setContentView(R.layout.cashback_redemption_camera);
	    
		createLayout();
	}
	
	@Override
	protected void onResume() {
		if(mCamera==null){
			new CameraOpeningBackgroundTask().execute();
		}else{
			try{
				showCamera(false);
			}catch (Exception e) {
				e.printStackTrace();
				showError(getResources().getString(R.string.cashback_image_send_error_text_1));
			}
		}
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		if(mCamera != null && mCameraState.equals(CameraState.C_STATE_PICTURE) && !validation_ok.isEnabled()){
			// BackgroundCroppingTask is running
			return;
		}else if(mCamera != null && contextView.isShown()){
			// set current item of viewpager to 0 
			if(redemptionHint!=null)
				((CashbackRedemptionContextView)redemptionHint).refreshViewpager();
			if(picturesTaken>=1)
				showCamera(true);
			else
				showCamera(false);
			return;
		}else if(mCamera != null && (picturesTaken>0 && receipt_preview_layout.isShown())){
			if(picturesTaken>=1){
				// show the preview with the last pictures
				if(mCameraState.equals(CameraState.C_STATE_PREVIEW) || mCameraState.equals(CameraState.C_STATE_FOCUS)){
					showPicturesAfterBackPressed = true;
					// make preview not scrollable
					receipt_preview_layout.setScrollingEnabled(true);
					
					// scroll from the current position...
					scrolled = receipt_preview_layout.getScrollY();
					// ... to the position of the last ImageView
					scrollTo = (int) cropedPreviews.get(picturesTaken-1).getBottom();

					previewAlpha = -(0.2f / ((scrollTo-scrolled) > 0?(scrollTo-scrolled)*animationDpiFactor:(scrolled-scrollTo)*animationDpiFactor));
					mStartTime = System.currentTimeMillis();
					smoothScrollHandler.removeCallbacks(SmoothScrollUpDownThread);
					smoothScrollHandler.postDelayed(SmoothScrollUpDownThread, 0);
				}else {
					// restart the preview to take a new picture
					validation_again.performClick();
				}
				
				return;
			}
			showCamera(true);
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onStop() {
		try{
			showHintOnCreateCamera = false;
			
			if(cropedPreviews != null){
				LayoutParams newLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
				for(ImageView previews : cropedPreviews){
					if(previews.getDrawable() != null){
						previews.getDrawable().setCallback(null);
						previews.setImageBitmap(null); //Bild von vorher frei machen
					}else if(previews.getBackground() != null){
						previews.getBackground().setCallback(null);
						previews.setImageBitmap(null);
						receipt_preview_layout.setVisibility(View.GONE);
					}
					// hide the imageViews again
					previews.setLayoutParams(newLayoutParams);
				}
				picturesTaken = 0;
	    	}
			
			// Speicher freigeben...
			if(bMap!=null && !bMap.isRecycled()){
    			bMap.recycle();
    			bMap=null;
    		}
		}catch (Exception e) {
			// Dann ist das schon gestoppt
		}
		super.onStop();
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		stopPreviewAndFreeCamera();
		if(mSensorManager!= null)
			mSensorManager.unregisterListener(this);
	}
	
	@Override
	protected void onDestroy() {
		// Free the Memory from Camera
		if(validation_again != null && validation_again.getBackground() != null)
			validation_again.getBackground().setCallback(null);
		if(validation_ok != null && validation_ok.getBackground() != null)
			validation_ok.getBackground().setCallback(null);
		if(helpButton != null && helpButton.getBackground() != null)
			helpButton.getBackground().setCallback(null);
		if(border_view_top != null && border_view_top.getBackground() != null){
			border_view_top.getBackground().setCallback(null);
		}
		if(cropedPreviews != null){
			for(ImageView previews : cropedPreviews){
				if(previews.getDrawable() != null){
					previews.getDrawable().setCallback(null);
					previews.setImageBitmap(null); //Bild von vorher frei machen
				}else if(previews.getBackground() != null){
					previews.getBackground().setCallback(null);
					previews.setImageBitmap(null);
					receipt_preview_layout.setVisibility(View.GONE);
				}
			}
    	}

		if(validation_layout != null && validation_layout.getBackground() != null)
			validation_layout.getBackground().setCallback(null);
		if(redemption_Layout != null && redemption_Layout.getBackground() != null)
			redemption_Layout.getBackground().setCallback(null);
		
		super.onDestroy();
	}
/**
 * End implementing system callback methods 
 */

/**
 * Start implementing camera callback methods 
 */
	AutoFocusCallback triggerAutoFocusCallback = new AutoFocusCallback(){
		public synchronized void onAutoFocus(boolean success, Camera camera) {
			if(mCameraState.equals(CameraState.C_STATE_PICTURE)){
				mCamera.takePicture(shutterCallback, null, null, mPicture);
			}else{
				mCameraState = CameraState.C_STATE_PREVIEW;
			}
		}
	};
  
  	ErrorCallback cameraErrorCallback = new ErrorCallback() {
  		public void onError(int error, Camera camera) {
  			Log.e(TAG, "Error on Camera:" + error);
  			if(mCamera != null){
  				stopPreviewAndFreeCamera();
  			}else{
  				camera.stopPreview();
  				camera.release();
  			}
  			// LurCache leeren um mehr Systemspeicher frei zu machen...
  			finishWithResult(null, RESULT_CAMERA_ERROR);
  		}
  	};
  
  	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			try{
				// change layout
		        openValidation();
		        
				validation_ok.setEnabled(false);
				validation_again.setEnabled(false);
				sendRedemption.setEnabled(false);
				addReceiptPictureButton.setEnabled(false);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
  	};

  	private PictureCallback mPicture = new PictureCallback() {
  		public void onPictureTaken(byte[] data, Camera camera) {
  			if (data == null){
  				Log.d(TAG, "Error on data");
  				showError(getResources().getString(R.string.cashback_take_picture_memory_exception));
  				return;
  			}
	        
  			if(mCameraState.equals(CameraState.C_STATE_PICTURE)){
  				mCameraState = CameraState.C_STATE_PICTURE_IN_PROGRESS;
  				new CropingBackgroundTask(data).execute();
  			}else if(mCameraState.equals(CameraState.C_STATE_PICTURE_IN_PROGRESS)){
  				// this callback is called two times --> just one time is needed
  			}
  			// free Data Memory
  			data = null;
  			
  			// Trigger gc to get all possible memory
  			System.gc();
  		}
  	};
/**
 * End implementing camera callback methods 
 */
	
/**
 * start implementing helper methods for views
 */
	private void createLayout(){
		preview = (FrameLayout) findViewById(R.id.preview_view);
		
		border_view_top = (ImageView) findViewById(R.id.border_view_top);
		border_view_left = (ImageView) findViewById(R.id.border_view_left);
		border_view_right = (ImageView) findViewById(R.id.border_view_right);
		contextView = (FrameLayout) findViewById(R.id.cashback_redemption_context);
		  
		triggerButton = (ImageButton)findViewById(R.id.cashback_redemption_trigger);
		addReceiptPictureButton = (Button)findViewById(R.id.cashback_validation_add_picture_btn);
		redemption_Layout = (RelativeLayout)findViewById(R.id.cashback_redemption_context_tab);      
		helpButton = (Button)findViewById(R.id.cashback_redemption_need);      
		validation_layout = (RelativeLayout)findViewById(R.id.cashback_validation_context_tab);
		validation_layout.setLayoutParams(new RelativeLayout.LayoutParams(displayWidth, (int)(80*displaymetrics.density)));
	      
		validation_ok = (Button) findViewById(R.id.cashback_validation_go_on_btn);
		validation_again = (Button) findViewById(R.id.cashback_validation_back_btn);

		receipt_preview_layout = (CoupiesScrollView) findViewById(R.id.cashback_receipt_scroll_view);
		
		cropedPreviews = new ArrayList<ImageView>();
		cropedPreviews.add((ImageView) findViewById(R.id.cashback_redemption_croped_preview_one));
		cropedPreviews.add((ImageView) findViewById(R.id.cashback_redemption_croped_preview_two));
		cropedPreviews.add((ImageView) findViewById(R.id.cashback_redemption_croped_preview_three));
		// New layouts for adding number of articles on receipt
		redemptionNumberLayout = (RelativeLayout)findViewById(R.id.cashback_redemption_number_layout);
		redemptionNumberLayout.setLayoutParams(new RelativeLayout.LayoutParams(displayWidth, (int)(80*displaymetrics.density)));
	      
		redemptionNumberPositive = (Button) findViewById(R.id.cashback_redemption_number_positive_btn);
		redemptionNumberNegative = (Button) findViewById(R.id.cashback_redemption_number_negative_btn);
		sendRedemption = (Button) findViewById(R.id.cashback_validation_finish_btn);
		redemptionNumbers = (EditText) findViewById(R.id.cashback_redemption_numbers);
		complete_validation_layout = (RelativeLayout) findViewById(R.id.cashback_validation_layout);
		flashButton = (ImageButton) findViewById(R.id.camera_light_button);
	}
  
	private void createFadeoutAnim(){
		help_image = (ImageView)findViewById(R.id.cashback_help_image_overlay);
		helpTextViewTitle = (TextView)findViewById(R.id.cashback_help_image_overlay_top_text);
		helpTextViewText = (TextView)findViewById(R.id.cashback_help_image_overlay_bottom_text);
		if(help_image != null){
			help_image.setVisibility(View.INVISIBLE);
			helpTextViewTitle.setVisibility(View.INVISIBLE);
			helpTextViewText.setVisibility(View.INVISIBLE);
			  
			Animation fadeIn = new AlphaAnimation(0f, 1f);
			fadeIn.setDuration(2000);
			
			Animation fadeOut = new AlphaAnimation(1f, 0f);
			fadeOut.setStartOffset(3500);
			fadeOut.setDuration(4000);
			
			AnimationSet animation = new AnimationSet(false); //change to false
			animation.addAnimation(fadeIn);
			animation.addAnimation(fadeOut);
			// start the animations
			help_image.startAnimation(animation);
			helpTextViewTitle.startAnimation(animation);
			helpTextViewText.startAnimation(animation);
		}
	}
	
	public void showBorder(boolean showTop, boolean showMiddle){
		if(showTop){
    		border_view_top.setVisibility(View.VISIBLE);
    		// set params of left border
    		RelativeLayout.LayoutParams borderParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
    		borderParams.addRule(RelativeLayout.BELOW, border_view_top.getId());
    		borderParams.addRule(RelativeLayout.ALIGN_LEFT, border_view_top.getId());
    		border_view_left.setLayoutParams(borderParams);
    		// set Layout of right border
    		borderParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
    		borderParams.addRule(RelativeLayout.BELOW, border_view_top.getId());
    		borderParams.addRule(RelativeLayout.ALIGN_RIGHT, border_view_top.getId());
    		border_view_right.setLayoutParams(borderParams);
    		
    		border_view_left.setVisibility(View.VISIBLE);
    		border_view_right.setVisibility(View.VISIBLE);
		}else if(showMiddle){
			border_view_top.setVisibility(View.INVISIBLE);
			// set params of left border
    		RelativeLayout.LayoutParams borderParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
    		borderParams.addRule(RelativeLayout.BELOW, border_view_top.getId());
    		borderParams.addRule(RelativeLayout.ABOVE, redemption_Layout.getId());
    		borderParams.addRule(RelativeLayout.ALIGN_LEFT, border_view_top.getId());
    		border_view_left.setLayoutParams(borderParams);
    		// set Layout of right border
    		borderParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
    		borderParams.addRule(RelativeLayout.BELOW, border_view_top.getId());
    		borderParams.addRule(RelativeLayout.ABOVE, redemption_Layout.getId());
    		borderParams.addRule(RelativeLayout.ALIGN_RIGHT, border_view_top.getId());
    		border_view_right.setLayoutParams(borderParams);
    		
    		border_view_left.setVisibility(View.VISIBLE);
    		border_view_right.setVisibility(View.VISIBLE);
		}else{
			border_view_top.setVisibility(View.GONE);
			border_view_left.setVisibility(View.GONE);
			border_view_right.setVisibility(View.GONE);
		}
		
	}
	
	public void openValidation(){
		try{
			showValidationBorder();
			
			if(border_view_top!=null && border_view_left!=null && border_view_right!=null)
				showBorder(false, false);
	    	if(receipt_preview_layout!=null){
	    		receipt_preview_layout.setVisibility(View.VISIBLE);
	    	}else
	    		 showError("try_again");

		}catch(Exception e){
			Log.e("showValidation", "Error");
			e.printStackTrace();
		}
	}
	
	public void showValidationBorder(){
		try{
			if(picturesTaken == 2 && (mCameraState.equals(CameraState.C_STATE_PICTURE) ||
					mCameraState.equals(CameraState.C_STATE_PICTURE_IN_PROGRESS))){
				// disable and hide "add receipt part" button
				addReceiptPictureButton.setEnabled(false);
				addReceiptPictureButton.setVisibility(View.GONE);
			}else{
				addReceiptPictureButton.setEnabled(true);
				addReceiptPictureButton.setVisibility(View.VISIBLE);
			}
			
			if(complete_validation_layout != null)
				complete_validation_layout.setVisibility(View.VISIBLE);
			else
				showError("try_again");
		}catch(Exception e){
			Log.e("showValidation", "Error");
			e.printStackTrace();
		}
	}
	
	public void showCameraBorder(){
		try{
			if(border_view_top!=null && picturesTaken == 0)
	    		showBorder(true, false);
			if(complete_validation_layout != null)
	    		complete_validation_layout.setVisibility(View.GONE);
			
		}catch(Exception e){
			Log.e("showValidation", "Error");
			e.printStackTrace();
		}
	}
	    
    public void showCamera(boolean showPartOfLastPicture){
    	try{
			if(redemptionHint != null && contextView.isShown()){
				helpButton.performClick();
			}
			
			mCamera.startPreview();
	    	mCameraState = CameraState.C_STATE_PREVIEW;
	    	readyForAutofocus = true;
			
	    	if(receipt_preview_layout!=null && picturesTaken<1)
	    		receipt_preview_layout.setVisibility(View.GONE);
	    	
	    	if(complete_validation_layout != null){
	    		complete_validation_layout.setVisibility(View.GONE);
	    	}
	    	
	    	if(border_view_top!=null && picturesTaken == 0)
	    		showBorder(true, false);
	    	else if(border_view_top != null && showPartOfLastPicture && picturesTaken == 1)
	    		showBorder(false, true);
	    	else if(border_view_top != null && showPartOfLastPicture && picturesTaken == 2)
	    		showBorder(false, true);

	    	// Bitmap aus dem Speicher entfernen
	    	if(bMap!=null && !bMap.isRecycled()){
				bMap.recycle();
				bMap=null;
			}
	    	
	    	if(triggerButton != null){
	        	triggerButton.setEnabled(true);
	        }
    		// register Listener for the frist time or again to activate autofocus
    		mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
    	}catch(Exception e){
    		Log.e("showCamera", "Error");
    		e.printStackTrace();
    	}
    }
    
    public void showError(String where){
    	String title = getResources().getString(R.string.cashback_error_try_again_title);
    	if(where.equals("try_again")){
    		where = getResources().getString(R.string.cashback_error_try_again_text);
    		showChoiceDialog(title, where, "OK" ,new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.dismiss();
    				onResume();
    			}
        	}, null, null);
    	}else if(where.equals("camera_null")){
    		where = getResources().getString(R.string.cashback_get_camera_error);
    		showChoiceDialog(title, where, "OK" ,new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.dismiss();
    				onResume();
    			}
        	}, null, null);
    	}else{
	    	showChoiceDialog(title, where, "OK" ,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					stopPreviewAndFreeCamera();
					dialog.dismiss();
					myCashbackRedemption.finishWithResult(null, RESULT_CAMERA_ERROR);
				}
	    	}, null, null);
    	}
    }
/**
 * End implementing helper methods for views
 */
    
/**
 * Start implementing sensor callback methods
 */
    public void onSensorChanged(SensorEvent event) {

    	if(!mCameraState.equals(CameraState.C_STATE_PREVIEW) || 
    			validation_layout.isShown() ||
    			contextView.isShown() ||
    			!(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)){
    		return;
    	}

    	float x = event.values[0];
    	float y = event.values[1];
    	float z = event.values[2];
    	if (!mInitialized){
    	    mLastX = x;
    	    mLastY = y;
    	    mLastZ = z;
    	    mInitialized = true;
    	}
    	float deltaX  = Math.abs(mLastX - x);
    	float deltaY = Math.abs(mLastY - y);
    	float deltaZ = Math.abs(mLastZ - z);
    	
    	if (deltaX > 0.5 || deltaY > 0.5 || deltaZ > 0.5){
    		if(mCamera != null && readyForAutofocus){
    			mCameraState = CameraState.C_STATE_FOCUS;
	    		mCamera.autoFocus(triggerAutoFocusCallback);
    		}
    	}
    	
    	mLastX = x;
    	mLastY = y;
    	mLastZ = z;
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Kann leer bleiben
	}
/**
 * End implementing sensor callbacks
 */
	
/** Create a File for saving an image 
 * @throws IOException 
 **/
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type,Context context) throws IOException{
	    File outputDir = context.getCacheDir(); // context being the Activity pointer

	    // Create the storage directory if it does not exist
	    if (!outputDir.exists()){
	        if (!outputDir.mkdirs()){
	            Log.d("COUPIES", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	    	mediaFile = File.createTempFile("IMG_"+ timeStamp, "extension", outputDir);
	    } else {
	        return null;
	    }
	    return mediaFile;
	}
	
/**
 * Start implementing SurfaceView (to show camera preview)
 */
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
		private SurfaceHolder mHolder;
	
		public CameraPreview(Context context, Camera camera){
	        super(context);
	
	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        if(preview != null)
	        	mHolder = getHolder();
	        if(mHolder!=null){
		        mHolder.addCallback(this);
		     // deprecated setting, but required on Android versions prior to 3.0
	        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
	        	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        }
	    }
	    
	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	            mCamera.setPreviewDisplay(holder);
	        } catch (Exception e) {
	            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
	            e.printStackTrace();
	            showError(getResources().getString(R.string.cashback_image_send_error_text_1));
	            return;
	        }
	    }
	
	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // empty. Take care of releasing the Camera preview in your activity.
	    }
	
	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // --> no change and rotation possible
	    }
	}
/**
 * End implementing SurfaceView (to show camera preview)
 */

/**
 * Start implementing AsyncTask (to initialize camera and show preview off UI thread)
 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private class CameraOpeningBackgroundTask extends AsyncTask<Void,Void,Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			if(mCamera == null){
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
					Camera.CameraInfo info = new Camera.CameraInfo();
					
					for (int i=0; i < Camera.getNumberOfCameras(); i++) {
						Camera.getCameraInfo(i, info);
						
						if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
							mCamera = getCameraInstance(i);
						}
					}
			    }
				
			    if (mCamera == null) {
			    	mCamera = getCameraInstance(-1);
			    }
			}
			
			// could not open Camera
			if(mCamera == null)
				return false;
			
	        // set preview size and make any resize, rotate or
	        // reformatting changes here
	        mCameraParams = mCamera.getParameters();

            if(mCameraParams == null)
                return false;

        	List<Size> validSizes = mCameraParams.getSupportedPictureSizes();
        	List<Size> validPreviewSizes = mCameraParams.getSupportedPreviewSizes();
        	
        	int maxpictureWidth = 2048;

        	bestPreviewSize = getBestPreviewSize(validPreviewSizes, displayWidth, displayHeight);
        	bestPictureSize = getBestPictureSize(validSizes, maxpictureWidth, bestPreviewSize);

        	mCameraParams.setPictureSize(bestPictureSize.width, bestPictureSize.height);
    		mCameraParams.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        	
    		try{
	    		List<String> focusModes = mCameraParams.getSupportedFocusModes();
	    		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
		    		mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);				// automatischer Fokus
    			}
    		}catch (Exception e) {
				// There are no focus modes
			}
    		try{
    			// first check if Flash is available
    			if(myCashbackRedemption.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
    				// check what flashmode is available for this device
    				if(mCameraParams.getSupportedFlashModes()!=null && (mCameraParams.getSupportedFlashModes().contains(Parameters.FLASH_MODE_TORCH) ||  mCameraParams.getSupportedFlashModes().contains(Parameters.FLASH_MODE_ON)))
    					hasFlashlight = true;
		    		if(mCameraParams.getSupportedFlashModes()!= null && mCameraParams.getSupportedFlashModes().contains(Parameters.FLASH_MODE_AUTO)){
		    			mCameraParams.setFlashMode(Parameters.FLASH_MODE_AUTO);						// automatischer Blitz!
		    			mFlashState = FlashState.C_STATE_FLASH_AUTO;
		    		}
	    		}
    		}catch (Exception e) {
				//There is no Flashlight on the Smartphone 
			}

    		mCameraParams.setPictureFormat(ImageFormat.JPEG);
        	mCameraParams.setJpegQuality(100);
	
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// call super method
			super.onPostExecute(result);
			
			if(!result){
				showError("camera_null");
				return;
			}else if(mCamera != null){
				mCamera.setErrorCallback(cameraErrorCallback);
				mCamera.setParameters(mCameraParams);
	        	mCamera.setDisplayOrientation(90);
				// the accelerometer is used for autofocus
				mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				
				mPreview = new CameraPreview(myCashbackRedemption, mCamera);
				preview.addView(mPreview);

				triggerButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(help_image != null){
							help_image.clearAnimation();
							help_image.setVisibility(View.GONE);
						}
						if(helpTextViewTitle != null){
							helpTextViewTitle.clearAnimation();
							helpTextViewTitle.setVisibility(View.GONE);
						}
						if(helpTextViewText != null){
							helpTextViewText.clearAnimation();
							helpTextViewText.setVisibility(View.GONE);
						}
						if(redemptionHint != null && contextView.isShown()){
							helpButton.performClick();
						}else{
							triggerButton.setEnabled(false);
							
							// its necessary to change from FOCUS_MODE_TORCH to FOCUS_MODE_ON
							if(hasFlashlight){
								if(mCamera!=null && mFlashState!=null && mFlashState.equals(FlashState.C_STATE_FLASH_TORCH)){
									Parameters params = mCamera.getParameters();
									if (params.getSupportedFlashModes().contains(Parameters.FLASH_MODE_OFF)) {
								    	params.setFlashMode(Parameters.FLASH_MODE_OFF);
								    	mFlashState = FlashState.C_STATE_FLASH_OFF;
								    	mCamera.setParameters(params);
									}
									if (params.getSupportedFlashModes().contains(Parameters.FLASH_MODE_ON)) {
								    	params.setFlashMode(Parameters.FLASH_MODE_ON);
								    	mFlashState = FlashState.C_STATE_FLASH_ON;
								    	mCamera.setParameters(params);
									}
								}
							}

							//unregister Sensor to stop it in background
							mSensorManager.unregisterListener(myCashbackRedemption);
							
							// get an image from the camera after focus finishes
							if(mCameraState.equals(CameraState.C_STATE_PREVIEW)){
								mCameraState = CameraState.C_STATE_PICTURE;
								// use autofocus if ready --> else the autofocus is still running and will take a picture
								mCamera.autoFocus(triggerAutoFocusCallback);
							}else{
								// camera is autofocusing and will take a picture on finish focus
								mCameraState = CameraState.C_STATE_PICTURE;
							}
						}
					}
				});
				
				// new long receipt redemption
				addReceiptPictureButton.setOnClickListener(new OnClickListener() {	  
					public void onClick(View v) {
						if(picturesTaken==3){
							Toast.makeText(myCashbackRedemption, "Es sind maximal 3 Fotos erlaubt!", Toast.LENGTH_SHORT).show();
							return;
						}
						
						// save last taken picture in array to send later
						if(pictureFiles.size()<picturesTaken){
							try {
								File tempFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, getApplicationContext());
								
								FileOutputStream fos;
								fos = new FileOutputStream(tempFile);
								bMap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
								
								// free memory of FileOutputStream
								fos.flush();
								
								pictureFiles.add(tempFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						LayoutParams newLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (displayHeight-(80*displaymetrics.density)));
						cropedPreviews.get(picturesTaken).setLayoutParams(newLayoutParams);
						
						// make preview not scrollable
						receipt_preview_layout.setScrollingEnabled(false);
						
						// scroll from the current position...
						scrolled = receipt_preview_layout.getScrollY();
						// ... to the position of the next ImageView
						scrollTo = (int) (cropedPreviews.get(picturesTaken-1).getBottom()-(80*displaymetrics.density));

						previewAlpha = 0.2f / ((scrollTo-scrolled) > 0?(scrollTo-scrolled)*animationDpiFactor:(scrolled-scrollTo)*animationDpiFactor);
						mStartTime = System.currentTimeMillis();
						smoothScrollHandler.removeCallbacks(SmoothScrollUpDownThread);
						smoothScrollHandler.postDelayed(SmoothScrollUpDownThread, 0);
					}
				});
								
				helpButton.setOnClickListener(new OnClickListener() {	  
					public void onClick(View v) {
						if(redemptionHint == null){
							redemptionHint = new CashbackRedemptionContextView();
							FragmentManager fm = getSupportFragmentManager();
					        FragmentTransaction transaction = fm.beginTransaction();
					        transaction.add(R.id.cashback_redemption_context, redemptionHint);
					        transaction.commit();
						}
						
						if(contextView.isShown()){
							mCamera.startPreview();
					        contextView.setVisibility(View.GONE);
					        receipt_preview_layout.setVisibility(View.VISIBLE);

							triggerButton.setEnabled(true);
						}else{
							mCamera.stopPreview();
							receipt_preview_layout.setVisibility(View.GONE);
							contextView.setVisibility(View.VISIBLE);

							triggerButton.setEnabled(false);
						}
					}
				});
				
				if(remaining > 1){
					// more than one remaining Coupons are available
					validation_ok.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							TranslateAnimation slideAnim = new TranslateAnimation(0, -displayWidth, 0, 0);
							slideAnim.setAnimationListener(new AnimationListener() {
								
								public void onAnimationStart(Animation animation) {
									TranslateAnimation slideInAnim = new TranslateAnimation(validation_layout.getRight()-20*displaymetrics.density, 0, 0, 0);
									slideInAnim.setDuration(333);
									slideInAnim.setAnimationListener(new AnimationListener() {
										
										public void onAnimationStart(Animation animation) {
											
										}
										
										public void onAnimationRepeat(Animation animation) {
											
										}
										
										public void onAnimationEnd(Animation animation) {
											redemptionNumberLayout.layout(redemptionNumberLayout.getLeft(), redemptionNumberLayout.getTop(), redemptionNumberLayout.getRight(), redemptionNumberLayout.getBottom());
										}
									});
									redemptionNumberLayout.startAnimation(slideInAnim);
								}
								
								public void onAnimationRepeat(Animation animation) {
									// Nothing to do!!!
								}
								
								public void onAnimationEnd(Animation animation) {
									validation_layout.layout(validation_layout.getLeft()-displayWidth, validation_layout.getTop(), validation_layout.getRight()-displayWidth, validation_layout.getBottom());
								}
							});
							slideAnim.setDuration(333);
							validation_layout.startAnimation(slideAnim);
						}
					});
					
					redemptionNumberPositive.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(numberArticles+1 <= remaining){
								numberArticles ++;
								redemptionNumbers.setText(String.valueOf(numberArticles));
							}
						}
					});
					
					redemptionNumberNegative.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(numberArticles-1 > 0){
								numberArticles--;
								redemptionNumbers.setText(String.valueOf(numberArticles));
							}
						}
					});
					
					sendRedemption.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(redemptionNumbers.getText().toString().length()>0)
								numberArticles = Integer.parseInt(redemptionNumbers.getText().toString());
							newPictureFile=null;
							if(pictureFiles.size()<picturesTaken){
								try {
									newPictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, getApplicationContext());
									
									FileOutputStream fos;
									fos = new FileOutputStream(newPictureFile);
									bMap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
									
									// release outputStream
									fos.flush();
								} catch (FileNotFoundException e) {
									Log.d(TAG, "Error creating media file, No such File: ");
								}catch (IOException e1) {
									Log.d(TAG, "Error creating media file, IO-Error: ");
								}catch (Exception e){
									Log.d(TAG, "Error creating media");
								}
							}
							if(picturesTaken == 1 && newPictureFile!=null){
								finishCashbackOK(newPictureFile, numberArticles);
							}else if(picturesTaken > 1 ){
								if(newPictureFile!=null)
									pictureFiles.add(newPictureFile);
								finishMultiReceiptCashbackOK(pictureFiles, numberArticles);
							}else{
								Bundle errorbundle = new Bundle();
								CoupiesServiceException e = new CoupiesServiceException(getResources().getString(R.string.cashback_image_file_output_stream_error));
								errorbundle.putSerializable("exception", e);
								stopPreviewAndFreeCamera();
								finishWithResult(errorbundle, RESULT_CONNECTION_ERROR);
							}
						}
					});
				}else{
					validation_ok.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							newPictureFile=null;
							if(pictureFiles.size()<picturesTaken){
								try {
									newPictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, getApplicationContext());
									
									FileOutputStream fos;
									fos = new FileOutputStream(newPictureFile);
									bMap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
									
									// release outputStream
									fos.flush();
								} catch (FileNotFoundException e) {
									Log.d(TAG, "Error creating media file, No such File: ");
								}catch (IOException e1) {
									Log.d(TAG, "Error creating media file, IO-Error: ");
								}catch (Exception e){
									Log.d(TAG, "Error creating media");
								}
							}
							if(picturesTaken == 1 && newPictureFile!=null){
								finishCashbackOK(newPictureFile);
							}else if(picturesTaken > 1){
								if(newPictureFile!=null)
									pictureFiles.add(newPictureFile);
								finishMultiReceiptCashbackOK(pictureFiles);
							}else{
								Bundle errorbundle = new Bundle();
								CoupiesServiceException e = new CoupiesServiceException(getResources().getString(R.string.cashback_image_file_output_stream_error));
								errorbundle.putSerializable("exception", e);
								stopPreviewAndFreeCamera();
								finishWithResult(errorbundle, RESULT_CONNECTION_ERROR);
							}
						}
					});
				}
						      
				validation_again.setOnClickListener(new OnClickListener() {
					synchronized public void onClick(View v) {
						if(picturesTaken >= 1){	
							// frist scroll to last picture
							// scroll from the current position...
							scrolled = receipt_preview_layout.getScrollY();
							// ... to the position of the last ImageView
							scrollTo = (int) (cropedPreviews.get(picturesTaken-1).getTop()-(80*displaymetrics.density));
							
							if(Build.VERSION.SDK_INT < 11){
								// fade out last taken picture
								currentFade = ViewCompat.getAlpha(cropedPreviews.get(picturesTaken-1));
							}else
								currentFade = cropedPreviews.get(picturesTaken-1).getAlpha();
							
							mStartTime = System.currentTimeMillis();
							smoothFadeHandler.removeCallbacks(SmoothScrollAndFadeOutThread);
							smoothFadeHandler.postDelayed(SmoothScrollAndFadeOutThread, 0);						
						}else{
							picturesTaken--;
							showCamera(false);
						}
					}
				});
				
				if(hasFlashlight){
					flashButton.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(!mCameraState.equals(CameraState.C_STATE_PICTURE)){
								turnOnOffAutoFlash();
							}
						}
					});
					flashButton.setVisibility(View.VISIBLE);
					flashButton.setEnabled(true);
				}
				
				// Include and run the FadeOutAnimation for the hint
				if(showHintOnCreateCamera)
					createFadeoutAnim();
				
				showCamera(false);
			}else{
				Bundle errorbundle = new Bundle();
				CoupiesServiceException e = new CoupiesServiceException(getResources().getString(R.string.cashback_image_send_error_text_1));
				errorbundle.putSerializable("exception", e);
				stopPreviewAndFreeCamera();
				finishWithResult(errorbundle, RESULT_CONNECTION_ERROR);
			}
		}
	}
/**
 * End implementing AsyncTask (to initialize camera and show preview off UI thread)
 */

/**
 * Start implementing AsyncTask (to handle data from camera off UI thread)
 */
	private class CropingBackgroundTask extends AsyncTask<Void,Void,Boolean>{
		byte[] data;
		ProgressBar cropingProgress = null;
		
		public CropingBackgroundTask(byte[] incomingData){
			this.data = incomingData;
		}
		
		@Override
		protected void onPreExecute() {
			if(redemptionHint != null && contextView.isShown()){
				helpButton.performClick();
			}
			
			cropingProgress = (ProgressBar)myCashbackRedemption.findViewById(R.id.cashbackProcessingBar);
			if(cropingProgress != null)
				cropingProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				BitmapFactory.Options opt=new BitmapFactory.Options();
			    
			    /*	
			     *	before making an actual bitmap, check size
			     *	if the bitmap's size is too large,out of memory occurs. 
			    */
			    opt.inJustDecodeBounds=true;
			    BitmapFactory.decodeByteArray(data, 0, data.length,opt);
			    int srcSize=Math.max(opt.outWidth, opt.outHeight);
			    
			    // use max size of 2000
			    int maxSize = 2000;
			    
			    opt.inSampleSize = maxSize < srcSize ? (srcSize/maxSize) : 1;
			    
			    opt.inJustDecodeBounds=false;
			    opt.inPreferredConfig = Bitmap.Config.RGB_565;
			    opt.inPurgeable = true;
			    
			    Bitmap tmp=BitmapFactory.decodeByteArray(data, 0, data.length, opt);

			    //Scaling and rotation

			    // make sure no site is longer than 2000		    
			    float scale=(float)maxSize/srcSize;
			    
			    Matrix matrix=new Matrix();
			   
			    if(opt.outHeight < opt.outWidth){
			    	matrix.postRotate(90);
	            } else {
	            	matrix.postRotate(0);
	            }
			    
			    matrix.postScale(scale, scale);
			    
			    // calculate height of validationLayout
			    double picturePreviewHeightFactor = (double) bestPictureSize.width / bestPreviewSize.width;
			    double previewDisplayHeigtFactor = (double) bestPreviewSize.width / displayHeight;
			    int cutPositionsInPicture = 0;
			    
			    if(picturesTaken==0){				    
				    int[] location_validation = new int[2];
				    validation_layout.getLocationOnScreen(location_validation);
				    int yTopPosition = displayHeight-location_validation[1];
		            
		            double validationInPreview = previewDisplayHeigtFactor*yTopPosition;
		            cutPositionsInPicture = (int) (validationInPreview*picturePreviewHeightFactor);
		            
			    }else if(picturesTaken>0){
		            // calculating top overlay
		            int[] location_overlay = new int[2];
		            cropedPreviews.get(picturesTaken-1).getLocationOnScreen(location_overlay);
		            location_overlay[1] += cropedPreviews.get(picturesTaken-1).getMeasuredHeight();
		            int yTopPosition = location_overlay[1];
		            
		            double overlayInPreview = previewDisplayHeigtFactor*yTopPosition;
		            cutPositionsInPicture = (int) (overlayInPreview*picturePreviewHeightFactor);
	            }
			    
            	bMap = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth()-cutPositionsInPicture, tmp.getHeight(), matrix, true);
            	
            	// change scale size for previews
           		maxSize = (int) (displayHeight-(80*displaymetrics.density));
           		srcSize = Math.max(tmp.getHeight(), tmp.getWidth());
        		            	
           		float scaleHeight = (float)maxSize/srcSize;
            	matrix.postScale(scaleHeight, scaleHeight);
            	
	            if(picturesTaken==0)
	            	previewSampleBitmap = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth()-cutPositionsInPicture, tmp.getHeight(), matrix, true);
	            else
	            	previewSampleBitmap = Bitmap.createBitmap(tmp, cutPositionsInPicture, 0, tmp.getWidth()-(cutPositionsInPicture*2), tmp.getHeight(), matrix, true);
	            
	            if(picturesTaken == 0)
	            	System.out.println("imageView height: "+(int) (displayHeight-(80*displaymetrics.density)));
	            else
	            	System.out.println("imageView height: "+(int) (displayHeight-(160*displaymetrics.density)));
	            
	            System.out.println("previewSampleBitmap height: "+previewSampleBitmap.getHeight());
	            System.out.println("previewSampleBitmap width: "+previewSampleBitmap.getWidth());
	            
	            return true;
			} catch (Exception e) {
				e.printStackTrace();
				// nothing to do --> automatically show error
				return false;
	        }
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(cropingProgress != null)
				cropingProgress.setVisibility(View.GONE);
			if(result){
				mCameraState = CameraState.C_STATE_VALIDATE;
				// enable the buttons again
				if(validation_ok != null)
					validation_ok.setEnabled(true);
				if(validation_again != null)
					validation_again.setEnabled(true);
				if(addReceiptPictureButton != null)
					addReceiptPictureButton.setEnabled(true);
				if(sendRedemption != null)
					sendRedemption.setEnabled(true);
				
				// Display croped picture in Preview
				if(previewSampleBitmap != null){
					LayoutParams newLayoutParams;
						newLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (displayHeight-(80*displaymetrics.density)));
					
					cropedPreviews.get(picturesTaken).setLayoutParams(newLayoutParams);
					cropedPreviews.get(picturesTaken).setImageBitmap(previewSampleBitmap);
					
					// make last picture non transparent
					if(picturesTaken>=1)
						gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), 1.0f);
					
					// enable scrolling on previews
					receipt_preview_layout.setScrollingEnabled(true);
					
					// picture taken and displayed
					picturesTaken ++;
				}
					            
	            if(hasFlashlight){
	            	if(mFlashState != null && mFlashState.equals(FlashState.C_STATE_FLASH_ON) || mFlashState.equals(FlashState.C_STATE_FLASH_TORCH))
	            		turnOnOffAutoFlash();
	            }
			}else{
				// an error occurs --> show Message
				showError(getResources().getString(R.string.cashback_take_picture_memory_exception));
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			if(cropingProgress != null)
				cropingProgress.setVisibility(View.GONE);
			super.onCancelled();
		}
	}
/**
 * End implementing AsyncTask (to handle data from camera off UI thread)
 */

/**
 * Start helper methods for Camera options
 */
	private Camera.Size getBestPreviewSize(List<Camera.Size> previewSizes, int width, int height) {
		Comparator<Camera.Size> cmp = new Comparator<Camera.Size>() {
	        public int compare(Camera.Size size_1, Camera.Size size_2) {
	            return Integer.valueOf(size_1.width).compareTo(Integer.valueOf(size_2.width));
	        }
	    };
		
		double targetAspect = (double)width / (double)height;

		ArrayList<Camera.Size> matchedPreviewSizes = new ArrayList<Camera.Size>();
		final double ASPECT_TOLERANCE = 0.1;
		for(Size previewSize : previewSizes) {
		        double previewAspect = (double)previewSize.width / (double)previewSize.height;

		        if(Math.abs(targetAspect - previewAspect) < ASPECT_TOLERANCE &&
		                    (previewSize.width <= width && previewSize.height <= height)) {
		                matchedPreviewSizes.add(previewSize);
		        }
		}
		
		Camera.Size bestPreviewSize;
		if(!matchedPreviewSizes.isEmpty()) {
		    bestPreviewSize = Collections.max(matchedPreviewSizes, cmp);
		} else {
		    bestPreviewSize = Collections.max(previewSizes, cmp);
		}
		
		return bestPreviewSize;
	}
	
	private Camera.Size getBestPictureSize(List<Camera.Size> pictureSizes, int maxWidth, Size previewSize) {
		Comparator<Camera.Size> cmp = new Comparator<Camera.Size>() {
	        public int compare(Camera.Size size_1, Camera.Size size_2) {
	            return Integer.valueOf(size_1.width).compareTo(Integer.valueOf(size_2.width));
	        }
	    };
	    final double ASPECT_TOLERANCE = 0.1;
	    double targetAspect = (double)previewSize.width / (double)previewSize.height;

		ArrayList<Camera.Size> matchedPictureSizes = new ArrayList<Camera.Size>();

		for(Size pictureSize : pictureSizes) {
			double pictureAspect = (double)pictureSize.width / (double)pictureSize.height;
			
	        if(pictureSize.width <= maxWidth && Math.abs(targetAspect - pictureAspect) < ASPECT_TOLERANCE) {
        		matchedPictureSizes.add(pictureSize);
	        }
		}
		
		Camera.Size bestPictureSize;
		if(!matchedPictureSizes.isEmpty()) {
			bestPictureSize = Collections.max(matchedPictureSizes, cmp);
		} else {
			bestPictureSize = Collections.max(pictureSizes, cmp);
		}
		
		return bestPictureSize;
	}
	
	public void turnOnOffAutoFlash(){
		int resId = 0;
		Parameters params = mCamera.getParameters();
		if(mFlashState != null && (mFlashState.equals(FlashState.C_STATE_FLASH_TORCH) || mFlashState.equals(FlashState.C_STATE_FLASH_ON))){
			// turn falshlight off
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			mFlashState = FlashState.C_STATE_FLASH_OFF;
			resId = R.drawable.camera_flash_off;
		}else if(mFlashState != null && mFlashState.equals(FlashState.C_STATE_FLASH_OFF) && params.getSupportedFlashModes().contains(Parameters.FLASH_MODE_AUTO)){
			// turn flashlight to auto
			params.setFlashMode(Parameters.FLASH_MODE_AUTO);
			mFlashState = FlashState.C_STATE_FLASH_AUTO;
			resId = R.drawable.camera_flash_auto;
		}else{
			// turn falshlight torch/on
			if (params.getSupportedFlashModes().contains(Parameters.FLASH_MODE_TORCH)) {
				params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mFlashState = FlashState.C_STATE_FLASH_TORCH;
		    } else if (params.getSupportedFlashModes().contains(Parameters.FLASH_MODE_ON)) {
		    	params.setFlashMode(Parameters.FLASH_MODE_ON);
		    	mFlashState = FlashState.C_STATE_FLASH_ON;
		    }
			resId = R.drawable.camera_flash_on;
		}
		flashButton.setImageResource(resId);
		mCamera.setParameters(params);
	}
	
	/** A safe way to get an instance of the Camera object. */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static Camera getCameraInstance(int cameraId){
	    Camera c = null;
	    try {
	    	if(cameraId >= 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
	    		c = Camera.open(cameraId); // attempt to get a Camera instance
	    	else
	    		c = Camera.open();
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	/**
	 * When this function returns, mCamera will be null.
	 */
	private void stopPreviewAndFreeCamera() {
	    if (mCamera != null) {
	        // Call stopPreview() to stop updating the preview surface.
	        mCamera.stopPreview();
	        if(preview != null)
	        	preview.removeView(mPreview);
	        // Important: Call release() to release the camera for use by other
	        // applications. Applications should release the camera immediately
	        // during onPause() and re-open() it during onResume()).
	        mCamera.release();
	        mCamera = null;
	    }
	}
/**
 * End helper methods for Camera options
 */

/**
 * Start animation threads for multipicture feature
 */
	private Runnable SmoothScrollAndFadeOutThread = new Runnable() {
		public void run() {
			final long start = mStartTime;
			long millis = SystemClock.uptimeMillis() - start;

			if(currentFade > 0 || scrolled < scrollTo){
				gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), currentFade);
				receipt_preview_layout.scrollTo(0, scrolled);
				if(scrollTo-scrolled > 8*animationDpiFactor && scrollTo-scrolled>(scrollTo-scrolled)/2){
					scrolled += 8*animationDpiFactor;
				}else if(scrollTo-scrolled > 5*animationDpiFactor && scrollTo-scrolled>(scrollTo-scrolled)/4){
					scrolled += 5*animationDpiFactor;
				}else if(scrollTo-scrolled > 2*animationDpiFactor && scrollTo-scrolled>(scrollTo-scrolled)/8){
					scrolled += 2*animationDpiFactor;
				}else if(scrollTo-scrolled > 0){
					scrolled += 1;
				}
				
				if(currentFade > 0.5f){
					currentFade -= 0.05f;
				}else if(currentFade < 0.5f && currentFade > 0.2f){
					currentFade -= 0.04f;
				}else if(currentFade > 0.03f){
					currentFade -= 0.03f;
				}else
					currentFade = 0f;
				
				smoothFadeHandler.postAtTime(this, start + millis);
			}else if(currentFade > 0 || scrolled > scrollTo){
				gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), currentFade);
				receipt_preview_layout.scrollTo(0, scrolled);
				if(scrolled-scrollTo > 8*animationDpiFactor && scrolled-scrollTo>(scrolled-scrollTo)/2){
					scrolled -= 8*animationDpiFactor;
				}else if(scrolled-scrollTo > 5*animationDpiFactor && scrolled-scrollTo>(scrolled-scrollTo)/4){
					scrolled -= 5*animationDpiFactor;
				}else if(scrolled-scrollTo > 2*animationDpiFactor && scrolled-scrollTo>(scrolled-scrollTo)/8){
					scrolled -= 2*animationDpiFactor;
				}else if(scrolled-scrollTo > 0){
					scrolled -= 1;
				}
				
				if(currentFade > 0.5f){
					currentFade -= 0.05f;
				}else if(currentFade < 0.5f && currentFade > 0.2f){
					currentFade -= 0.04f;
				}else if(currentFade > 0.03f){
					currentFade -= 0.03f;
				}else
					currentFade = 0f;
				
				smoothFadeHandler.postAtTime(this, start + millis);
			}else{
				cropedPreviews.get(picturesTaken-1).setImageBitmap(null);

				if(picturesTaken>=2)
					gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-2), 0.8f);
				gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), 1f);
				
				// decrease alreadyTakenPictures
				picturesTaken--;
				
				if(bMap != null && !bMap.isRecycled()){
					// recycle bMap and remove from array
					bMap.recycle();
					pictureFiles.remove(bMap);
				}
				
				// disable scrolling
				receipt_preview_layout.setScrollingEnabled(false);
				
				// start Camera again
				showCamera(true);
				
				smoothFadeHandler.removeCallbacks(this);
			}
		}
    };
    
    private static void gingerbreadAlphaWorkaround(View view, float alpha){
    	if(Build.VERSION.SDK_INT < 11){
    		final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
    	}else
    		view.setAlpha(alpha);
    }
    
    private Runnable SmoothScrollUpDownThread = new Runnable() {
		public void run() {
			final long start = mStartTime;
			long millis = SystemClock.uptimeMillis() - start;
			
			if(scrolled < scrollTo){
				// Scroll animation bottom up and fade out
				receipt_preview_layout.scrollTo(0, scrolled);
				if(!showPicturesAfterBackPressed)
					gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), 1.0f - previewAlpha*scrolled);
				
				if(scrollTo-scrolled > 8*animationDpiFactor && scrollTo-scrolled>(scrollTo-scrolled)/2){
					scrolled += 8*animationDpiFactor;
				}else if(scrollTo-scrolled > 5*animationDpiFactor && scrollTo-scrolled>(scrollTo-scrolled)/4){
					scrolled += 5*animationDpiFactor;
				}else if(scrollTo-scrolled > 2*animationDpiFactor && scrollTo-scrolled>(scrollTo-scrolled)/8){
					scrolled += 2*animationDpiFactor;
				}else if(scrollTo-scrolled > 0){
					scrolled += 1;
				}
				
				smoothScrollHandler.postAtTime(this, start + millis);
			}else if(scrolled > scrollTo){
				// Scroll animation bottom down
				receipt_preview_layout.scrollTo(0, scrolled);
				if(!showPicturesAfterBackPressed)
					gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), 1.0f + previewAlpha*scrolled);

				if(scrolled-scrollTo > 8*animationDpiFactor && scrolled-scrollTo>(scrolled-scrollTo)/2){
					scrolled -= 8*animationDpiFactor;
				}else if(scrolled-scrollTo > 5*animationDpiFactor && scrolled-scrollTo>(scrolled-scrollTo)/4){
					scrolled -= 5*animationDpiFactor;
				}else if(scrolled-scrollTo > 2*animationDpiFactor && scrolled-scrollTo>(scrolled-scrollTo)/8){
					scrolled -= 2*animationDpiFactor;
				}else if(scrolled-scrollTo > 0){
					scrolled -= 1;
				}
			
				smoothScrollHandler.postAtTime(this, start + millis);
			}else{
				// show Camera
				if(!showPicturesAfterBackPressed)
					showCamera(true);
				else{
					showPicturesAfterBackPressed = false;
					gingerbreadAlphaWorkaround(cropedPreviews.get(picturesTaken-1), 1.0f);
					LayoutParams newLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
					cropedPreviews.get(picturesTaken).setLayoutParams(newLayoutParams);
					mCameraState = CameraState.C_STATE_VALIDATE;
					openValidation();
				}
				smoothScrollHandler.removeCallbacks(this);
			}
		}
    };
/**
 * End animation threads for multipicture feature
 */
}
