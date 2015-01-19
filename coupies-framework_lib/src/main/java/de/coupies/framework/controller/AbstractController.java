package de.coupies.framework.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import de.coupies.framework.services.ServiceFactory;
import de.coupies.framework.session.CoupiesSession;

public abstract class AbstractController{
	protected ServiceFactory serviceFactory;
	protected CoupiesSession session;
	
	public AbstractController(CoupiesSession inSession, ServiceFactory inFactory) {
		session = inSession;
		serviceFactory = inFactory;
	}
	
	public ServiceFactory getServiceFactory() {
		return serviceFactory;
	}
	
	public CoupiesSession getCoupiesSession() {
		return session;
	}
	
	protected void redirect(Activity context, Class<? extends Activity> target, Bundle bundle) {
		Intent intent = createIntent(context, target, bundle, -1);
		context.startActivity(intent);
	}
	
	protected void redirect(Activity context, Class<? extends Activity> target, Bundle bundle, int flags) {
		Intent intent = createIntent(context, target, bundle, flags);
		context.startActivity(intent);
	}
	
	protected void redirectForResult(Activity context, Class<? extends Activity> target, Bundle bundle, int requestCode) {
		Intent intent = createIntent(context, target, bundle, -1);
		context.startActivityForResult(intent, requestCode);
	}
	
	protected void redirectForResult(Activity context, Class<? extends Activity> target, Bundle bundle, int flags, int requestCode) {
		Intent intent = createIntent(context, target, bundle, flags);
		context.startActivityForResult(intent, requestCode);
	}
	
	private Intent createIntent(Activity context, Class<? extends Activity> target, Bundle bundle, int flags) {
		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (flags != -1) {
			intent.setFlags(flags);
		}
		if (context != null) {
			intent.setClass(context, target);
		}
		else {
			throw new CoupiesManagerRuntimeException(
				new NullPointerException("Callback activity cannot be null. " +
					"Use setcontext(Activity incontext) before this operation."));
		}
		return intent;
	}
}
