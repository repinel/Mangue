package com.cowlark.eventbus.events;

import com.cowlark.eventbus.Event;

public class KindletDestroyEvent extends Event<KindletDestroyEventHandler>
{
	public static Type<KindletDestroyEventHandler> TYPE =
		new Type<KindletDestroyEventHandler>();

    @Override
    public Type<KindletDestroyEventHandler> getAssociatedType() {
        return TYPE;
    }

	@Override
	protected void dispatch(KindletDestroyEventHandler handler)
	{
		handler.onKindletDestroy();
	}
}
