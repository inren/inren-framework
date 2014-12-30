/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.inren.frontend.storehouse.dynamic;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;

/**
 * @author Ingo Renner
 *
 */
public class TextImageResource extends RenderedDynamicImageResource {

    private final String text;

    public TextImageResource(int width, int height, String text) {
        super(width, height);
        this.text = text;
    }

    @Override
    protected boolean render(Graphics2D graphics, Attributes attributes) {
        graphics.drawRect(0, 0, getHeight(), getWidth());
        String[] parts = text.split("\n");
        paint(graphics, parts);
        return true;
    }

    public void paint(Graphics g, String[] parts) {
        int newline = g.getFont().getSize() + 5;
        int y = 20;
        for (int i = 0; i < parts.length; i++) {
            g.drawString(parts[i], 10, y += newline);
        }
    }

    public String getText() {
        return text;
    }

}
