package cc.pinel.mangue.storage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
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
}
