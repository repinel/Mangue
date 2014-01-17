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
import java.net.URL;

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

	private URL imageURL;

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
	 * @return the imageURL
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public URL getImageURL() throws SAXException, IOException {
		if (this.imageURL == null) {
			logger.info("Fetching image URL for page" + this.link);

			DOMParser parser = new DOMParser();
			InputSource url = new InputSource(this.link);

			parser.parse(url);
			Document document = parser.getDocument();
			Element img = document.getElementById("img");
			this.imageURL = new URL(img.getAttribute("src"));

			logger.debug("image src: " + this.imageURL);
		}
		return this.imageURL;
	}
}
