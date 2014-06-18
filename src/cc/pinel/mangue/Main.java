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

/**
 * The main controller.
 * 
 * @author Roque Pinel
 *
 */
public class Main implements Kindlet {
	/**
	 * The possible values for the variable <code>state</code>.
	 */
	private static final int INACTIVE_STATE = -1;
	private static final int MAIN_STATE     =  0;
	private static final int CHAPTERS_STATE =  1;
	private static final int VIEW_STATE     =  2;

	/**
	 * The minimal number of characters that can be searched.
	 */
	private static final int MIN_CHARS_SEARCH = 3;

	private KindletContext context;

	private MainPanel mainPanel;
	private ChaptersPanel chaptersPanel;
	private ViewPanel viewPanel;
	private AddMangaPanel addMangaPanel;

	private int state = INACTIVE_STATE;

	// --- KDK Methods  ---

	/**
	 * {@inheritDoc}
	 */
	public void create(KindletContext context) {
		this.context = context;

		PropertyConfigurator.configure(getClass().getResource("/res/log4j.properties"));

		getContext().setMenu(new Menu(this));

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MainKeyEventDispatcher());
	}

	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	public void stop() {
		// ignored
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() {
		// ignored
	}

	/**
	 * @return the Kindlet Context
	 */
	public KindletContext getContext() {
		return this.context;
	}

	// --- States ---

	/**
	 * The kindlet is always inactive the first time it is started.
	 * 
	 * @return if it is inactive
	 */
	public boolean isInactive() {
		return this.state == INACTIVE_STATE;
	}

	/**
	 * @return if the main panel is being displayed
	 */
	public boolean isMainActive() {
		return this.state == MAIN_STATE;
	}

	/**
	 * @return if the chapters panel is being displayed
	 */
	public boolean isChaptersActive() {
		return this.state == CHAPTERS_STATE;
	}

	/**
	 * @return if the view panel is being displayed
	 */
	public boolean isViewActive() {
		return this.state == VIEW_STATE;
	}

	/**
	 * @return if the add manga panel is being displayed
	 */
	public boolean isAddActive() {
		return this.state > VIEW_STATE;
	}

	// --- Panels ---

	/**
	 * @return the chapters panel
	 */
	public ChaptersPanel getChaptersPanel() {
		return this.chaptersPanel;
	}

	/**
	 * @param chaptersPanel the chapters panel
	 */
	public void setChaptersPanel(ChaptersPanel chaptersPanel) {
		this.chaptersPanel = chaptersPanel;
	}

	/**
	 * @return the view panel
	 */
	public ViewPanel getViewPanel() {
		return this.viewPanel;
	}

	/**
	 * @param viewPanel the view panel
	 */
	public void setViewPanel(ViewPanel viewPanel) {
		this.viewPanel = viewPanel;
	}

	/**
	 * @return the add manga panel
	 */
	public AddMangaPanel getAddMangaPanel() {
		return this.addMangaPanel;
	}

	/**
	 * @param addMangaPanel the add manga panel
	 */
	public void setAddMangaPanel(AddMangaPanel addMangaPanel) {
		this.addMangaPanel = addMangaPanel;
	}

	// --- Paint Methods ---

	/**
	 * Set the state as main panel and paints it.
	 */
	public void paintMainPanel() {
		this.state = MAIN_STATE;
		paintActive();
	}

	/**
	 * Set the state as chapters panel and paints it.
	 */
	public void paintChaptersPanel() {
		this.state = CHAPTERS_STATE;
		paintActive();
	}

	/**
	 * Set the state as view panel and paints it.
	 */
	public void paintViewPanel() {
		this.state = VIEW_STATE;
		paintActive();
	}

	/**
	 * Set the state as add manga panel and paints it.
	 */
	public void paintAddMangaPanel() {
		if (!isAddActive())
			this.state += VIEW_STATE + 1;
		paintActive();
	}

	/**
	 * Paints the active panel based on the current state.
	 */
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

	/**
	 * Reloads the mangas and paints the main panel.
	 * 
	 * It creates a main panel if it does not exist.
	 */
	public void reloadMainPanel() {
		if (this.mainPanel == null)
			this.mainPanel = new MainPanel(this);
		paintMainPanel();
		this.mainPanel.loadMangas();
	}

	// --- Menu Actions ---

	/**
	 * Displays the search manga dialog.
	 */
	public void searchManga() {
		final KindletContext context = getContext();

		String term = new GeneralStorage(context).getSearchTerm();

		KOptionPane.showInputDialog(context.getRootContainer(), "Title (min " + MIN_CHARS_SEARCH + " char):  ", term == null ? "" : term,
				new KOptionPane.InputDialogListener() {
					public void onClose(String input) {
						if (input != null && input.length() >= MIN_CHARS_SEARCH) {
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

	/**
	 * Displays the clear all mangas confirmation message.
	 */
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

	/**
	 * Displays the clear previous searched term confirmation message.
	 */
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

	// --- Events ---

	/**
	 * Handles the main key events.
	 * 
	 * Basically, the back button.
	 * 
	 * @author Roque Pinel
	 *
	 */
	private class MainKeyEventDispatcher implements KeyEventDispatcher {
		/**
		 * {@inheritDoc}
		 */
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

	// --- Special Methods

	/**
	 * Loads the last view panel for the parameters provided
	 * 
	 * @param mangaId the manga id
	 * @param chapterNumber the chapter number
	 * @param pageNumber the page number
	 */
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

	/**
	 * Resquests the system Garbage Collection to run.
	 */
	public void requestGC() {
		System.gc();
	}
}
