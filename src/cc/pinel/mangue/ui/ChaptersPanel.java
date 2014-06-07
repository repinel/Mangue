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
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class ChaptersPanel extends KPanel {
	private static final long serialVersionUID = 7836204925749827794L;

	private final Main main;

	private final Manga manga;

	private final KPages chaptersPages;

	private final ChapterLabelActionListener chapterListener;

	public ChaptersPanel(Main main, Manga manga) {
		super(new GridBagLayout());

		this.main = main;
		this.manga = manga;

		new GeneralStorage(main.getContext()).setCurrentMangaId(manga.getId());

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

		loadChapters();
	}

	/**
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		try {
			((Component) chaptersPages.getPageModel().getElementAt(0)).requestFocus();
		} catch (NoSuchElementException e) {
			chaptersPages.requestFocus();
		}
	}

	private void loadChapters() {
		new StorageHandler(main.getContext(), "Loading mangas...") {
			public void handleRun() throws Exception {
				final String lastChapterNumber = new StateStorage(main.getContext()) .getChapter(manga.getId());

				final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading chapters...") {
					public void handleConnected() throws Exception {
						Main.logger.info("Fetching chapters for " + manga.getName());

						JSONParser parser = new JSONParser();
						JSONArray chapters = (JSONArray) parser.parse(IOUtils.toString(new URL(
								manga.getSearchChaptersLink()).openStream()));

						Main.logger.debug("chapters size: " + chapters.size());

						for (int i = chapters.size() - 1; i >= 0; i--) {
							JSONObject chapter = (JSONObject) chapters.get(i);
							String chapterNumber = chapter.get("chapter").toString();
							String chapterName = StringUtils.unescapeHtml(chapter.get("chapter_name").toString());
							String chapterTitle = chapterNumber + (chapterName != null && chapterName.length() != 0 ? ": " + chapterName : "");

							final KWTSelectableLabel chapterLabel = new KWTSelectableLabel(chapterTitle);
							chapterLabel.setName(chapterNumber);
							chapterLabel.setFocusable(true);
							chapterLabel.setEnabled(true);
							chapterLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
							chapterLabel.addActionListener(chapterListener);

							// last read chapter
							if (lastChapterNumber != null && chapterNumber.equals(lastChapterNumber)) {
								Font font = chapterLabel.getFont();
								chapterLabel.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
								chapterLabel.setForeground(new Color(255, 84, 84));
							}

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

	private class ChapterLabelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (Integer.parseInt(event.getActionCommand()) == KindleKeyCodes.VK_FIVE_WAY_SELECT) {
				String chapterNumber = ((KWTSelectableLabel) event.getSource()).getName();

				Main.logger.debug("Selected chapter: " + chapterNumber);

				rememberChapter(chapterNumber);
				ViewPanel viewPanel = new ViewPanel(main, new Chapter(chapterNumber, manga.getChapterLink(chapterNumber)));
				main.setActivePanel(viewPanel);
			}
		}

		private void rememberChapter(final String chapterNumber) {
			new StorageHandler(main.getContext(), "Loading mangas...") {
				public void handleRun() throws Exception {
					new StateStorage(main.getContext()).setChapter(manga.getId(), chapterNumber);
				}
			}.start();
		}
	}
}
