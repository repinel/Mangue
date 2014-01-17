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
package cc.pinel.mangue.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cc.pinel.mangue.model.Manga;

public class MangaSearch {
	private static final Logger logger = Logger.getLogger(MangaSearch.class);

	private static final int MIN_TOKEN_LENGHT = 6;

	public static Collection<Manga> search(String query) throws MalformedURLException, IOException {
		Collection<Manga> mangas = new ArrayList<Manga>();

		InputStream is = new URL("http://www.mangapanda.com/actions/search/?q=" + query + "&limit=20").openStream();

		String lines[] = StringUtils.split(IOUtils.toString(is), '\n');

		logger.debug("Search " + lines.length + " results: " + query);

		for (int i = 0, length = lines.length; i < length; i++) {
			String tokens[] = StringUtils.splitPreserveAllTokens(lines[i], '|');

			if (tokens.length >= MIN_TOKEN_LENGHT)
				mangas.add(new Manga(tokens[5], tokens[2]));
		}

		logger.debug("mangas size: " + mangas.size());

		return mangas;
	}

}
