package cc.pinel.mangue;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Manga {

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

			try {
				InputStream is = new URL("http://www.mangapanda.com/actions/selector/?id=" + id + "&which").openStream();

				JSONArray chapters = new JSONArray(IOUtils.toString(is));

				for (int i = chapters.length() - 1; i >= 0; i--) {
					JSONObject chapter = (JSONObject) chapters.get(i);
					this.chapters.add(new Chapter(chapter.get("chapter").toString(), chapter.get("chapter_name").toString(), "http://www.mangapanda.com" + chapter.get("chapterlink").toString()));
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return this.chapters;
	}
}
