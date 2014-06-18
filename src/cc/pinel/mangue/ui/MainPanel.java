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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.StorageHandler;
import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.storage.MangaStorage;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

/**
 * The panel to displays the manga favorites.
 * 
 * @author Roque Pinel
 *
 */
public class MainPanel extends KPanel {
	private static final long serialVersionUID = -4692282056850151456L;

	private final Main main;

	private final KPages mangaListPages;

	/**
	 * @param main the main controller
	 */
	public MainPanel(Main main) {
		super(new GridBagLayout());

		this.main = main;

		mangaListPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		mangaListPages.setFocusable(true);
		mangaListPages.setEnabled(true);
		mangaListPages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		mangaListPages.addItem(defaultItem());

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 20, 20, 20);
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;

		add(mangaListPages, gc);

		mangaListPages.first();
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestFocus() {
		try {
			((Component) mangaListPages.getPageModel().getElementAt(0)).requestFocus();
		} catch (NoSuchElementException e) {
			mangaListPages.requestFocus();
		}
	}

	/**
	 * Loads all favorites mangas.
	 */
	public void loadMangas() {
		new StorageHandler(main.getContext(), "Loading mangas...") {
			/**
			 * {@inheritDoc}
			 */
			public void handleRun() throws Exception {
				final Collection mangas = new MangaStorage(main.getContext()).getMangas();

				EventQueue.invokeAndWait(new Runnable() {
					/**
					 * {@inheritDoc}
					 */
					public void run() {
						mangaListPages.removeAllItems();

						if (mangas.size() == 0) {
							mangaListPages.addItem(defaultItem());
						}

						for (Iterator iter = mangas.iterator(); iter.hasNext(); ) {
							Manga manga = (Manga) iter.next();
							final KWTSelectableLabel mangaLabel = new KWTSelectableLabel(manga.getName());
							mangaLabel.setFocusable(true);
							mangaLabel.setEnabled(true);
							mangaLabel.addActionListener(new MangaLabelActionListener(manga));
							mangaListPages.addItem(mangaLabel);
						}

						mangaListPages.firePageModelUpdates();

						mangaListPages.first();

						requestFocus();
						repaint();
					}
				});
			}
		}.start();
	}

	/**
	 * Handles the panel actions. 
	 * 
	 * Basically, when a manga is selected.
	 * 
	 * @author Roque Pinel
	 *
	 */
	private class MangaLabelActionListener implements ActionListener {
		private final Manga manga;

		public MangaLabelActionListener(Manga manga) {
			this.manga = manga;
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent event) {
			switch (Integer.parseInt(event.getActionCommand())) {
				case KindleKeyCodes.VK_FIVE_WAY_SELECT:
					ChaptersPanel chaptersPanel = main.getChaptersPanel();
					if (chaptersPanel == null)
						main.setChaptersPanel(new ChaptersPanel(main, manga));
					else
						chaptersPanel.loadChapters(manga);
					main.paintChaptersPanel();
					break;
				case KindleKeyCodes.VK_FIVE_WAY_LEFT:
					KOptionPane.showConfirmDialog(main.getContext().getRootContainer(), "Would you like to remove "
							+ manga.getName() + "?", new KOptionPane.ConfirmDialogListener() {
						public void onClose(int option) {
							if (option == KOptionPane.OK_OPTION) {
								new StorageHandler(main.getContext(), "Removing manga...") {
									public void handleRun() throws Exception {
										new MangaStorage(main.getContext()).removeManga(manga);
										loadMangas();
									}
								}.start();
							}
						}
					});
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Returns the default item with a welcome message,
	 * copyright and instructions.
	 * 
	 * @return the default item to be displayed.
	 */
	private Object defaultItem() {
		return new KLabelMultiline("Welcome to Mangue - Online Manga Reader.\n"
								 + "Copyright \u00A92014 Roque Pinel.\n\n"
								 + "Use the menu option to search and add mangas to your list.\n\n"
								 + "All mangas available here are loaded from the www.mangapanda.com website.\n"
								 + "Mangue is not affiliated with Manga Panda.\n");
	}
}
