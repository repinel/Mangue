package cc.pinel.mangue.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazon.kindle.kindlet.KindletContext;

public abstract class AbstractStorage {
	private final KindletContext context;

	private final String storageFile;

	public AbstractStorage(KindletContext context, String storageFile) {
		this.context = context;
		this.storageFile = storageFile;
	}

	protected String getPath() {
		return this.context.getHomeDirectory().getAbsolutePath() + "/" + this.storageFile;
	}

	protected JSONObject readJSON() throws ParseException, IOException {
		InputStream is = getClass().getResourceAsStream(getPath());

		JSONParser parser = new JSONParser();

		return (JSONObject) parser.parse(IOUtils.toString(is));
	}

	protected void writeJSON(JSONObject json) throws IOException {
		FileWriter fw = null;

		try {
			fw = new FileWriter(getPath());

			json.writeJSONString(fw);
		} finally {
			if (fw != null)
				fw.close();
		}
	}

	protected JSONObject findObject(JSONArray jsonMangas, String mangaId) {
		for(int i = 0; i < jsonMangas.size(); i++) {
			JSONObject jsonManga = (JSONObject) jsonMangas.get(i);
			if (jsonManga.get("id").equals(mangaId))
				return jsonManga;
		}
		return null;
	}
}
