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
package cc.pinel.mangue.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cc.pinel.mangue.Main;
import cc.pinel.mangue.util.StringUtils;

public class Manga {
	private final String id;
	private final String name;
	private final String path;

	private Collection<Chapter> chapters;

	public Manga(String id, String name, String path) {
		this.id = id;
		this.name = name;
		this.path = path;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the chapters
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ParseException 
	 */
	public Collection<Chapter> getChapters() throws MalformedURLException, IOException, ParseException {
		if (this.chapters == null || this.chapters.isEmpty()) {
			this.chapters = new ArrayList<Chapter>();

			Main.logger.info("Fetching chapters for " + this.name);

			InputStream is = new URL("http://www.mangapanda.com/actions/selector/?id=" + id + "&which=0").openStream();

			JSONParser parser = new JSONParser();
			JSONArray chapters = (JSONArray) parser.parse(IOUtils.toString(is));

			for (int i = chapters.size() - 1; i >= 0; i--) {
				JSONObject chapter = (JSONObject) chapters.get(i);
				this.chapters.add(new Chapter(chapter.get("chapter").toString(),
						StringUtils.unescapeHtml(chapter.get("chapter_name").toString()),
						getChapterLink(chapter.get("chapter").toString())));
			}

			Main.logger.debug("chapters size: " + this.chapters.size());
		}
		return this.chapters;
	}

	public Chapter getChapter(String chapterNumber) {
		try {
			for (Chapter chapter : this.getChapters()) {
				if (chapter.getNumber().equals(chapterNumber)) {
					return chapter;
				}
			}
		} catch (Exception e) {
			Main.logger.error(e);
		}
		return null;
	}

	public String getChapterLink(String chapterNumber) {
		return "http://www.mangapanda.com" + this.path + "/" + chapterNumber;
	}
}
