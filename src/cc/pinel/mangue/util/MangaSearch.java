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

import cc.pinel.mangue.model.Manga;

public class MangaSearch {
	/**
	 * The minimum token length so that the required information can be loaded.
	 */
	private static final int MIN_TOKEN_LENGHT = 6;

	/**
	 * Searches mangas remotely using the query.
	 * 
	 * @param query the query to be searched
	 * @return the list of mangas filtered
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Collection search(String query) throws MalformedURLException, IOException {
		Collection mangas = new ArrayList();

		InputStream is = new URL("http://www.mangapanda.com/actions/search/?q=" + query + "&limit=20").openStream();

		String lines[] = StringUtils.split(IOUtils.toString(is), '\n');

		for (int i = 0, length = lines.length; i < length && i < 20; i++) {
			String tokens[] = StringUtils.splitPreserveAllTokens(lines[i], '|');

			if (tokens.length >= MIN_TOKEN_LENGHT)
				mangas.add(new Manga(tokens[5], tokens[2], convertOldPath(tokens[4])));
		}

		return mangas;
	}

	/**
	 * Converts old path format to new paths format.
	 * 
	 * @param currentPath the current path
	 * @return the new path
	 */
	private static String convertOldPath(String currentPath) {
		int formatIndex = currentPath.indexOf(".html");
		if (formatIndex != 1 && currentPath.charAt(0) == '/') {
			int middleSlashIndex = currentPath.indexOf('/', 1);
			if (middleSlashIndex != -1 && middleSlashIndex < formatIndex)
				return "/" + currentPath.substring(middleSlashIndex + 1, formatIndex);
		}
		return currentPath;
	}
}
