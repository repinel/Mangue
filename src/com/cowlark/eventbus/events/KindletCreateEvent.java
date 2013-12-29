package com.cowlark.eventbus.events;

import com.cowlark.eventbus.Event;

public class KindletCreateEvent extends Event<KindletCreateEventHandler>
{
	public static Type<KindletCreateEventHandler> TYPE =
		new Type<KindletCreateEventHandler>();

    @Override
    public Type<KindletCreateEventHandler> getAssociatedType() {
        return TYPE;
    }

	@Override
	protected void dispatch(KindletCreateEventHandler handler)
	{
		handler.onKindletCreate();
	}
}
