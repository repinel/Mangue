package cc.pinel.mangue.ui;

import java.awt.GridBagLayout;

import cc.pinel.mangue.Chapter;
import cc.pinel.mangue.Main;

import com.amazon.kindle.kindlet.ui.KPanel;

public class ViewPanel extends KPanel {
	private static final long serialVersionUID = -2485604965935171736L;

	private final Main main;

	public ViewPanel(Main main, Chapter chapter) {
		super(new GridBagLayout());

		this.main = main;
	}
}
