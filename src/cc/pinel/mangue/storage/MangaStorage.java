package cc.pinel.mangue.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cc.pinel.mangue.model.Manga;

import com.amazon.kindle.kindlet.KindletContext;

public class MangaStorage extends AbstractStorage {
	private static final String STORAGE_FILE = "mangas.json";

	private static final Logger logger = Logger.getLogger(MangaStorage.class);

	public MangaStorage(KindletContext context) {
		super(context, STORAGE_FILE);
	}

	public Collection<Manga> getMangas() {
		Collection<Manga> mangas = new ArrayList<Manga>();

		JSONArray jsonMangas = null;
		try {
			jsonMangas = (JSONArray) readJSON().get("mangas");
		} catch (Exception e) {
			// ignored
		}
		if (jsonMangas == null)
			jsonMangas = new JSONArray();

		for (int i = 0; i < jsonMangas.size(); i++) {
			JSONObject jsonManga = (JSONObject) jsonMangas.get(i);
			mangas.add(new Manga(jsonManga.get("id").toString(), jsonManga.get("name").toString()));
		}

		logger.debug("mangas size: " + mangas.size());

		return mangas;
	}

	@SuppressWarnings("unchecked")
	public void addManga(Manga manga) throws IOException {
		JSONObject json = null;

		try {
			json = readJSON();
		} catch (Exception e) {
			// ignored
		}
		if (json == null)
			json = new JSONObject();

		JSONArray jsonMangas = (JSONArray) json.get("mangas");
		if (jsonMangas == null) {
			jsonMangas = new JSONArray();
			json.put("mangas", jsonMangas);
		}

		JSONObject jsonManga = findObject(jsonMangas, manga.getId());
		if (jsonManga == null) {
			jsonManga = new JSONObject();
			jsonManga.put("id", manga.getId());
			jsonManga.put("name", manga.getName());
	
			jsonMangas.add(jsonManga);
	
			Object[] sortedArray = jsonMangas.toArray();
			Arrays.sort(sortedArray, new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					JSONObject manga1 = (JSONObject) o1;
					JSONObject manga2 = (JSONObject) o2;
					return manga1.get("name").toString().compareTo(manga2.get("name").toString());
				}
			});
	
			JSONArray sortedJSONArray = new JSONArray();
			for (Object obj : sortedArray) {
				sortedJSONArray.add(obj);
			}
			json.put("mangas", sortedJSONArray);
	
			writeJSON(json);
		}
	}

	public void removeManga(Manga manga) throws IOException {
		JSONObject json = null;

		try {
			json = readJSON();
		} catch (Exception e) {
			// ignored
		}
		if (json == null)
			json = new JSONObject();

		JSONArray jsonMangas = (JSONArray) json.get("mangas");
		if (jsonMangas != null) {
			JSONObject jsonManga = findObject(jsonMangas, manga.getId());
			if (jsonManga != null) {
				jsonMangas.remove(jsonManga);

				writeJSON(json);
			}
		}
	}
}
