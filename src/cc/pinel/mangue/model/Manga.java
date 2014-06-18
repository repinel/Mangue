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

/**
 * The manga.
 * 
 * @author Roque Pinel
 *
 */
public class Manga {
	private final String id;
	private final String name;
	private final String path;

	/**
	 * @param id the id
	 * @param name the name
	 * @param path the path
	 */
	public Manga(String id, String name, String path) {
		this.id = id;
		this.name = name;
		this.path = path;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Generates the chapter link based on the
	 * manga path and the chapter number.
	 * 
	 * @param chapterNumber the chapter number
	 * @return the chapter link
	 */
	public String getChapterLink(String chapterNumber) {
		return "http://www.mangapanda.com" + this.path + "/" + chapterNumber;
	}

	/**
	 * Generates the link to all chapters based
	 * on the manga id.
	 * 
	 * @return the link to all chapters
	 */
	public String getAllChaptersLink() {
		return "http://www.mangapanda.com/actions/selector/?id=" + id + "&which=0";
	}
}
