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

/**
 * The general storage.
 * 
 * @author Roque Pinel
 *
 */
public class GeneralStorage {
	/**
	 * The search term key.
	 */
	private static final String SEARCH_TERM_KEY = "search_term";

	/**
	 * The current manga, chapter and page keys.
	 */
	private static final String CURRENT_MANGA_KEY = "current_manga";
	private static final String CURRENT_CHAPTER_KEY = "current_chapter";
	private static final String CURRENT_PAGE_KEY = "current_page";

	private final SecureStorage secureStorage;

	/**
	 * @param context the kindlet context
	 */
	public GeneralStorage(KindletContext context) {
		this.secureStorage = context.getSecureStorage();
	}

	// --- Search Term ---

	/**
	 * @return the search term
	 */
	public String getSearchTerm() {
		return getValue(SEARCH_TERM_KEY);
	}

	/**
	 * Saves the search term.
	 * 
	 * @param term the search term
	 * @return if it was saved
	 */
	public boolean setSearchTerm(String term) {
		return secureStorage.putChars(SEARCH_TERM_KEY, term.toCharArray());
	}

	/**
	 * Removes the search term from the storage.
	 * 
	 * @return if it was removed
	 */
	public boolean removeSearchTerm() {
		return secureStorage.remove(SEARCH_TERM_KEY);
	}

	// --- Last Viewed ---

	/**
	 * @return the current manga id
	 */
	public String getCurrentMangaId() {
		return getValue(CURRENT_MANGA_KEY);
	}

	/**
	 * Saves the current manga id.
	 * 
	 * @param id the manga id
	 * @return if it was saved
	 */
	public boolean setCurrentMangaId(String id) {
		return secureStorage.putChars(CURRENT_MANGA_KEY, id.toCharArray());
	}

	/**
	 * Removes the current manga id from the storage.
	 * 
	 * @return if it was removed
	 */
	public boolean removeCurrentMangaId() {
		return secureStorage.remove(CURRENT_MANGA_KEY);
	}

	/**
	 * @return the current chapter number
	 */
	public String getCurrentChapterNumber() {
		return getValue(CURRENT_CHAPTER_KEY);
	}

	/**
	 * Saves the current chapter number.
	 * 
	 * @param number the chapter number
	 * @return if it was saved
	 */
	public boolean setCurrentChapterNumber(String number) {
		return secureStorage.putChars(CURRENT_CHAPTER_KEY, number.toCharArray());
	}

	/**
	 * Removes the current chapter number from the storage.
	 * 
	 * @return if it was removed
	 */
	public boolean removeCurrentChapterNumber() {
		return secureStorage.remove(CURRENT_CHAPTER_KEY);
	}

	/**
	 * @return the current page number
	 */
	public String getCurrentPageNumber() {
		return getValue(CURRENT_PAGE_KEY);
	}

	/**
	 * Saves the current page number.
	 * 
	 * @param number the page number
	 * @return if it was saved
	 */
	public boolean setCurrentPageNumber(String number) {
		return secureStorage.putChars(CURRENT_PAGE_KEY, number.toCharArray());
	}

	/**
	 * Removes the current page number from the storage.
	 * 
	 * @return if it was removed
	 */
	public boolean removeCurrentPageNumber() {
		return secureStorage.remove(CURRENT_PAGE_KEY);
	}

	// --- Private Methods ---

	/**
	 * Returns the value from key.
	 * 
	 * @param key
	 * @return the value
	 */
	private String getValue(String key) {
		char[] value = secureStorage.getChars(key);
		return value == null ? null : new String(value);
	}
}
