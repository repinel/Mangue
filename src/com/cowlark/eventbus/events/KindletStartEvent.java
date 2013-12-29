package com.cowlark.eventbus.events;

import com.cowlark.eventbus.Event;

public class KindletStartEvent extends Event<KindletStartEventHandler>
{
	public static Type<KindletStartEventHandler> TYPE =
		new Type<KindletStartEventHandler>();

    @Override
    public Type<KindletStartEventHandler> getAssociatedType() {
        return TYPE;
    }

	@Override
	protected void dispatch(KindletStartEventHandler handler)
	{
		handler.onKindletStart();
	}
}
