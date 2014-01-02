package cc.pinel.mangue;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.pinel.mangue.ui.MainPanel;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.cowlark.kindlet.KindletWrapper;

public class Main extends KindletWrapper {
	private static final String RES_DIR = "/res/";

	private MainPanel mainPanel;

	private List<Manga> mangas;

	@Override
	public void onKindletCreate() {
		try {
			loadMangaList(getClass().getResourceAsStream(RES_DIR + "mangas.json"));
		} catch (IOException e) {
			e.printStackTrace();
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

		context.getRootContainer().remove(0);
		context.getRootContainer().add(panel);

		panel.requestFocus();

		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

	private void loadMangaList(InputStream is) throws IOException {
		this.mangas = new ArrayList<Manga>();

		JSONObject json = new JSONObject(IOUtils.toString(is));

		JSONArray mangas = (JSONArray) json.get("mangas");

		for (int i = 0; i < mangas.length(); i++) {
			JSONObject manga = (JSONObject) mangas.get(i);
			this.mangas.add(new Manga(manga.get("id").toString(), manga.get("name").toString(), manga.get("first_chapter_url").toString()));
		}
	}
}
