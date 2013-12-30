package cc.pinel.mangue;

public class Manga {

	private final String name;
	private final String firstChapterURL;

	public Manga(String name, String firstChapterURL) {
		this.name = name;
		this.firstChapterURL = firstChapterURL;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the firstChapterURL
	 */
	public String getFirstChapterURL() {
		return firstChapterURL;
	}
}
