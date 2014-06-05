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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.model.Manga;

import com.amazon.kindle.kindlet.KindletContext;

public class MangaStorage extends AbstractStorage {
	public MangaStorage(KindletContext context) {
		super(context, "mangas.json");
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
			mangas.add(new Manga(jsonManga.get("id").toString(), jsonManga.get("name").toString(), jsonManga.get("path").toString()));
		}

		Main.logger.debug("mangas size: " + mangas.size());

		return mangas;
	}

	public Manga getManga(String id) {
		JSONObject json = null;

		try {
			json = readJSON();
		} catch (Exception e) {
			// ignored
		}
		if (json == null)
			return null;

		JSONArray jsonMangas = (JSONArray) json.get("mangas");
		if (jsonMangas == null)
			return null;

		JSONObject jsonManga = findObject(jsonMangas, id);
		if (jsonManga == null)
			return null;

		return new Manga(jsonManga.get("id").toString(), jsonManga.get("name").toString(), jsonManga.get("path").toString());
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
			jsonManga.put("path", manga.getPath());
	
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
