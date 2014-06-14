/*
 * Copyright 2010 Adrian Petrescu.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located in the LICENSE file included with this
 * distribution.
 * 
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the
 * License.
 */
/*
 * Copyright 2014 Roque Pinel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kwt.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.amazon.kindle.kindlet.ui.KLabel;

/**
 * A selectable KLabel that can be focusable and receive ActionEvents. Intended
 * to be sometimes used in lieu of a KButton.
 * 
 * <br>
 * <br>
 * <font size="1">Example render: </font> <img src=
 * "http://s3.amazonaws.com/kwt-dev/javadoc_images/KWTSelectableLabel_ExampleRender.png"
 * >
 * 
 * @author Adrian Petrescu
 * @author Roque Pinel
 * 
 */
public class KWTSelectableLabel extends KLabel {
	private static final long serialVersionUID = 8118660222383683366L;

	private static final int BUTTON_DOWN_EVENT = 401;
	private static final int DEFAULT_UNDERLINE_WIDTH = 5;
	private static final int DEFAULT_UNDERLINE_GAP = 2;

	private List actionListeners;
	private int underlineWidth = DEFAULT_UNDERLINE_WIDTH;
	private int underlineGap = DEFAULT_UNDERLINE_GAP;

	/*
	 * This is a dirty hack to get around the fact that KLabels do not respond
	 * to setPosition(). Instead, when painting the superclass, we have to trick
	 * it into thinking its size is smaller than it actually is. However, we
	 * don't want this faulty size to be read at any other time. Hence this flag
	 * for when to spoof the size.
	 */
	private boolean spoofSize = false;

	/**
	 * Constructs a new selectable label with the given text. The text will be
	 * clipped if it extends past the label's maximum size.
	 * 
	 * @param text
	 *            the label's text
	 */
	public KWTSelectableLabel(String text) {
		super(text);
		enableEvents(AWTEvent.KEY_EVENT_MASK);
		setFocusable(true);
		actionListeners = new LinkedList();
	}

	/**
	 * {@inheritDoc }
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * {@inheritDoc }
	 */
	public Dimension getMinimumSize() {
		// Use KLabel's preferred size for its minimum size, to work around a
		// known bug in the KDK.
		Dimension d = super.getPreferredSize();
		if (spoofSize)
			return new Dimension(d.width, d.height - underlineGap - underlineWidth);
		return new Dimension(d.width, d.height + underlineGap + underlineWidth + 1);
	}

	/**
	 * {@inheritDoc }
	 */
	public Dimension getSize() {
		Dimension d = super.getSize();
		if (spoofSize)
			return new Dimension(d.width, d.height - underlineGap - underlineWidth);
		return new Dimension(d.width, d.height + underlineGap + underlineWidth + 1);
	}

	/**
	 * {@inheritDoc }
	 */
	public void paint(Graphics g) {
		spoofSize = true;
		super.paint(g);
		spoofSize = false;
		if (this.isFocusOwner()) {
			int y = super.getSize().height - (underlineGap + underlineWidth);
			g.setColor(Color.BLACK);
			for (int i = 0; i <= (this.getWidth() - 1) / (underlineWidth - 1); i += 2) {
				g.fillRect(i * (underlineWidth - 1), y + underlineGap, underlineWidth - 1, underlineWidth - 1);
			}
		}
	}

	/**
	 * Registers a listener who wishes to be notified whenever this label is
	 * clicked by the user.
	 * 
	 * @param listener
	 *            a listener who wishes to be notified
	 */
	public void addActionListener(ActionListener listener) {
		this.actionListeners.add(listener);
	}

	/**
	 * {@inheritDoc }
	 */
	public void processEvent(AWTEvent e) {
		switch (e.getID()) {
			case BUTTON_DOWN_EVENT:
				KeyEvent keyEvent = (KeyEvent) e;
				Iterator it = actionListeners.iterator();
				while (it.hasNext()) {
					ActionListener listener = (ActionListener) it.next();
					listener.actionPerformed(new ActionEvent(this, BUTTON_DOWN_EVENT, Integer.toString(keyEvent.getKeyCode())));
				}
				break;
			default:
				break;
		}
	}
}
