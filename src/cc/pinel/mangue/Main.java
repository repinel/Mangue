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

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

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
	private static final int INACTIVE_STATE = -1;
	private static final int MAIN_STATE     = 0;
	private static final int CHAPTERS_STATE = 1;
	private static final int VIEW_STATE     = 2;

	private KindletContext context;

	private MainPanel mainPanel;
	private ChaptersPanel chaptersPanel;
	private ViewPanel viewPanel;
	private AddMangaPanel addMangaPanel;

	private int state = INACTIVE_STATE;

	/**
	 * @see com.amazon.kindle.kindlet.Kindlet#create(com.amazon.kindle.kindlet.KindletContext)
	 */
	public void create(KindletContext context) {
		this.context = context;

		PropertyConfigurator.configure(getClass().getResource("/res/log4j.properties"));

		getContext().setMenu(new Menu(this));

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MainKeyEventDispatcher());
	}

	/**
	 * @see com.amazon.kindle.kindlet.Kindlet#start()
	 */
	public void start() {
		if (!isViewActive()) {
			GeneralStorage generalStorage = new GeneralStorage(context);
			String pageNumber = generalStorage.getCurrentPageNumber();
			if (pageNumber != null) {
				String mangaId = generalStorage.getCurrentMangaId();
				String chapterNumber = generalStorage.getCurrentChapterNumber();
				if (mangaId != null && chapterNumber != null)
					loadLastViewed(mangaId, chapterNumber, pageNumber);
			}
		}

		if (isInactive()) {
			this.mainPanel = new MainPanel(this);
			this.mainPanel.loadMangas();
			this.state = MAIN_STATE;
		}

		paintActive();
	}

	/**
	 * @see com.amazon.kindle.kindlet.Kindlet#stop()
	 */
	public void stop() {
		// ignored
	}

	/**
	 * @see com.amazon.kindle.kindlet.Kindlet#destroy()
	 */
	public void destroy() {
		// ignored
	}

	public KindletContext getContext() {
		return this.context;
	}

	public boolean isInactive() {
		return this.state == INACTIVE_STATE;
	}

	public boolean isMainActive() {
		return this.state == MAIN_STATE;
	}

	public boolean isChaptersActive() {
		return this.state == CHAPTERS_STATE;
	}

	public boolean isViewActive() {
		return this.state == VIEW_STATE;
	}

	public boolean isAddActive() {
		return this.state > VIEW_STATE;
	}

	public ChaptersPanel getChaptersPanel() {
		return this.chaptersPanel;
	}

	public void setChaptersPanel(ChaptersPanel chaptersPanel) {
		this.chaptersPanel = chaptersPanel;
	}

	public ViewPanel getViewPanel() {
		return this.viewPanel;
	}

	public void setViewPanel(ViewPanel viewPanel) {
		this.viewPanel = viewPanel;
	}

	public AddMangaPanel getAddMangaPanel() {
		return this.addMangaPanel;
	}

	public void setAddMangaPanel(AddMangaPanel addMangaPanel) {
		this.addMangaPanel = addMangaPanel;
	}

	public void paintMainPanel() {
		this.state = MAIN_STATE;
		paintActive();
	}

	public void paintChaptersPanel() {
		this.state = CHAPTERS_STATE;
		paintActive();
	}

	public void paintViewPanel() {
		this.state = VIEW_STATE;
		paintActive();
	}

	public void paintAddMangaPanel() {
		if (!isAddActive())
			this.state += VIEW_STATE + 1;
		paintActive();
	}

	public void paintActive() {
		KPanel panel;
		if (isViewActive())
			panel = this.viewPanel;
		else if (isChaptersActive())
			panel = this.chaptersPanel;
		else if (isAddActive())
			panel = this.addMangaPanel;
		else
			panel = this.mainPanel;

		context.getRootContainer().removeAll();
		context.getRootContainer().add(panel);

		panel.requestFocus();

		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

	public void reloadMainPanel() {
		if (this.mainPanel == null)
			this.mainPanel = new MainPanel(this);
		paintMainPanel();
		this.mainPanel.loadMangas();
	}

	public void searchManga() {
		final KindletContext context = getContext();

		String term = new GeneralStorage(context).getSearchTerm();

		KOptionPane.showInputDialog(context.getRootContainer(), "Title (min 3 chars):  ", term == null ? "" : term,
				new KOptionPane.InputDialogListener() {
					public void onClose(String input) {
						if (input != null && input.length() >= 3) {
							new GeneralStorage(context).setSearchTerm(input);
							AddMangaPanel addMangaPanel = getAddMangaPanel();
							if (addMangaPanel == null)
								setAddMangaPanel(new AddMangaPanel(Main.this, input));
							else
								addMangaPanel.loadResults(input);
							paintAddMangaPanel();
						}
					}
				});
	}

	public void clearMangas() {
		final KindletContext context = getContext();

		KOptionPane.showConfirmDialog(context.getRootContainer(), "Would you really like to clear your favorites?",
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
				"Would you really like to clear your previous searched term?", new KOptionPane.ConfirmDialogListener() {
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
					if (isChaptersActive()) {
						new GeneralStorage(getContext()).removeCurrentChapterNumber();

						if (Main.this.mainPanel == null)
							Main.this.mainPanel = new MainPanel(Main.this);
						paintMainPanel();
						Main.this.mainPanel.loadMangas();
						requestGC();
					} else if (isViewActive()) {
						GeneralStorage generalStorage = new GeneralStorage(getContext());
						generalStorage.removeCurrentPageNumber();

						if (getChaptersPanel() == null) {
							final String mangaId = generalStorage.getCurrentMangaId();

							final Manga manga = new MangaStorage(getContext()).getManga(mangaId);
							if (manga != null)
								setChaptersPanel(new ChaptersPanel(Main.this, manga));
						}
						paintChaptersPanel();
						requestGC();
					} else if (isAddActive()) {
						Main.this.state -= Main.VIEW_STATE + 1;
						paintActive();
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

	private void loadLastViewed(String mangaId, String chapterNumber, String pageNumber) {
		final Manga manga = new MangaStorage(getContext()).getManga(mangaId);

		if (manga != null) {
			Chapter chapter = new Chapter(chapterNumber, manga.getChapterLink(chapterNumber));

			ViewPanel viewPanel = getViewPanel();
			if (viewPanel == null)
				setViewPanel(new ViewPanel(this, chapter, new Integer(pageNumber)));
			else
				viewPanel.loadImage(chapter);
			this.state = VIEW_STATE;
		}
	}

	public void requestGC() {
		System.gc();
	}
}
