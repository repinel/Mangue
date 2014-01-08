package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.StorageHandler;
import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.util.MangaStorage;

import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class MainPanel extends KPanel {
	private static final long serialVersionUID = -4692282056850151456L;

	private final Main main;

	private final KPages mangaListPages;

	public MainPanel(Main main) {
		super(new GridBagLayout());

		this.main = main;

		mangaListPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		mangaListPages.setFocusable(true);
		mangaListPages.setEnabled(true);
		mangaListPages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		loadMangas();

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
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		if (mangaListPages.getComponents().length > 0)
			mangaListPages.getComponent(0).requestFocus();
		else
			mangaListPages.requestFocus();
	}

	public void loadMangas() {
		new StorageHandler(main.getContext(), "Loading mangas...") {
			@Override
			public void handleRun() throws Exception {
				Collection<Manga> mangas = new MangaStorage(main.getContext()).getMangas();

				mangaListPages.removeAllItems();

				for (Manga manga : mangas) {
					final KWTSelectableLabel mangaLabel = new KWTSelectableLabel(manga.getName());
					mangaLabel.setFocusable(true);
					mangaLabel.setEnabled(true);
					mangaLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
					mangaLabel.addActionListener(new MangaLabelActionListener(manga));
					mangaListPages.addItem(mangaLabel);
				}

				mangaListPages.firePageModelUpdates();

				mangaListPages.first();

				requestFocus();
				repaint();
			}
		}.start();
	}

	private class MangaLabelActionListener implements ActionListener {
		private final Manga manga;

		public MangaLabelActionListener(Manga manga) {
			this.manga = manga;
		}

		public void actionPerformed(ActionEvent event) {
			ChaptersPanel chaptersPanel = new ChaptersPanel(main, manga);
			main.setActivePanel(chaptersPanel);
		}
	}
}
