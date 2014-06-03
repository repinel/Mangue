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

import cc.pinel.mangue.Main;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KProgress;

public abstract class StorageHandler extends Thread {
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
			Main.logger.error(e);
		} finally {
			progress.setIndeterminate(false);
		}
	}
}
