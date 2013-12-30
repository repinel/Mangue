package cc.pinel.mangue.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.kwt.ui.KWTSelectableLabel;

import cc.pinel.mangue.Manga;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KPages;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.pages.PageProviders;

public class MainPanel extends KPanel {
	private static final long serialVersionUID = -4692282056850151456L;

	private final KPages mangaListPages;

	public MainPanel(KindletContext context, List<Manga> mangas) {
		super(new GridBagLayout());

		mangaListPages = new KPages(PageProviders.createKBoxLayoutProvider(KBoxLayout.Y_AXIS));
		mangaListPages.setFocusable(true);
		mangaListPages.setEnabled(true);
		mangaListPages.setPageKeyPolicy(KPages.PAGE_KEYS_LOCAL);

		for (Manga manga : mangas) {
			final KWTSelectableLabel mangaLabel = new KWTSelectableLabel(manga.getName());
			mangaLabel.setFocusable(true);
			mangaLabel.setEnabled(true);
			mangaLabel.setUnderlineStyle(KWTSelectableLabel.STYLE_DASHED);
			mangaLabel.addActionListener(new MangaLabelActionListener(this.mangaList()));
			mangaListPages.addItem(mangaLabel);
		}
		mangaListPages.getComponent(0).requestFocus();

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(20, 20, 20, 20);
		gc.anchor = GridBagConstraints.NORTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		add(mangaListPages, gc);
		mangaListPages.first();
	}

	private List<String> mangaList() {
		List<String> mangas = new ArrayList<String>();
		mangas.add("Naruto");
		return mangas;
	}

	private class MangaLabelActionListener implements ActionListener {
		private final List<String> mangaList;

		public MangaLabelActionListener(List<String> mangaList) {
			this.mangaList = mangaList;
		}

		public void actionPerformed(ActionEvent event) {

		}
	}
}
