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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * The manga chapter.
 * 
 * @author Roque Pinel
 *
 */
public class Chapter {
	private static final Logger logger = Logger.getLogger(Chapter.class);

	private final String number;
	private final String link;

	private int pagesTotal = -1;

	/**
	 * @param number the number
	 * @param link the link
	 */
	public Chapter(String number, String link) {
		this.number = number;
		this.link = link;
	}

	/**
	 * @return number
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * @param pageNumber the page number
	 * 
	 * @return the image URL
	 * @throws IOException
	 */
	public URL getPageImageURL(int pageNumber) {
		URL imageURL = null;

		if (pageNumber > 0 && pageNumber <= getPageTotal()) {
			try {
				URL u = new URL(this.link + "/" + pageNumber);
				InputStream in = u.openStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;

				while ((line = reader.readLine()) != null) {
					if (line.indexOf(" id=\"img\" ") != -1) {
						int i = line.indexOf("src=\"");
						if (i != -1) {
							int j = line.indexOf("\"", i + 5);
							if (j != -1 && (i += 5) < line.length()) {
								imageURL = new URL(line.substring(i, j));
								break;
							}
						}
					}
				}

				reader.close();
				in.close();
			} catch (Exception e) {
				logger.error(e); // ignored
			}
		}

		return imageURL;
	}

	/**
	 * @return the total number of pages
	 */
	public int getPageTotal() {
		if (this.pagesTotal < 0) {
			try {
				URL u = new URL(this.link);
				InputStream in = u.openStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;

				while ((line = reader.readLine()) != null) {
					int i = line.indexOf("</select> of ");
					if (i != -1) {
						int j = line.indexOf("</div>", i + 1);
						if (j != -1 && (i += 13) < line.length()) {
							this.pagesTotal = Integer.parseInt(line.substring(i, j));
							break;
						}
					}
				}

				reader.close();
				in.close();
			} catch (Exception e) {
				logger.error(e);
				this.pagesTotal = 0;
			}
		}

		return this.pagesTotal;
	}

	/**
	 * @return if chapter has pages
	 */
	public boolean hasPages() {
		return this.pagesTotal > 0;
	}
}
