package cc.pinel.mangue;

public class Chapter {

	private final String number;
	private final String name;
	private final String link;

	public Chapter(String number, String name, String link) {
		this.number = number;
		this.name = name;
		this.link = link;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
}
