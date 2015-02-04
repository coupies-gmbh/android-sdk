#COUPIES Android SDK
The COUPIES Android SDK enables you to easily integrate coupons in your app. The easiest way is to display everything in our CoupiesWebView (a customized WebView) or in an original WebView and let COUPIES do the rendering. You can customize the appearance with CSS. It is also possible to retrieve the objects directly (e.g. coupons and locations) and do the UI natively.

##Installation

We recommend gradle builds. If you wish to integrate COUPIES based on the Eclipse IDE please conatct us at felix.schul@coupies.de for more information.

###Install using Maven

The easiest way to install the COUPIES Android SDK ist to use maven import. Therefor you have to add the following lines to your gradle file:
```xml
repositories {
    mavenCentral()
    maven {
        url "https://raw.github.com/coupies-gmbh/android-sdk/mvn-repo";
    }
}

dependencies {
    compile 'de.coupies.android:coupies-framework_lib:<version>'
}
```

###Install manually

To install the COUPIES Android SDK without Maven, follow these steps:

1. Download the latest stable version.
2. Copy the coupies-framework_lib-<version>.aar from HelloCOUPIES/libs into the libs folder of your project.
3. Add the following lines to your gradle file:
```xml
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    compile(name:'coupies-framework_lib-<version>', ext:'aar')
}
```

###Add these permissions in your Manifest:
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.NFC" />
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```

###Add these features in your Manifest:
```xml
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false"/>
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.nfc" android:required="false" />
```
###Add this metadata and activitys in your Manifest:
```xml
<meta-data  android:name=”com.google.android.gms.version” 
            android:value=”@integer/google_play_service_version”/>

<activity android:name="com.google.zxing.client.android.CaptureActivity"
          android:screenOrientation="portrait"
          android:configChanges="orientation|keyboardHidden"
          android:noHistory="false"
          android:windowSoftInputMode="stateAlwaysHidden">
  <intent-filter>
    <action android:name="android.intent.action.MAIN"/>
    <category android:name="android.intent.category.DEFAULT"/>
  </intent-filter>
  <intent-filter>
    <action android:name="com.google.zxing.client.android.SCAN"/>
    <category android:name="android.intent.category.DEFAULT"/>
  </intent-filter>
</activity>

<activity android:name="de.coupies.framework.controller.redemption.CashbackRedemption"
          android:screenOrientation="portrait"
          android:configChanges="orientation|keyboardHidden"
          android:noHistory="false"
          android:windowSoftInputMode="stateAlwaysHidden">
</activity>

<activity android:name="de.coupies.framework.controller.redemption.CouponRedemptionNfc"
          android:noHistory="true"
          android:launchMode="singleTop"
          android:screenOrientation="portrait">
</activity>

<activity android:name="de.coupies.framework.controller.redemption.RedemptionActivity"
          android:noHistory="true"
          android:launchMode="singleTop"
          android:screenOrientation="portrait">
</activity>
```

##Usage

To access the COUPIES API you need to register your application and receive an API-key (felix.schul@coupies.de). You can register multiple applications and for each of them you will receive an API-key for accessing the API resources.

To run the example project (HelloCOUPIES), clone the repo, and open the Android Studio project in the "HelloCOUPES" directory. 

Set your COUPIES API-key and the required COUPIES API-level. In the HelloCOUPIES demo app you have to add the key in the AbstractFragment class:

```java
public abstract class AbstractFragment extends Fragment {
// TODO: please return your coupies API Key here
private static final String API_KEY = null;
```

As API-level set “4” for all coupons including cashback or “3” for all coupons, deals and offers excluding cashback.

## Author

Lars Eimermacher, lars.eimermacher@coupies.de

##Bugs

If you encounter any bugs. Please [report them](https://github.com/coupies-gmbh/android-sdk/issues).
