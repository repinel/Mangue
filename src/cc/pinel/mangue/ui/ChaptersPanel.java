package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.ConnectivityHandler;
import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.model.Manga;

import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class ChaptersPanel extends KPanel {
	private static final long serialVersionUID = 7836204925749827794L;

	private final Main main;

	private final Manga manga;

	private final KPages chaptersPages;

	public ChaptersPanel(Main main, Manga manga) {
		super(new GridBagLayout());

		this.main = main;
		this.manga = manga;

		chaptersPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		chaptersPages.setFocusable(true);
		chaptersPages.setEnabled(true);
		chaptersPages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		loadChapters();

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
	}

	/**
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		if (chaptersPages.getComponents().length > 0)
			chaptersPages.getComponent(0).requestFocus();
		else
			chaptersPages.requestFocus();
	}

	private void loadChapters() {
		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading chapters...") {
			@Override
			public void handleConnected() throws Exception {
				for (Chapter chapter : manga.getChapters()) {
					final KWTSelectableLabel chapterLabel = new KWTSelectableLabel(chapter.getTitle());
					chapterLabel.setFocusable(true);
					chapterLabel.setEnabled(true);
					chapterLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
					chapterLabel.addActionListener(new ChapterLabelActionListener(chapter));
					chaptersPages.addItem(chapterLabel);
				}
				chaptersPages.first();

				requestFocus();
				repaint();	
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	private class ChapterLabelActionListener implements ActionListener {
		private final Chapter chapter;

		public ChapterLabelActionListener(Chapter chapter) {
			this.chapter = chapter;
		}

		public void actionPerformed(ActionEvent event) {
			ViewPanel viewPanel = new ViewPanel(main, chapter);
			main.setActivePanel(viewPanel);
		}
	}
}
