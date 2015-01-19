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

package com.google.zxing.client.android.result;

import android.app.Activity;

import com.google.zxing.client.result.ParsedResult;

/**
 * Offers appropriate actions for URLS.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class URIResultHandler extends ResultHandler {

	URIResultHandler(Activity activity, ParsedResult result) {
		super(activity, result);
		// TODO Auto-generated constructor stub
	}
  // URIs beginning with entries in this array will not be saved to history or copied to the
  // clipboard for security.

	@Override
	public int getButtonCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getButtonText(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDisplayTitle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void handleButtonPress(int index) {
		// TODO Auto-generated method stub
		
	}
 
}
