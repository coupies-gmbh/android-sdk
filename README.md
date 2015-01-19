#Welcome to the COUPIES Android SDK
Android SDK to integrate COUPIES coupons into your Android application

#Introduction
The COUPIES API provides access to the core functionality of COUPIES. To use the API you need to register your application and receive an API-Key (felix.schul@coupies.de). You can register multiple applications and for each of them you will receive an API-key for accessing the API resources.
The COUPIES API enables you to integrate and use coupons in your application. It pro- vides the following capabilities:
<ul>
<li>Search for stores (locations) that offer coupons in a specified area</li>
<li>Search for categories (interests) in a specific area</li>
<li>Retrieve available coupons in a specific area, store, or category</li>
<li>Search for coupons that match a certain search string</li>
<li>View details for a coupon (deal, image, availability, distance to next store)</li>
<li>Directly redeem a coupon in your mobile application using the COUPIES-Touchpoints etc.</li>
</ul>

The framework enables you to easily integrate coupons in your app. The easiest way is to display everything in our CoupiesWebView (a customized WebView) or in an original WebView and let COUPIES do the rendering. You can customize the appearance CSS. It is also possible to retrieve the objects directly (e.g. coupons and locations) and do the UI natively.

#1. Integration</br>
We recommend gradle builds and will only document the gradle setup. If you wish to integrate COUPIES based on the Eclipse IDE please conatct us at <a href="mailto:info@coupies.de">info@coupies.de</a> for more informations.

##nessasary permissions in your Manifest:
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.NFC" />
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```

##nessasary features in your Manifest:
```xml
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false"/>
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.nfc" android:required="false" />
```
##nessasary metadata and Activitys in your Manifest:
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

##nessasary dependencies in your gradle.build:
```groovy
repositories {
    mavenCentral()
    maven {
        url "https://raw.github.com/coupies-gmbh/android-sdk/coupies-framework_lib";
    }
}

dependencies {
       compile ('de.coupies.framework:<INSERT-THE-LATEST-RELEASE>')
   }
```

##Set up the SDK in your Application class:
Set your COUPIES API-key and the required API-Level. In the CoupiesDemoApp you have to add the Key in the AbstractActivity class:
```java
public abstract class AbstractActivity extends Activity {
// TODO: please return your coupies API Key here
private static final String API_KEY = null;
```
As API-Level set “4” for all coupons including cashback or “3” for all coupons, deals and offers excluding cashback.

#2. Using the framework</br>
The following section show you how to use the integrated COUPIES SDK.
##Objects and HTML-Requests
There are three ways, in which you can retrieve coupons, stores etc. with the COUPIES- framework:
<ul>
<li>
HTML-Requests (CoupiesWebView): If you want to display coupons or the details of a coupon as easy as possible, you can   display it in the CoupiesWebView. This way you do not have to worry about the layout (e.g. of the redemption view) – COUPIES does all this for you. The layout can be customized to your app using CSS. All interceptions and delegation to the COUPIES-framework will be done au- tomatically by the CoupiesWebView.
</li>
<li>
HTML-Requests (WebView): If you want to display coupons or the details of a coupon in a fast and easy way, you can also display it in a WebView. This way you do not have to worry about the layout (e.g. of the redemption view) – COUPIES does all this for you. The layout can be customized to your app using CSS. Be- cause the redemption of a coupon often requires access to the camera (e.g. using the COUPIES-Touchpoint), the click in “Redeem now” in a WebView must be in- tercepted and delegated to the COUPIES-framework. This can be done with a few lines of code, check out the CoupiesDemoApp.
</li>
<li>
Objects: You can retrieve all the objects of the COUPIES-API like offers/coupons, stores (locations), categories (interests) as native Java objects. This way you can implement coupons deeply in your application and handle everything in a native way, e.g. displaying stores on a map.
</li>
</ul>
You can also mix both representations and use the best of both sides. E.g. you can display coupons and stores in a native way like coupons in a ListView and display the coupon de- tails and the redemption in a CoupiesWebView/WebView (see HelloCoupies for an example).

##Initialize the COUPIES framework</br>
You can initialize the COUPIES framework via the static CoupiesManager class.
```java
ServiceFactory serviceFactory = CoupiesManager.createTestServiceFactory(context, API_KEY, API_LEVEL);
```
There are two alternative methods:
1. CoupiesManager.createLiveServiceFactory(Context context, String apiKey, String apiLevel): Opens a connection to the COUPIES Live server.
2. CoupiesManager.createServiceFactory(Context context, String apiKey, String pro- tocol, String host, Integer port, String apiLevel): for full configuration.

##Create a CoupiesSession
Before you start to communicate with the COUPIES server you have to create a Coupies- Session object. This object gives you the permission to access to the COUPIES re- sources. The PartnerSession is very simple because it does not require a COUPIES ac- count.
```java
AuthentificationService authService = serviceFactory.createAuthentificationService(); PartnerSession session = authService.createPartnerSession(null);
```
COUPIES will then identify the user using the Device ID. If you want to use your own unique identifier, for the user, just submit the user ID instead of null:
```java
PartnerSession session = authService.createPartnerSession(“this is the partner-token from the documentation”);
````
##Using a CoupiesWebView to display coupons
There is an ‘_html’ method for most of the service methods in the COUPIES framework. These methods return a String with html data that you can use to display a list of coupons, the coupon details or a redemption screen directly in a CoupiesWebView/WebView.
1. Using CoupiesWebView as layout and initialize the View
```java
@Override
public void onCreate(Bundle savedInstanceState) {
setContentView(R.layout.coupies_web_view);
coupiesWebView = (CoupiesWebView)findViewById(R.id.coupiesWebView);
coupiesWebView.init(getActivity(), coupiesSession, serviceFactory);
}
```
2. Call the ‘_html’ method
```java
String htmlData = couponService.getCouponFeed_html(session, myPosition);
```

>Note: The CoupiesService uses an Internet connection to retrieve the coupon data. Please make sure to call this method from a new thread (outside the UI-thread), otherwise Android will throw an NetworkOnMainThreadException.

3. Load the htmlData into the CoupiesWebView
```java
coupiesWebView.loadCoupiesContent(couponListHTML);
```
4. Forward ActivityResults to the CoupiesWebView
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
super.onActivityResult(requestCode, resultCode, data);
coupiesWebView.handleRedemeCallback(requestCode, resultCode, data);
}
```
##Using a WebView to display coupons
1. Call the ‘_html’ method
```java
String htmlData = couponService.getCouponFeed_html(session, myPosition);
```
>Note: The CoupiesService uses an Internet connection to retrieve the coupon data. Please make sure to call this method from a new thread (outside the UI-thread), otherwise Android will throw an NetworkOnMainThreadException.

2. Create a WebView to display the html data:
```java
String baseUrl = serviceFactory.getAPIBaseUrl();
WebView webView = new WebView(this);
webView.loadDataWithBaseURL(baseUrl, htmlData, "text/html", "UTF-8", baseUrl);
```
The Android WebView needs a base url to display the html data. You get the base url from the method ServiceFactory.getAPIBaseUrl() (see HelloCOUPIES).

##Using objects to display coupons
Create a new CouponService and call the method getCouponFeed
```java
CouponService couponService = serviceFactory.createCouponService();
Coordinate myPosition = new CoordinateImpl(50.937056F, 6.958237F);
List<Offer> coupons = couponService.getCouponFeed(session, myPosition);
````
You will then retrieve a list of coupon objects.

