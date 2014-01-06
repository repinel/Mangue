package cc.pinel.mangue;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cc.pinel.mangue.model.Manga;
import cc.pinel.mangue.ui.ChaptersPanel;
import cc.pinel.mangue.ui.MainPanel;
import cc.pinel.mangue.ui.Menu;
import cc.pinel.mangue.ui.ViewPanel;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.cowlark.kindlet.KindletWrapper;

public class Main extends KindletWrapper {
	private static final String RES_DIR = "/res/";

	private static final Logger logger = Logger.getLogger(Main.class);

	private MainPanel mainPanel;
	private ChaptersPanel chaptersPanel;
	private ViewPanel viewPanel;

	private List<Manga> mangas;

	/**
	 * @see com.cowlark.kindlet.KindletWrapper#onKindletCreate()
	 */
	@Override
	public void onKindletCreate() {
		PropertyConfigurator.configure(getClass().getResource(RES_DIR + "log4j.properties"));

		logger.info("About to load the manga list.");
		try {
			loadMangaList(getClass().getResourceAsStream(RES_DIR + "mangas.json"));
		} catch (IOException e) {
			logger.error(e);
		} catch (ParseException e) {
			logger.error(e);
		}

		mainPanel = new MainPanel(this, mangas);

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
		setPanel(panel);
	}

	public void setActivePanel(ChaptersPanel panel) {
		this.chaptersPanel = panel;
		this.viewPanel = null;
		setPanel(panel);
	}

	public void setActivePanel(ViewPanel panel) {
		this.viewPanel = panel;
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

	private void loadMangaList(InputStream is) throws IOException, ParseException {
		this.mangas = new ArrayList<Manga>();

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(IOUtils.toString(is));
		JSONArray mangas = (JSONArray) json.get("mangas");

		for (int i = 0; i < mangas.size(); i++) {
			JSONObject manga = (JSONObject) mangas.get(i);
			this.mangas.add(new Manga(manga.get("id").toString(), manga.get("name").toString(), manga.get("first_chapter_url").toString()));
		}
	}

	private class MainKeyEventDispatcher implements KeyEventDispatcher {
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.isConsumed())
				return false;

			if (e.getKeyCode() == KindleKeyCodes.VK_BACK) {
				Component displayed = getContext().getRootContainer().getComponent(0);

				if (displayed == chaptersPanel) {
					setActivePanel(mainPanel);
				} else if (displayed == viewPanel) {
					setActivePanel(chaptersPanel);
				}

				return true; // for now, back just does not exit
			}

			return false;
		}
	}
}
