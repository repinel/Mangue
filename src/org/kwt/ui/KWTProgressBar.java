/*
 * Copyright 2010 Adrian Petrescu.
 * 
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.amazon.kindle.kindlet.ui.KComponent;

/**
 * A progress bar. Represents the proportion of progress towards completing a
 * particular task. Meant to emulate the book progress bar at the bottom of the
 * regular Kindle reader application.
 * 
 * <br>
 * <br>
 * <font size="1">Example render: </font> <img src=
 * "http://s3.amazonaws.com/kwt-dev/javadoc_images/KWTProgressBar_ExampleRender.png"
 * >
 * 
 * @author Adrian Petrescu
 * @author Roque Pinel
 * 
 */
public class KWTProgressBar extends KComponent {
	private static final long serialVersionUID = 5781953629278873008L;

	private static final int VERTICAL_PADDING = 3;
	private static final int HORIZONTAL_PADDING = 10;
	private static final int CORNER_ROUNDING = 10;

	private int totalTicks;
	private int currentTick;

	/**
	 * Constructs a new progress bar. The current tick begins at 0.
	 * 
	 * @param totalTicks
	 *            The total number of ticks representing completion of the task.
	 */
	public KWTProgressBar(int totalTicks) {
		this.totalTicks = totalTicks;
		this.currentTick = 0;

		this.setFocusable(false);
	}

	/**
	 * Returns the total number of ticks.
	 * 
	 * @return the total number of ticks.
	 */
	public int getTotalTicks() {
		return totalTicks;
	}

	/**
	 * Sets the total number of ticks. If the current tick is larger than
	 * <code>totalTicks</code>, then it is set to <code>totalTicks</code>.
	 * 
	 * @param totalTicks
	 *            the total number of ticks representing completion of the task.
	 */
	public void setTotalTicks(int totalTicks) {
		this.totalTicks = totalTicks;

		// Make sure current tick is not beyond the maximum value.
		setCurrentTick(currentTick);
	}

	/**
	 * Returns the current tick.
	 * 
	 * @return the current tick.
	 */
	public int getCurrentTick() {
		return currentTick;
	}

	/**
	 * Sets the current tick. If <code>currentTick < 0</code> or
	 * <code>currentTick > totalTicks</code>, then <code>currentTick</code> will
	 * be set to 0 or <code>totalTicks</code> respectively.
	 * 
	 * @param currentTick
	 *            the current tick.
	 */
	public void setCurrentTick(int currentTick) {
		this.currentTick = Math.min(totalTicks, Math.max(0, currentTick));
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
		return new Dimension(2 * HORIZONTAL_PADDING, 2 * VERTICAL_PADDING);
	}

	/**
	 * {@inheritDoc }
	 */
	public void paint(Graphics g) {
		double progress = (double) currentTick / totalTicks;

		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_ROUNDING, CORNER_ROUNDING);
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, (int) (progress * getWidth()) - 1, getHeight() - 1, CORNER_ROUNDING, CORNER_ROUNDING);

		g.setXORMode(Color.WHITE);
		for (int i = 0; i < totalTicks; i++) {
			int x = (int) (((double) i / totalTicks) * getWidth()) - 1;
			g.fillRect(x, 0, 2, getHeight() - 1);
		}
	}
}