##Using objects to display locations on the map
Create a new LocationService and call the method getLocationWithPosition Create a new CouponService and call the method getCouponsWithLocation
```java
LocationService locationService = serviceFactory.createLocationService();
Coordinate myPosition = new CoordinateImpl(50.937056F, 6.958237F);
Int inRadius = 10000; // 10 km
Integer limit = 100; // returned list has 100 items
List<Location> locations = locationService.getLocationWithPosition(session, myPosition, inRadius, limit);
// you have to pick the Location from the list you searching for
Location myLocation = locations.get(“XY”);
CouponService couponService = serviceFactory.createCouponService();
List<Offer> coupons = couponService.get CouponsWithLocation(session, myPosition, locationId, limit);
```
You will then retrieve a list of coupon objects.

#3.Function overview
An instance of an CouponService provides a variety of functions. Below these functions are represented with a short explanation.
##Native functions:
Return one Coupon with the specified coupon-id = id:
```java
Offer getCoupon(CoupiesSession session, Coordinate coordinate, int id)
```
>Note: The function getCouponFeed(...) should used in partnered applications using native lists

Returns a list of coupons with best conversion ordered by number of likes:
```java
List<Offer> getCouponFeed(CoupiesSession session, Coordinate coordinate)
```
Returns a list of featured coupons:
```java
List<Offer> getCouponsWithHighlights(CoupiesSession session, Coordinate coordinate, Integer limit)
```
Returns a list of coupons with the specified category = categoryId:
```java
List<Offer> getCouponsWithCategory(CoupiesSession session, Coordinate coordinate, Integer radius, ￼int categoryId￼)
```
Returns a list of coupons near the position specified in coordinate:
```java
List<Offer> getCouponsWithPosition(CoupiesSession session, Coordinate coordinate, int inRadius, ￼Integer limit)
```
Returns a list of coupons provided in an specified location:
```java
List<Offer> getCouponsWithLocation(CoupiesSession session, Coordinate coordinate, int locationId, ￼Integer inLimit)
```
Returns a list of coupons or one coupon based on the search string:
```java
List<Offer> search(CoupiesSession session, Coordinate coordinate, String inQuery, Integer limit)
```

##HTML functions:
(Same functions like the native ones but return HTML code to insert in a WebView)

>Note:The function getCouponFeed_html(...) should used in partnered applications using Web- View
```java
String getCoupon_html(CoupiesSession session, Coordinate coordinate, int id)
String getCouponFeed_html(CoupiesSession session, Coordinate coordinate)
String getCouponsWithHighlights_html(CoupiesSession session, Coordinate coordinate, Integer limit)
String search_html(CoupiesSession session, Coordinate coordinate, String inQuery, Integer limit)
```
