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
package cc.pinel.mangue;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.handler.StorageHandler;
import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.storage.GeneralStorage;
import cc.pinel.mangue.storage.MangaStorage;
import cc.pinel.mangue.storage.StateStorage;
import cc.pinel.mangue.ui.AddMangaPanel;
import cc.pinel.mangue.ui.ChaptersPanel;
import cc.pinel.mangue.ui.MainPanel;
import cc.pinel.mangue.ui.Menu;
import cc.pinel.mangue.ui.ViewPanel;

import com.amazon.kindle.kindlet.Kindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import com.amazon.kindle.kindlet.ui.KPanel;

public class Main implements Kindlet {
	public static final Logger logger = Logger.getLogger(Main.class);

	private KindletContext context;

	private MainPanel mainPanel;
	private ChaptersPanel chaptersPanel;
	private ViewPanel viewPanel;
	private AddMangaPanel addMangaPanel;

	public KindletContext getContext() {
		return this.context;
	}

	public void create(KindletContext context) {
		this.context = context;

		PropertyConfigurator.configure(getClass().getResource("/res/log4j.properties"));

		logger.info("-- Kindle Create --");

		mainPanel = new MainPanel(this);

		getContext().setMenu(new Menu(this));

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MainKeyEventDispatcher());
	}

	public void start() {
		KindletContext context = getContext();

		KPanel currentPanel = this.mainPanel;

		GeneralStorage generalStorage = new GeneralStorage(context);
		String pageNumber = generalStorage.getCurrentPageNumber();
		if (pageNumber != null) {
			String mangaId = generalStorage.getCurrentMangaId();
			String chapterNumber = generalStorage.getCurrentChapterNumber();
			if (mangaId != null && chapterNumber != null) {
				logger.info("Last viewed - manga: " + mangaId + " - chapter: " + chapterNumber + " - page: " + pageNumber);
				if (loadLastViewed(mangaId, chapterNumber, pageNumber))
					currentPanel = this.viewPanel;
			}
		}

		context.getRootContainer().add(currentPanel);
	}


	public void stop() {
		// ignored
	}

	public void destroy() {
		// ignored
	}

	public void setActivePanel(MainPanel panel) {
		this.chaptersPanel = null;
		this.viewPanel = null;
		this.addMangaPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(ChaptersPanel panel) {
		this.chaptersPanel = panel;
		this.viewPanel = null;
		this.addMangaPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(ViewPanel panel) {
		this.viewPanel = panel;
		this.addMangaPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(AddMangaPanel panel) {
		this.addMangaPanel = panel;
		setPanel(panel);
	}

	private void setPanel(KPanel panel) {
		KindletContext context = getContext();

		context.getRootContainer().removeAll();
		context.getRootContainer().add(panel);

		panel.requestFocus();

		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

	public void reloadMainPanel() {
		setActivePanel(this.mainPanel);
		this.mainPanel.loadMangas();
	}

	public void searchManga() {
		final KindletContext context = getContext();

		String term = new GeneralStorage(context).getSearchTerm();

		KOptionPane.showInputDialog(context.getRootContainer(), "Title (min 3 chars):  ",
				term == null ? "" : term, new KOptionPane.InputDialogListener() {
					public void onClose(String input) {
						if (input != null && input.length() >= 3) {
							new GeneralStorage(context).setSearchTerm(input);
							AddMangaPanel addMangaPanel = new AddMangaPanel(Main.this, input);
							setActivePanel(addMangaPanel);
						}
					}
				});
	}

	public void clearMangas() {
		final KindletContext context = getContext();

		KOptionPane.showConfirmDialog(context.getRootContainer(),
				"Would you really like to clear your favorites?",
				new KOptionPane.ConfirmDialogListener() {
					public void onClose(int option) {
						if (option == KOptionPane.OK_OPTION) {
							new StorageHandler(context, "Clearing Favorites...") {
								public void handleRun() throws Exception {
									new MangaStorage(context).clear();
									new StateStorage(context).clear();
									reloadMainPanel();
								}
							}.start();
						}
					}
				});
	}

	public void clearSearch() {
		final KindletContext context = getContext();

		KOptionPane.showConfirmDialog(context.getRootContainer(),
				"Would you really like to clear your previous searched term?",
				new KOptionPane.ConfirmDialogListener() {
					public void onClose(int option) {
						if (option == KOptionPane.OK_OPTION)
							new GeneralStorage(context).removeSearchTerm();
					}
				});
	}

	private class MainKeyEventDispatcher implements KeyEventDispatcher {
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.isConsumed())
				return false;

			if (e.getKeyCode() == KindleKeyCodes.VK_BACK) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					Component displayed = getContext().getRootContainer().getComponent(0);

					if (displayed == chaptersPanel) {
						new GeneralStorage(getContext()).removeCurrentChapterNumber();
						setActivePanel(mainPanel);
						requestGC();
					} else if (displayed == viewPanel) {
						new GeneralStorage(getContext()).removeCurrentPageNumber();
						setActivePanel(chaptersPanel);
						requestGC();
					} else if (displayed == addMangaPanel) {
						if (viewPanel != null)
							setActivePanel(viewPanel);
						else if (chaptersPanel != null)
							setActivePanel(chaptersPanel);
						else
							setActivePanel(mainPanel);
						requestGC();
					}
				}
				e.consume();
				return true;
			} else if (e.getSource() instanceof KWTSelectableLabel) {
				KWTSelectableLabel label = (KWTSelectableLabel) e.getSource();
				label.processEvent(e);
			}

			return false;
		}
	}

	private boolean loadLastViewed(String mangaId, String chapterNumber, String pageNumber) {
		final Manga manga = new MangaStorage(getContext()).getManga(mangaId);

		if (manga != null) {
			this.chaptersPanel = new ChaptersPanel(this, manga);
			this.viewPanel = new ViewPanel(this, new Chapter(chapterNumber,
					manga.getChapterLink(chapterNumber)), new Integer(pageNumber));
			return true;
		}

		return false;
	}

	public void requestGC() {
		System.gc();
	}
}
