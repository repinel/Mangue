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

/**
 * The abstract classes for storages.
 * 
 * @author Roque Pinel
 *
 */
public abstract class AbstractStorage {
	private final KindletContext context;

	private final String storageFile;

	/**
	 * @param context the kindlet context
	 * @param storageFile the storage file
	 */
	public AbstractStorage(KindletContext context, String storageFile) {
		this.context = context;
		this.storageFile = storageFile;
	}

	/**
	 * Clears the storage.
	 * 
	 * @throws IOException
	 */
	public void clear() throws IOException {
		writeJSON(new JSONObject());
	}

	/**
	 * @return the storage path
	 */
	protected String getPath() {
		return this.context.getHomeDirectory().getAbsolutePath() + "/" + this.storageFile;
	}

	/**
	 * Reads the storage JSON.
	 * 
	 * @return the JSON object
	 * @throws ParseException
	 * @throws IOException
	 */
	protected JSONObject readJSON() throws ParseException, IOException {
		InputStream is = getClass().getResourceAsStream(getPath());

		JSONParser parser = new JSONParser();

		return (JSONObject) parser.parse(IOUtils.toString(is));
	}

	/**
	 * Writes the JSON object to the storage.
	 * 
	 * @param json the JSON object
	 * @throws IOException
	 */
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

	/**
	 * Finds the manga JSON object from the array
	 * based on the manga id provided.
	 * 
	 * @param jsonMangas the array
	 * @param mangaId the manga id
	 * @return the manga, if found
	 */
	protected static JSONObject findManga(JSONArray jsonMangas, String mangaId) {
		for(int i = 0; i < jsonMangas.size(); i++) {
			JSONObject jsonManga = (JSONObject) jsonMangas.get(i);
			Object id = jsonManga == null ? null : jsonManga.get("id");
			if (id.equals(mangaId))
				return jsonManga;
		}
		return null;
	}
}
