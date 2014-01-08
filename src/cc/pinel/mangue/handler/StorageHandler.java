package cc.pinel.mangue.handler;

import org.apache.log4j.Logger;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KProgress;

public abstract class StorageHandler extends Thread {
	private static final Logger logger = Logger.getLogger(StorageHandler.class);

	private final KindletContext context;

	private final String busyText;

	public StorageHandler(KindletContext context) {
		this.context = context;
		this.busyText = null;
	}

	public StorageHandler(KindletContext context, String busyText) {
		this.context = context;
		this.busyText = busyText;
	}

	public abstract void handleRun() throws Exception;

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		KProgress progress = this.context.getProgressIndicator();

		if (this.busyText != null)
			progress.setString(this.busyText);

		progress.setIndeterminate(true);

		try {
			handleRun();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			progress.setIndeterminate(false);
		}
	}
}
