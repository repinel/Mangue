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

/**
 * The panel to displays a chapter page.
 * 
 * @author Roque Pinel
 *
 */
public class ViewPanel extends KPanel implements KeyListener {
	private static final long serialVersionUID = -2485604965935171736L;

	private final Main main;

	private final KImage mangaImage;

	private final KWTProgressBar progressBar;

	private int pageNumber = 1;

	private boolean isPortrait = true;

	private Chapter chapter;

	/**
	 * @param main the main controller
	 * @param chapter the chapter
	 */
	public ViewPanel(Main main, Chapter chapter) {
		this(main, chapter, null);
	}

	/**
	 * @param main the main controller
	 * @param chapter the chapter
	 * @param lastPageNumber the page number to be displayed
	 */
	public ViewPanel(Main main, Chapter chapter, Integer lastPageNumber) {
		super(new GridBagLayout());

		this.main = main;

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

		if (lastPageNumber == null)
			loadImage(chapter);
		else
			loadImage(chapter, lastPageNumber.intValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestFocus() {
		mangaImage.requestFocus();
	}

	/**
	 * Loads the first page of the given chapter.
	 * 
	 * @param chapter the chapter
	 */
	public void loadImage(final Chapter chapter) {
		loadImage(chapter, 1); // first page
	}

	/**
	 * Loads the page number from the given chapter.
	 * 
	 * @param chapter the chapter
	 * @param number the page number
	 */
	public void loadImage(final Chapter chapter, final int number) {
		this.chapter = chapter;
		this.pageNumber = number;

		rememberChapter();
		rememberPage(number);

		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading image...") {
			/**
			 * {@inheritDoc}
			 */
			public void handleConnected() throws Exception {
				progressBar.setTotalTicks(chapter.getPageTotal());

				final URL imageURL = chapter.getPageImageURL(number);

				EventQueue.invokeAndWait(new Runnable() {
					/**
					 * {@inheritDoc}
					 */
					public void run() {
						if (imageURL != null) {
							final Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
							mangaImage.setImage(image, true);
							isPortrait = true;
						}

						progressBar.setCurrentTick(number);

						requestFocus();
						repaint();
					}
				});
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void keyReleased(KeyEvent e) {
		if (!chapter.hasPages())
			return;

		switch (e.getKeyCode()) {
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE:
				if (pageNumber < chapter.getPageTotal())
					loadImage(chapter, ++pageNumber);
				break;
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE_BACK:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE_BACK:
				if (pageNumber > 1)
					loadImage(chapter, --pageNumber);
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
	 * {@inheritDoc}
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Remembers the current chapter to be able to
	 * restore it in the future.
	 */
	private void rememberChapter() {
		new GeneralStorage(main.getContext()).setCurrentChapterNumber(chapter.getNumber());
	}

	/**
	 * Remembers the page number to be able to
	 * restore it in the future.
	 * 
	 * @param number the page number
	 */
	private void rememberPage(int number) {
		new GeneralStorage(main.getContext()).setCurrentPageNumber(Integer.toString(number));
	}
}
