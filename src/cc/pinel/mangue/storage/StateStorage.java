package cc.pinel.mangue.storage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazon.kindle.kindlet.KindletContext;

public class StateStorage extends AbstractStorage {
	private static final String STORAGE_FILE = "state.json";

	private static final Logger logger = Logger.getLogger(StateStorage.class);

	public StateStorage(KindletContext context) {
		super(context, STORAGE_FILE);
	}

	public String getChapter(String mangaId) {
		String chapterNumber = null;

		JSONArray jsonMangas;

		try {
			jsonMangas = (JSONArray) readJSON().get("mangas");
		} catch (Exception e) {
			logger.error(e);
			jsonMangas = new JSONArray();
		}

		JSONObject jsonManga = findObject(jsonMangas, mangaId);
		if (jsonManga != null)
			chapterNumber = jsonManga.get("chapterNumber").toString();

		logger.debug("manga id: " + mangaId + " - chapter number: " + chapterNumber);

		return chapterNumber;
	}

	@SuppressWarnings("unchecked")
	public void setChapter(String mangaId, String chapterNumber) throws IOException {
		JSONObject json;

		try {
			json = readJSON();
		} catch (Exception e) {
			logger.error(e);
			json = new JSONObject();
		}

		JSONArray jsonMangas = (JSONArray) json.get("mangas");
		if (jsonMangas == null) {
			jsonMangas = new JSONArray();
			json.put("mangas", jsonMangas);
		}

		JSONObject jsonManga = findObject(jsonMangas, mangaId);
		if (jsonManga == null) {
			jsonManga = new JSONObject();
			jsonManga.put("id", mangaId);
			jsonMangas.add(jsonManga);
		}
		jsonManga.put("chapterNumber", chapterNumber);

		writeJSON(json);
	}
}
