/**
 * Copyright 2011 the original author or authors.
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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Simple way to display a picture without worrying about html and stuff.
 * 
 * @author Ingo Renner
 * 
 */
public class TextImagePicturePanel extends Panel {

    private TextImageResource image;

    public TextImagePicturePanel(String id, final TextImageResource image) {
        super(id);
        this.image = image;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        String css = "thumbnail";
        Image picture = new Image("pic", image);
        picture.add(AttributeModifier.replace("alt", image.getText()));
        picture.add(AttributeModifier.replace("title", image.getText()));
        picture.add(AttributeModifier.replace("class", css));
        add(picture);
    }
}
