package cc.pinel.mangue;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Page {
	private static final Logger logger = Logger.getLogger(Page.class);

	private final String number;
	private final String link;

	private Image image;

	public Page(String number, String link) {
		this.number = number;
		this.link = link;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		if (this.image == null) {
			logger.info("Fetching pages for chapter " + this.link);

			DOMParser parser = new DOMParser();
			InputSource url = new InputSource(this.link);

			try {
				parser.parse(url);
				Document document = parser.getDocument();
				Element img = document.getElementById("img");
				this.image = Toolkit.getDefaultToolkit().getImage(new URL(img.getAttribute("src")));
				logger.debug("image src: " + img.getAttribute("src"));
			} catch (SAXException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			}
		}

		return this.image;
	}
}
