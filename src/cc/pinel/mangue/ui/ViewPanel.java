package cc.pinel.mangue.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.net.URL;

import org.apache.log4j.Logger;

import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.model.Page;

import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.ComponentProvider;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class ViewPanel extends KPanel {
	private static final long serialVersionUID = -2485604965935171736L;

	private static final Logger logger = Logger.getLogger(ViewPanel.class);

	private final KPages pages;

	private KImage mangaImage;

	// help caching the last image fetched
	private URL mangaImageURL;

	public ViewPanel(Chapter chapter) {
		super(new GridBagLayout());

		pages = new KPages(PageProviders.createFullPageProvider(new ImageProvider()));
		pages.setFocusable(false);
		pages.setEnabled(true);
		pages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		for (Page page : chapter.getPages())
			pages.addItem(page);

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
	}

	public void requestFocus() {
		if (pages.getComponents().length > 0)
			pages.getComponent(0).requestFocus();
		else
			super.requestFocus();
	}

	private class ImageProvider implements ComponentProvider {

		public Component getComponent(Object object) {
			Page page = (Page) object;

			if (mangaImageURL == null || !mangaImageURL.equals(page.getImageURL())) {
				logger.info("Fetching image content " + page.getImageURL());

				Image image = Toolkit.getDefaultToolkit().getImage(page.getImageURL());
				mangaImage = new KImage(image, KImage.SCALE_TO_FIT, KImage.SCALE_TO_FIT);

				mangaImageURL = page.getImageURL();
			}

			return mangaImage;
		}
	}
}
