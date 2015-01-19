/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;

import de.coupies.coupies_framework_lib.R;
import de.coupies.framework.controller.redemption.AbstractRedemptionActivity;
import de.coupies.framework.controller.redemption.CouponRedemptionContextView;

/**
 * The barcode reader activity itself. This is loosely based on the CameraPreview
 * example included in the Android SDK.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends AbstractRedemptionActivity implements SurfaceHolder.Callback {

  private static final String TAG = CaptureActivity.class.getSimpleName();
  private static final String COUPIES = "coupies";

  private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;
  static {
    DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>(5);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
  }
  
  private CaptureActivityHandler handler;
  private ViewfinderView viewfinderView;
  private boolean hasSurface;
  @SuppressWarnings("unused")
  private Result lastResult;
  private Vector<BarcodeFormat> decodeFormats;
  private String characterSet;
  private InactivityTimer inactivityTimer;
  private BeepManager beepManager;
  private CouponRedemptionContextView contextView;

  ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  public Handler getHandler() {
    return handler;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.coupon_redemption_camera);

    CameraManager.init(getApplication());
    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    handler = null;
    hasSurface = false;
    lastResult = null;
    inactivityTimer = new InactivityTimer(this);
    beepManager = new BeepManager(this);

    contextView = (CouponRedemptionContextView) findViewById(R.id.coupon_redemption_context);
    contextView.init(this);
  }
  
  @SuppressWarnings("deprecation")
@Override
  protected void onResume() {
    super.onResume();
    resetStatusView();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder);
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this);
      if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
    	  surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    decodeFormats = null;
    characterSet = null;
    beepManager.updatePrefs();
    inactivityTimer.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    inactivityTimer.onPause();
    CameraManager.get().closeDriver();
  }

  @Override
  protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
		  
	  if (keyCode == KeyEvent.KEYCODE_BACK) {
		  if (contextView.isHelpVisible()) {
			  contextView.setHelpVisibility(false);
			  return true;
		  }
    } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
      // Handle these events so they don't launch the Camera app
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
  
	public void surfaceCreated(SurfaceHolder holder) {
	    if (!hasSurface) {
	      hasSurface = true;
	      initCamera(holder);
	    }
	}

  	public void surfaceDestroyed(SurfaceHolder holder) {
	  hasSurface = false;
  	}

  	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  	}
  
  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
  	public void handleDecode(Result rawResult, Bitmap barcode) {
  		stickerCode = null;
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();
		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
		String qrCode = resultHandler.getDisplayContents().toString();
		
		if (qrCode != null && qrCode.contains(COUPIES)) {
			int lastSlash = qrCode.lastIndexOf("/");
			if (lastSlash > 0 && lastSlash < qrCode.length() - 1) {
				stickerCode = qrCode.substring(lastSlash + 1);
			}
		}
		
		if (stickerCode != null) {
			finishWithSticker();
		}
		else {
			finishWithBadSticker();
		}
  	}

  /**
   * We want the help screen to be shown automatically the first time a new version of the app is
   * run. The easiest way to do this is to check android:versionCode from the manifest, and compare
   * it to a value stored as a preference.
   */
  @SuppressWarnings("unused")
private boolean showHelpOnFirstLaunch() {
    Boolean firstLaunch = false;
    return firstLaunch;
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    try {
    	CameraManager.get().openDriver(surfaceHolder);
      // Creating the handler starts the preview, which can also throw a RuntimeException..
      if (handler == null) {
        handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
      }
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
    } catch (RuntimeException e) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializating camera", e);
    }
  }

  private void resetStatusView() {
    viewfinderView.setVisibility(View.VISIBLE);
    lastResult = null;

  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();
  }
  
  	@Override
  	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
  	}
}
