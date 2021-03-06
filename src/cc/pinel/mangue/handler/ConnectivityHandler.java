/*
 * Copyright 2014 Roque Pinel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.pinel.mangue.handler;

import org.apache.log4j.Logger;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.net.NetworkDisabledDetails;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import com.amazon.kindle.kindlet.ui.KProgress;

/**
 * It helps handling network accesses.
 * 
 * @author Roque Pinel
 *
 */
public abstract class ConnectivityHandler implements com.amazon.kindle.kindlet.net.ConnectivityHandler {
	private static final Logger logger = Logger.getLogger(ConnectivityHandler.class);

	private final KindletContext context;

	private final String busyText;

	/**
	 * @param context the kindlet context
	 */
	public ConnectivityHandler(KindletContext context) {
		this.context = context;
		this.busyText = null;
	}

	/**
	 * @param context the kindlet context
	 * @param busyText the busy text to be displayed
	 */
	public ConnectivityHandler(KindletContext context, String busyText) {
		this.context = context;
		this.busyText = busyText;
	}

	/**
	 * Should be override to for the access to be handled.
	 * 
	 * @throws Exception the exception thrown, if any
	 */
	public abstract void handleConnected() throws Exception;

	/**
	 * {@inheritDoc}
	 */
	public void connected() throws InterruptedException {
		KProgress progress = this.context.getProgressIndicator();

		if (this.busyText != null)
			progress.setString(this.busyText);

		progress.setIndeterminate(true);

		try {
			handleConnected();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			progress.setIndeterminate(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void disabled(NetworkDisabledDetails details) {
		logger.info(details.getLocalizedMessage());

		try {
			KOptionPane.showMessageDialog(this.context.getRootContainer(), details.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
