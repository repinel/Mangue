package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import cc.pinel.mangue.Chapter;
import cc.pinel.mangue.Main;
import cc.pinel.mangue.Page;

import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class ViewPanel extends KPanel {
	private static final long serialVersionUID = -2485604965935171736L;

	private final Main main;

	private final KPages pages;

	public ViewPanel(Main main, Chapter chapter) {
		super(new GridBagLayout());

		this.main = main;

		pages = new KPages(PageProviders.createFullPageProvider());
		pages.setFocusable(false);
		pages.setEnabled(true);
		pages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		for (Page page : chapter.getPages()) {
			final KImage image = new KImage(page.getImage());
			pages.addItem(image);
			break;
		}

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 20, 20, 20);
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		add(pages, gc);

		pages.first();
		pages.requestFocus();
	}
}
