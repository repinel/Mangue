package cc.pinel.mangue.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cc.pinel.mangue.model.Manga;

import com.amazon.kindle.kindlet.KindletContext;

public class MangaStorage {
	private static final String RES_DIR = "/res/";

	private static final String STORAGE_FILE = "mangas.json";

	private static final Logger logger = Logger.getLogger(MangaStorage.class);

	private final KindletContext context;

	public MangaStorage(KindletContext context) {
		this.context = context;
	}

	public Collection<Manga> loadMangaList() throws IOException, ParseException {
		Collection<Manga> mangas = new ArrayList<Manga>();

		InputStream is = getClass().getResourceAsStream(RES_DIR + STORAGE_FILE);

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(IOUtils.toString(is));
		JSONArray jsonMangas = (JSONArray) json.get("mangas");

		for (int i = 0; i < jsonMangas.size(); i++) {
			JSONObject jsonManga = (JSONObject) jsonMangas.get(i);
			mangas.add(new Manga(jsonManga.get("id").toString(), jsonManga.get("name").toString()));
		}

		logger.debug("mangas size: " + mangas.size());

		return mangas;
	}
}
