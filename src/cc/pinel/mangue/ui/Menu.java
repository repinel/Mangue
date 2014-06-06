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
package cc.pinel.mangue.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cc.pinel.mangue.Main;

import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;

public class Menu extends KMenu {

	private final Main main;

	public Menu(Main main) {
		this.main = main;

		KMenuItem addItem = new KMenuItem("Add Manga");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Menu.this.main.searchManga();
			}
		});
		add(addItem);

		addSeparator();

		KMenuItem clearSearchItem = new KMenuItem("Clear Previous Search");
		clearSearchItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Menu.this.main.clearSearch();
			}
		});
		add(clearSearchItem);

		KMenuItem clearFavoritesItem = new KMenuItem("Clear Favorites");
		clearFavoritesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Menu.this.main.clearMangas();
			}
		});
		add(clearFavoritesItem);
	}
}
