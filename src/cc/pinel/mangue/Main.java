package cc.pinel.mangue;


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

import cc.pinel.mangue.ui.MainPanel;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.cowlark.kindlet.KindletWrapper;

public class Main extends KindletWrapper {
	private static final String RES_DIR = "/res/";

	private static final Logger logger = Logger.getLogger(Main.class);

	private MainPanel mainPanel;

	private List<Manga> mangas;

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
	}

	@Override
	public void onKindletStart() {
		KindletContext context = getContext();
		context.getRootContainer().add(mainPanel);
	}

	public void setActivePanel(KPanel panel) {
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
}
