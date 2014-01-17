/*
 * Copyright 2014 Roque Pinel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	public void clear() throws IOException {
		writeJSON(new JSONObject());
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
			Object id = jsonManga == null ? null : jsonManga.get("id");
			if (id.equals(mangaId))
				return jsonManga;
		}
		return null;
	}
}
