package com.cowlark.eventbus.events;

import com.cowlark.eventbus.Event;

public class KindletStopEvent extends Event<KindletStopEventHandler>
{
	public static Type<KindletStopEventHandler> TYPE =
		new Type<KindletStopEventHandler>();

    @Override
    public Type<KindletStopEventHandler> getAssociatedType() {
        return TYPE;
    }

	@Override
	protected void dispatch(KindletStopEventHandler handler)
	{
		handler.onKindletStop();
	}
}
