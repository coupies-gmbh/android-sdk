package de.coupies.framework.services.async.tasks;

public interface Executable {

	public void run() throws Exception;
	public void onError(Throwable e);

	public interface BackgroundProcess extends Executable {
		public void runOnUiThread();	
	}
	
	public abstract class AbstractBackgroundProcess implements BackgroundProcess {
		@Override
		public void runOnUiThread() {}
	}
}
