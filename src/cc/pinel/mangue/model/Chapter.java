package cc.pinel.mangue.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Chapter {
	private static final Logger logger = Logger.getLogger(Chapter.class);

	private final String number;
	private final String name;
	private final String link;

	private List<Page> pages;

	public Chapter(String number, String name, String link) {
		this.number = number;
		this.name = name;
		this.link = link;
	}

	public String getTitle() {
		return this.number + " - " + this.name;
	}

	/**
	 * @return the pages
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public List<Page> getPages() throws SAXException, IOException {
		if (this.pages == null || this.pages.isEmpty()) {
			this.pages = new ArrayList<Page>();

			logger.info("Fetching pages for chapter " + this.link);

			DOMParser parser = new DOMParser();
			InputSource url = new InputSource(this.link);

			parser.parse(url);
			Document document = parser.getDocument();
			Element pageMenu = document.getElementById("pageMenu");
			NodeList options = pageMenu.getElementsByTagName("OPTION");

			for (int i = 0; i < options.getLength(); i++) {
				Node node = options.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element option = (Element) node;
					this.pages.add(new Page(option.getTextContent(), "http://www.mangapanda.com" + option.getAttribute("value")));
				}
			}

			logger.debug("pages size: " + this.pages.size());
		}
		return this.pages;
	}
}
