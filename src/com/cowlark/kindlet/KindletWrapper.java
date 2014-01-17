/*
 * Copyright (C) 2011 by David Given
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
