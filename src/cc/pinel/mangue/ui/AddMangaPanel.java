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
import cc.pinel.mangue.handler.ConnectivityHandler;
import cc.pinel.mangue.handler.StorageHandler;
import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.storage.MangaStorage;
import cc.pinel.mangue.util.MangaSearch;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

/**
 * The panel to add a new manga to the favorites list.
 * 
 * @author Roque Pinel
 *
 */
public class AddMangaPanel extends KPanel {
	private static final long serialVersionUID = -2469140435669501883L;

	private final Main main;

	private final KPages results;

	/**
	 * @param main the main controller
	 * @param input the input to be searched
	 */
	public AddMangaPanel(Main main, String input) {
		super(new GridBagLayout());

		this.main = main;

		results = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		results.setFocusable(true);
		results.setEnabled(true);
		results.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 20, 20, 20);
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;

		add(results, gc);

		results.first();

		loadResults(input);
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestFocus() {
		try {
			((Component) results.getPageModel().getElementAt(0)).requestFocus();
		} catch (NoSuchElementException e) {
			results.requestFocus();
		}
	}

	/**
	 * Loads all search results based on the given input.
	 * 
	 * @param input the input to be searched
	 */
	public void loadResults(final String input) {
		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading results...") {
			/**
			 * {@inheritDoc}
			 */
			public void handleConnected() throws Exception {
				final Collection mangas = MangaSearch.search(input);

				EventQueue.invokeAndWait(new Runnable() {
					/**
					 * {@inheritDoc}
					 */
					public void run() {
						results.removeAllItems();
						for (Iterator iter = mangas.iterator(); iter.hasNext(); ) {
							Manga manga = (Manga) iter.next();
							final KWTSelectableLabel resultLabel = new KWTSelectableLabel(manga.getName());
							resultLabel.setFocusable(true);
							resultLabel.setEnabled(true);
							resultLabel.addActionListener(new ResultLabelActionListener(manga));
							results.addItem(resultLabel);
						}

						results.first();

						requestFocus();
						repaint();
					}
				});
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	// --- Events ---

	/**
	 * Handles the panel actions.
	 * 
	 * Basically, when a manga is selected.
	 * 
	 * @author Roque Pinel
	 *
	 */
	private class ResultLabelActionListener implements ActionListener {
		private final Manga manga;

		/**
		 * @param manga the manga
		 */
		public ResultLabelActionListener(Manga manga) {
			this.manga = manga;
		}

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent event) {
			if (Integer.parseInt(event.getActionCommand()) == KindleKeyCodes.VK_FIVE_WAY_SELECT) {
				new StorageHandler(main.getContext(), "Loading mangas...") {
					public void handleRun() throws Exception {
						new MangaStorage(main.getContext()).addManga(manga);
						main.reloadMainPanel();
					}
				}.start();
			}
		}
	}
}
