package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Chapter;
import cc.pinel.mangue.Main;
import cc.pinel.mangue.Manga;

import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class ChaptersPanel extends KPanel {
	private static final long serialVersionUID = 7836204925749827794L;

	private final Main main;

	private final KPages chaptersPages;

	public ChaptersPanel(Main main, Manga manga) {
		super(new GridBagLayout());

		this.main = main;

		chaptersPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		chaptersPages.setFocusable(true);
		chaptersPages.setEnabled(true);
		chaptersPages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		for (Chapter chapter : manga.getChapters()) {
			final KWTSelectableLabel chapterLabel = new KWTSelectableLabel(chapter.getNumber());
			chapterLabel.setFocusable(true);
			chapterLabel.setEnabled(true);
			chapterLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
			chapterLabel.addActionListener(new ChapterLabelActionListener(chapter));
			chaptersPages.addItem(chapterLabel);
		}

		if (chaptersPages.getComponents().length > 0)
			chaptersPages.getComponent(0).requestFocus();

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

	private class ChapterLabelActionListener implements ActionListener {
		private final Chapter chapter;

		public ChapterLabelActionListener(Chapter chapter) {
			this.chapter = chapter;
		}

		public void actionPerformed(ActionEvent event) {
			// TODO: set the view panel
		}
	}
}
