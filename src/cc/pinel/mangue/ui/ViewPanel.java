package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.model.Page;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.net.ConnectivityHandler;
import com.amazon.kindle.kindlet.net.NetworkDisabledDetails;
import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KPanel;

public class ViewPanel extends KPanel implements KeyListener {
	private static final long serialVersionUID = -2485604965935171736L;

	private static final Logger logger = Logger.getLogger(ViewPanel.class);

	private final Main main;

	private final KImage mangaImage;

	private List<Page> pages;

	private int pagesIndex = 0;

	public ViewPanel(Main main, Chapter chapter) {
		super(new GridBagLayout());

		this.main = main;

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 20, 20, 20);
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;

		mangaImage = new KImage(null, KImage.SCALE_TO_FIT, KImage.SCALE_TO_FIT);
		mangaImage.setFocusable(true);
		mangaImage.setEnabled(true);
		mangaImage.addKeyListener(this);

		loadPages(chapter);

		add(mangaImage, gc);
	}

	public void requestFocus() {
		mangaImage.requestFocus();
	}

	private void loadPages(final Chapter chapter) {
		final ConnectivityHandler handler = new ConnectivityHandler() {
			public void connected() {
				try {
					pages = chapter.getPages();

					if (!pages.isEmpty())
						loadImage(pages.get(pagesIndex));
				} catch (SAXException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error(e);
				}
			}

			public void disabled(NetworkDisabledDetails details) {
				logger.error("Connection disabled: " + details.getLocalizedMessage());
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	private void loadImage(final Page page) {
		final ConnectivityHandler handler = new ConnectivityHandler() {
			public void connected() {
				try {
					logger.info("Fetching image content " + page.getImageURL());

					Image image = Toolkit.getDefaultToolkit().getImage(page.getImageURL());
					mangaImage.setImage(image, true);

					requestFocus();
					repaint();
				} catch (SAXException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error(e);
				}
			}

			public void disabled(NetworkDisabledDetails details) {
				logger.error("Connection disabled: " + details.getLocalizedMessage());
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	public void keyReleased(KeyEvent e) {
		if (pages == null || pages.isEmpty())
			return;

		switch (e.getKeyCode()) {
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE:
				logger.debug("Key Released: turn page. " + pagesIndex);

				if (pagesIndex + 1 < pages.size()) {
					logger.debug("loading image");
					loadImage(pages.get(++pagesIndex));
				}
				break;
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE_BACK:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE_BACK:
				logger.debug("Key Released: turn page back. " + pagesIndex);

				if (pagesIndex - 1 >= 0) {
					logger.debug("loading image");
					loadImage(pages.get(--pagesIndex));
				}
				break;
			default:
				break;
		}
	}

	public void keyPressed(KeyEvent e) { }

	public void keyTyped(KeyEvent e) { }
	
}
