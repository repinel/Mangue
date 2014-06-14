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
package cc.pinel.mangue.ui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import org.kwt.ui.KWTProgressBar;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.ConnectivityHandler;
import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.storage.GeneralStorage;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KProgress;
import com.amazon.kindle.kindlet.ui.image.ImageUtil;

public class ViewPanel extends KPanel implements KeyListener {
	private static final long serialVersionUID = -2485604965935171736L;

	private final Main main;

	private final KImage mangaImage;

	private final KWTProgressBar progressBar;

	private int pageNumber = 1;

	private boolean isPortrait = true;

	private Chapter chapter;

	public ViewPanel(Main main, Chapter chapter) {
		this(main, chapter, null);
	}

	public ViewPanel(Main main, Chapter chapter, Integer lastPageNumber) {
		super(new GridBagLayout());

		this.main = main;
		this.chapter = chapter;

		new GeneralStorage(main.getContext()).setCurrentChapterNumber(chapter.getNumber());

		progressBar = new KWTProgressBar(0);
		progressBar.setCurrentTick(0);

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.weightx = 1.0;
		gc.weighty = 0.1;
		gc.insets = new Insets(0, 10, 2, 10);
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(progressBar, gc);

		mangaImage = new KImage(null, KImage.SCALE_TO_FIT, KImage.SCALE_TO_FIT);
		mangaImage.setFocusable(true);
		mangaImage.setEnabled(true);
		mangaImage.addKeyListener(this);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		add(mangaImage, gc);

		if (lastPageNumber != null)
			pageNumber = lastPageNumber.intValue();

		loadImage(pageNumber);
	}

	/**
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		mangaImage.requestFocus();
	}

	private void loadImage(final int pageNumber) {
		new GeneralStorage(main.getContext()).setCurrentPageNumber(Integer.toString(pageNumber));

		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading image...") {
			public void handleConnected() throws Exception {
				progressBar.setTotalTicks(chapter.getPageTotal());

				final URL imageURL = chapter.getPageImageURL(pageNumber);

				Main.logger.info("Fetching image content " + imageURL);

				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						if (imageURL != null) {
							final Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
							mangaImage.setImage(image, true);
							isPortrait = true;
						}

						progressBar.setCurrentTick(pageNumber);

						requestFocus();
						repaint();
					}
				});
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (!chapter.hasPages())
			return;

		switch (e.getKeyCode()) {
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE:
				if (pageNumber < chapter.getPageTotal())
					loadImage(++pageNumber);
				break;
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE_BACK:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE_BACK:
				if (pageNumber > 1)
					loadImage(--pageNumber);
				break;
			case KindleKeyCodes.VK_FIVE_WAY_SELECT:
				if (isPortrait) {
					KProgress progress = main.getContext().getProgressIndicator();
					progress.setString("Rotating image...");
					try {
						final Image rotatedImage = ImageUtil.getRotatedImage(mangaImage.getImage(), ImageUtil.ROTATE_RIGHT);
						if (rotatedImage != null) {
							mangaImage.setImage(rotatedImage, true);
							isPortrait = false;
							requestFocus();
							repaint();
						}
					} finally {
						progress.setIndeterminate(false);
					}
				}
				break;
			default:
				break;
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}

}
