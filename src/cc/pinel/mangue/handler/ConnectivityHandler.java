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

public abstract class ConnectivityHandler implements com.amazon.kindle.kindlet.net.ConnectivityHandler {
	private static final Logger logger = Logger.getLogger(ConnectivityHandler.class);

	private final KindletContext context;

	private final String busyText;

	public ConnectivityHandler(KindletContext context) {
		this.context = context;
		this.busyText = null;
	}

	public ConnectivityHandler(KindletContext context, String busyText) {
		this.context = context;
		this.busyText = busyText;
	}

	public abstract void handleConnected() throws Exception;

	/**
	 * @see com.amazon.kindle.kindlet.net.ConnectivityHandler#connected()
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
	 * @see com.amazon.kindle.kindlet.net.ConnectivityHandler#disabled(com.amazon.kindle.kindlet.net.NetworkDisabledDetails)
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
