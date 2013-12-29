package com.cowlark.eventbus.events;

import com.cowlark.eventbus.EventHandler;

public interface KindletDestroyEventHandler extends EventHandler
{
	public void onKindletDestroy();
}
