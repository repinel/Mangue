package com.cowlark.eventbus.events;

import com.cowlark.eventbus.EventHandler;

public interface KindletStartEventHandler extends EventHandler
{
	public void onKindletStart();
}
