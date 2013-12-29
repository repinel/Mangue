package cc.pinel.mangue;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KTextArea;
import com.cowlark.kindlet.KindletWrapper;

public class Main extends KindletWrapper
{
	@Override
	public void onKindletStart()
	{
		KindletContext context = getContext();
		KTextArea kta = new KTextArea("Hello, world!");
		context.getRootContainer().add(kta);
	}
}
