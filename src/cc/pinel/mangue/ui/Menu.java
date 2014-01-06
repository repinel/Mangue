package cc.pinel.mangue.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cc.pinel.mangue.Main;

import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;

public class Menu extends KMenu {

	private final Main main;

	public Menu (Main main) {
		this.main = main;

		KMenuItem addItem = new KMenuItem("Add Mangas");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Menu.this.main.searchManga();
			}
		});

		add(addItem);
	}
}
