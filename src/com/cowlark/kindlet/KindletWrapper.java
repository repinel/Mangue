package com.cowlark.kindlet;

import com.amazon.kindle.kindlet.Kindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.cowlark.eventbus.HandlerManager;
import com.cowlark.eventbus.events.KindletCreateEvent;
import com.cowlark.eventbus.events.KindletCreateEventHandler;
import com.cowlark.eventbus.events.KindletDestroyEvent;
import com.cowlark.eventbus.events.KindletDestroyEventHandler;
import com.cowlark.eventbus.events.KindletStartEvent;
import com.cowlark.eventbus.events.KindletStartEventHandler;
import com.cowlark.eventbus.events.KindletStopEvent;
import com.cowlark.eventbus.events.KindletStopEventHandler;

public class KindletWrapper implements Kindlet, KindletCreateEventHandler,
	KindletStartEventHandler, KindletStopEventHandler,
	KindletDestroyEventHandler
{
	private final HandlerManager _handler;
	private KindletContext _context;
	
	public KindletWrapper()
	{
		_handler = new HandlerManager(this);
		_handler.addHandler(KindletStartEvent.TYPE, this);
		_handler.addHandler(KindletStopEvent.TYPE, this);
		_handler.addHandler(KindletCreateEvent.TYPE, this);
		_handler.addHandler(KindletDestroyEvent.TYPE, this);
	}
	
	public KindletContext getContext()
	{
		return _context;
	}
	
	public void create(KindletContext context)
	{
		_context = context;
		KindletCreateEvent event = new KindletCreateEvent();
		_handler.fireEvent(event);
	}
	
	public void onKindletCreate()
	{
	}
	
	public void destroy()
	{
		KindletDestroyEvent event = new KindletDestroyEvent();
		_handler.fireEvent(event);
	}
	
	public void onKindletDestroy()
	{
	}
	
	public void start()
	{
		KindletStartEvent event = new KindletStartEvent();
		_handler.fireEvent(event);
	}
	
	public void onKindletStart()
	{
	}
	
	public void stop()
	{
		KindletStopEvent event = new KindletStopEvent();
		_handler.fireEvent(event);
	}
	
	public void onKindletStop()
	{
	}
}
