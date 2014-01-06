package cc.pinel.mangue.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cc.pinel.mangue.util.StringUtils;

public class Manga {
	private static final Logger logger = Logger.getLogger(Manga.class);

	private final String id;
	private final String name;

	private Collection<Chapter> chapters;

	public Manga(String id, String name) {
		this.id = id;
		this.name = name;
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
	 * @return the chapters
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ParseException 
	 */
	public Collection<Chapter> getChapters() throws MalformedURLException, IOException, ParseException {
		if (this.chapters == null || this.chapters.isEmpty()) {
			this.chapters = new ArrayList<Chapter>();

			logger.info("Fetching chapters for " + this.name);

			InputStream is = new URL("http://www.mangapanda.com/actions/selector/?id=" + id + "&which=0").openStream();

			JSONParser parser = new JSONParser();
			JSONArray chapters = (JSONArray) parser.parse(IOUtils.toString(is));

			for (int i = chapters.size() - 1; i >= 0; i--) {
				JSONObject chapter = (JSONObject) chapters.get(i);
				this.chapters.add(new Chapter(chapter.get("chapter").toString(),
						StringUtils.unescapeHtml(chapter.get("chapter_name").toString()),
						"http://www.mangapanda.com" + chapter.get("chapterlink").toString()));
			}

			logger.debug("chapters size: " + this.chapters.size());
		}
		return this.chapters;
	}
}
