package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.ConnectivityHandler;
import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.util.MangaSearch;

import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class AddMangaPanel extends KPanel {
	private static final long serialVersionUID = -2469140435669501883L;

	private static final Logger logger = Logger.getLogger(AddMangaPanel.class);

	private final Main main;

	private final KPages results;

	public AddMangaPanel(Main main, String input) {
		super(new GridBagLayout());

		this.main = main;

		results = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		results.setFocusable(true);
		results.setEnabled(true);
		results.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		loadResults(input);

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
	}

	/**
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		if (results.getComponents().length > 0)
			results.getComponent(0).requestFocus();
		else
			results.requestFocus();
	}

	private void loadResults(final String input) {
		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading results...") {
			@Override
			public void handleConnected() throws Exception {
				Collection<Manga> mangas = MangaSearch.search(input);

				for (Manga manga : mangas) {
					final KWTSelectableLabel resultLabel = new KWTSelectableLabel(manga.getName());
					resultLabel.setFocusable(true);
					resultLabel.setEnabled(true);
					resultLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
					resultLabel.addActionListener(new ResultLabelActionListener(manga));
					results.addItem(resultLabel);
				}

				results.first();

				requestFocus();
				repaint();	
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	private class ResultLabelActionListener implements ActionListener {
		private final Manga manga;

		public ResultLabelActionListener(Manga manga) {
			this.manga = manga;
		}

		public void actionPerformed(ActionEvent event) {
			logger.debug("Selected Manga: " + manga.getName());
		}
	}
}
