package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.kwt.ui.KWTProgressBar;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.handler.ConnectivityHandler;
import cc.pinel.mangue.model.Chapter;
import cc.pinel.mangue.model.Page;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KPanel;

public class ViewPanel extends KPanel implements KeyListener {
	private static final long serialVersionUID = -2485604965935171736L;

	private static final Logger logger = Logger.getLogger(ViewPanel.class);

	private final Main main;

	private final KImage mangaImage;

	private final KWTProgressBar progressBar;

	private List<Page> pages;

	private int pagesIndex = 0;

	public ViewPanel(Main main, Chapter chapter) {
		super(new GridBagLayout());

		this.main = main;

		progressBar = new KWTProgressBar();
		progressBar.setLabelStyle(KWTProgressBar.STYLE_NONE);

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.weightx = 1.0;
		gc.weighty = 0.1;
		gc.insets = new Insets(0, 10, 10, 10);
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(progressBar, gc);

		mangaImage = new KImage(null, KImage.SCALE_TO_FIT, KImage.SCALE_TO_FIT);
		mangaImage.setFocusable(true);
		mangaImage.setEnabled(true);
		mangaImage.addKeyListener(this);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		add(mangaImage, gc);

		loadPages(chapter);
	}

	/**
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		mangaImage.requestFocus();
	}

	private void loadPages(final Chapter chapter) {
		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading pages...") {
			@Override
			public void handleConnected() throws Exception {
				pages = chapter.getPages();

				progressBar.setCurrentTick(0);
				progressBar.setTotalTicks(pages.size());

				if (!pages.isEmpty())
					loadImage(pages.get(pagesIndex));
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	private void loadImage(final Page page) {
		final ConnectivityHandler handler = new ConnectivityHandler(main.getContext(), "Loading image...") {
			@Override
			public void handleConnected() throws Exception {
				logger.info("Fetching image content " + page.getImageURL());

				Image image = Toolkit.getDefaultToolkit().getImage(page.getImageURL());
				mangaImage.setImage(image, true);

				progressBar.setCurrentTick(pagesIndex + 1);

				requestFocus();
				repaint();
			}
		};

		main.getContext().getConnectivity().submitSingleAttemptConnectivityRequest(handler, true);
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (pages == null || pages.isEmpty())
			return;

		switch (e.getKeyCode()) {
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE:
				if (pagesIndex + 1 < pages.size())
					loadImage(pages.get(++pagesIndex));
				break;
			case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE_BACK:
			case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE_BACK:
				if (pagesIndex - 1 >= 0)
					loadImage(pages.get(--pagesIndex));
				break;
			default:
				break;
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) { }

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) { }
	
}
