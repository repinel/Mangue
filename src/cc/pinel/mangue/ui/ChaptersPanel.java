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

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.ConnectivityHandler;
import cc.pinel.mangue.handler.StorageHandler;
import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.storage.GeneralStorage;
import cc.pinel.mangue.storage.StateStorage;
import cc.pinel.mangue.util.StringUtils;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.LocationIterator;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

/**
 * The panel to displays a manga chapters.
 * 
 * @author Roque Pinel
 *
 */
public class ChaptersPanel extends KPanel {
	private static final long serialVersionUID = 7836204925749827794L;

	private final Main main;

	private final KPages chaptersPages;

	private final ChapterLabelActionListener chapterListener;

	private Manga manga = null;

	/**
	 * @param main the main controller
	 * @param manga the manga
	 */
	public ChaptersPanel(Main main, Manga manga) {
		super(new GridBagLayout());

		this.main = main;

		chaptersPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		chaptersPages.setFocusable(true);
		chaptersPages.setEnabled(true);
		chaptersPages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		chapterListener = new ChapterLabelActionListener();

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 20, 20, 20);
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;

		add(chaptersPages, gc);

		chaptersPages.first();

		loadChapters(manga);
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestFocus() {
		try {
			((Component) chaptersPages.getPageModel().getElementAt(0)).requestFocus();
		} catch (NoSuchElementException e) {
			chaptersPages.requestFocus();
		}
	}

	/**
	 * Loads all chapters from the given manga.
	 * 
	 * @param manga the manga
	 */
	public void loadChapters(final Manga manga) {
		if (this.manga != null && this.manga.getId().equals(manga.getId()))
			return;

		this.manga = manga;
		rememberManga();

		new StorageHandler(main.getContext(), "Loading mangas...") {
			/**
			 * {@inheritDoc}
			 */
			public void handleRun() throws Exception {
				final String lastChapterNumber = new StateStorage(main.getContext()) .getChapter(manga.getId());

				final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading chapters...") {
					/**
					 * {@inheritDoc}
					 */
					public void handleConnected() throws Exception {
						JSONParser parser = new JSONParser();
						JSONArray chapters = (JSONArray) parser.parse(IOUtils.toString(new URL(
								manga.getAllChaptersLink()).openStream()));

						chaptersPages.removeAllItems();
						for (int i = chapters.size() - 1; i >= 0; i--) {
							JSONObject chapter = (JSONObject) chapters.get(i);
							String chapterNumber = chapter.get("chapter").toString();
							String chapterName = StringUtils.unescapeHtml(chapter.get("chapter_name").toString());
							String chapterTitle = chapterNumber + (chapterName != null && chapterName.length() != 0 ? ": " + chapterName : "");

							final KWTSelectableLabel chapterLabel = new KWTSelectableLabel(chapterTitle);
							chapterLabel.setName(chapterNumber);
							chapterLabel.setFocusable(true);
							chapterLabel.setEnabled(true);
							chapterLabel.addActionListener(chapterListener);

							// last read chapter
							if (lastChapterNumber != null && chapterNumber.equals(lastChapterNumber))
								highlightLabel(chapterLabel);

							chaptersPages.addItem(chapterLabel);
						}

						EventQueue.invokeAndWait(new Runnable() {
							public void run() {
								chaptersPages.first();

								requestFocus();
								repaint();
							}
						});
					}
				};

				main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
			}
		}.start();
	}

	/**
	 * Updates the last chapter highlight when
	 * the user selects a new chapter to read.
	 * 
	 * @param lastChapterNumber the last chapter number
	 */
	private void updateLastChapter(String lastChapterNumber) {
		for (LocationIterator iter = chaptersPages.getPageModel().locationIterator(-1, true); iter.hasNext(); ) {
			final KWTSelectableLabel chapterLabel = (KWTSelectableLabel) iter.next();
			if (lastChapterNumber != null && chapterLabel.getName().equals(lastChapterNumber))
				highlightLabel(chapterLabel);
			else
				unhighlightLabel(chapterLabel);
		}
	}

	/**
	 * Handles the panel actions.
	 * 
	 * Basically, when a chapter is selected.
	 * 
	 * @author Roque Pinel
	 *
	 */
	private class ChapterLabelActionListener implements ActionListener {
		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent event) {
			if (Integer.parseInt(event.getActionCommand()) == KindleKeyCodes.VK_FIVE_WAY_SELECT) {
				String chapterNumber = ((KWTSelectableLabel) event.getSource()).getName();

				updateLastChapter(chapterNumber);
				rememberChapter(chapterNumber);

				Chapter chapter = new Chapter(chapterNumber, manga.getChapterLink(chapterNumber));
				ViewPanel viewPanel = main.getViewPanel();
				if (viewPanel == null)
					main.setViewPanel(new ViewPanel(main, chapter));
				else
					viewPanel.loadImage(chapter);
				main.paintViewPanel();
			}
		}

		/**
		 * Remembers the chapter to be able to
		 * restore it in the future.
		 * 
		 * @param chapterNumber the chapter number
		 */
		private void rememberChapter(final String chapterNumber) {
			new StorageHandler(main.getContext(), "Loading mangas...") {
				/**
				 * {@inheritDoc}
				 */
				public void handleRun() throws Exception {
					new StateStorage(main.getContext()).setChapter(manga.getId(), chapterNumber);
				}
			}.start();
		}
	}

	/**
	 * Remembers the current manga to be able to
	 * restore it in the future.
	 */
	private void rememberManga() {
		new GeneralStorage(main.getContext()).setCurrentMangaId(manga.getId());
	}

	/**
	 * Highlights the label.
	 * 
	 * @param label the chapter label
	 */
	private void highlightLabel(KLabel label) {
		Font font = label.getFont();
		label.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
		label.setForeground(new Color(255, 84, 84));
	}

	/**
	 * Unhighlights the label.
	 * 
	 * @param label the chapter label
	 */
	private void unhighlightLabel(KLabel label) {
		Font font = label.getFont();
		label.setFont(new Font(font.getFamily(), Font.PLAIN, font.getSize()));
		label.setForeground(Color.BLACK);
	}
}
