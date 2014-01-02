package cc.pinel.mangue;

import java.util.ArrayList;
import java.util.Collection;

public class Manga {

	private final String name;
	private final String firstChapterLink;

	private Collection<Chapter> chapters;

	public Manga(String name, String firstChapterLink) {
		this.name = name;
		this.firstChapterLink = firstChapterLink;
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
	 */
	public Collection<Chapter> getChapters() {
		if (chapters == null) {
			// TODO: load chapters
			chapters = new ArrayList<Chapter>();
		}

		return chapters;
	}
}
