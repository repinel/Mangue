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

public class Manga {
	private static final Logger logger = Logger.getLogger(Manga.class);

	private final String id;
	private final String name;
	private final String firstChapterLink;

	private Collection<Chapter> chapters;

	public Manga(String id, String name, String firstChapterLink) {
		this.id = id;
		this.name = name;
		this.firstChapterLink = firstChapterLink;
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
	 * @return the firstChapterLink
	 */
	public String getFirstChapterLink() {
		return firstChapterLink;
	}

	/**
	 * @return the chapters
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public Collection<Chapter> getChapters() {
		if (this.chapters == null) {
			this.chapters = new ArrayList<Chapter>();

			logger.info("Fetching chapters for " + this.name);

			try {
				InputStream is = new URL("http://www.mangapanda.com/actions/selector/?id=" + id + "&which=0").openStream();

				JSONParser parser = new JSONParser();
				JSONArray chapters = (JSONArray) parser.parse(IOUtils.toString(is));

				for (int i = chapters.size() - 1; i >= 0; i--) {
					JSONObject chapter = (JSONObject) chapters.get(i);
					this.chapters.add(new Chapter(chapter.get("chapter").toString(), chapter.get("chapter_name").toString(), "http://www.mangapanda.com" + chapter.get("chapterlink").toString()));
				}
			} catch (MalformedURLException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			} catch (ParseException e) {
				logger.error(e);
			}

			logger.debug("chapters size: " + this.chapters.size());
		}

		return this.chapters;
	}
}
