package cc.pinel.mangue;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cc.pinel.mangue.ui.AddMangaPanel;
import cc.pinel.mangue.ui.ChaptersPanel;
import cc.pinel.mangue.ui.MainPanel;
import cc.pinel.mangue.ui.Menu;
import cc.pinel.mangue.ui.ViewPanel;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.cowlark.kindlet.KindletWrapper;

public class Main extends KindletWrapper {
	private static final String RES_DIR = "/res/";

	private static final Logger logger = Logger.getLogger(Main.class);

	private MainPanel mainPanel;
	private ChaptersPanel chaptersPanel;
	private ViewPanel viewPanel;
	private AddMangaPanel addMangaPanel; 

	/**
	 * @see com.cowlark.kindlet.KindletWrapper#onKindletCreate()
	 */
	@Override
	public void onKindletCreate() {
		PropertyConfigurator.configure(getClass().getResource(RES_DIR + "log4j.properties"));

		logger.info("-- Kindle Create --");

		mainPanel = new MainPanel(this);

		getContext().setMenu(new Menu(this));

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MainKeyEventDispatcher());
	}

	/**
	 * @see com.cowlark.kindlet.KindletWrapper#onKindletStart()
	 */
	@Override
	public void onKindletStart() {
		KindletContext context = getContext();
		context.getRootContainer().add(mainPanel);
	}

	public void setActivePanel(MainPanel panel) {
		this.chaptersPanel = null;
		this.viewPanel = null;
		this.addMangaPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(ChaptersPanel panel) {
		this.chaptersPanel = panel;
		this.viewPanel = null;
		this.addMangaPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(ViewPanel panel) {
		this.viewPanel = panel;
		this.addMangaPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(AddMangaPanel panel) {
		this.addMangaPanel = panel;
		setPanel(panel);
	}

	private void setPanel(KPanel panel) {
		KindletContext context = getContext();

		context.getRootContainer().removeAll();
		context.getRootContainer().add(panel);

		panel.requestFocus();

		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

	public void reloadMainPanel() {
		setActivePanel(this.mainPanel);
		this.mainPanel.loadMangas();
	}

	public void searchManga() {
		KindletContext context = getContext();

		KOptionPane.showInputDialog(context.getRootContainer(), "Enter the Manga Title: ", "", new KOptionPane.InputDialogListener() {
			public void onClose(String input) {
				if (input != null) {
					AddMangaPanel addMangaPanel = new AddMangaPanel(Main.this, input);
					setActivePanel(addMangaPanel);
				}
			}
		});
	}

	private class MainKeyEventDispatcher implements KeyEventDispatcher {
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.isConsumed())
				return false;

			if (e.getKeyCode() == KindleKeyCodes.VK_BACK) {
				Component displayed = getContext().getRootContainer().getComponent(0);

				if (displayed == chaptersPanel)
					setActivePanel(mainPanel);
				else if (displayed == viewPanel)
					setActivePanel(chaptersPanel);
				else if (displayed == addMangaPanel) {
					if (viewPanel != null)
						setActivePanel(viewPanel);
					else if (chaptersPanel != null)
						setActivePanel(chaptersPanel);
					else
						setActivePanel(mainPanel);
				}

				return true; // for now, back just does not exit
			}

			return false;
		}
	}
}
