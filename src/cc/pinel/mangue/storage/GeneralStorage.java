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
package cc.pinel.mangue.storage;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.security.SecureStorage;

public class GeneralStorage {

	private final String SEARCH_TERM_KEY = "search_term";

	private final String CURRENT_MANGA_KEY = "current_manga";
	private final String CURRENT_CHAPTER_KEY = "current_chapter";
	private final String CURRENT_PAGE_KEY = "current_page";

	private final SecureStorage secureStorage;

	public GeneralStorage(KindletContext context) {
		this.secureStorage = context.getSecureStorage();
	}

	// search term

	public String getSearchTerm() {
		return getValue(SEARCH_TERM_KEY);
	}

	public boolean setSearchTerm(String term) {
		return secureStorage.putChars(SEARCH_TERM_KEY, term.toCharArray());
	}

	public boolean removeSearchTerm() {
		return secureStorage.remove(SEARCH_TERM_KEY);
	}

	// last viewed

	public String getCurrentMangaId() {
		return getValue(CURRENT_MANGA_KEY);
	}

	public boolean setCurrentMangaId(String id) {
		return secureStorage.putChars(CURRENT_MANGA_KEY, id.toCharArray());
	}

	public boolean removeCurrentMangaId() {
		return secureStorage.remove(CURRENT_MANGA_KEY);
	}

	public String getCurrentChapterNumber() {
		return getValue(CURRENT_CHAPTER_KEY);
	}

	public boolean setCurrentChapterNumber(String number) {
		return secureStorage.putChars(CURRENT_CHAPTER_KEY, number.toCharArray());
	}

	public boolean removeCurrentChapterNumber() {
		return secureStorage.remove(CURRENT_CHAPTER_KEY);
	}

	public String getCurrentPageNumber() {
		return getValue(CURRENT_PAGE_KEY);
	}

	public boolean setCurrentPageNumber(String number) {
		return secureStorage.putChars(CURRENT_PAGE_KEY, number.toCharArray());
	}

	public boolean removeCurrentPageNumber() {
		return secureStorage.remove(CURRENT_PAGE_KEY);
	}

	// private methods

	private String getValue(String key) {
		char[] value = secureStorage.getChars(key);
		return value == null ? null : new String(value);
	}
}
