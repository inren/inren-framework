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
package de.inren.frontend.storehouse.picture;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import de.inren.data.domain.picture.Picture;
import de.inren.frontend.application.InRenApplication;

/**
 * Simple way to display a picture without worrying about html and stuff.
 * 
 * @author Ingo Renner
 * 
 */
public class SimplePicturePanel extends Panel {

    private final PictureVariantType variant;
    private final Picture            picture;

    public SimplePicturePanel(String id, final PictureVariantType variant, final Picture picture) {
        super(id);
        this.variant = variant;
        this.picture = picture;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final String url;
        final String css;
        switch (variant) {
            case THUMBNAIL:
                url = ((InRenApplication) getApplication()).getThumbnailUrl(picture.getDigest(), RequestCycle.get());
                css = "thumbnail";
                break;
            case PREVIEW:
                url = ((InRenApplication) getApplication()).getPreviewUrl(picture.getDigest(), RequestCycle.get());
                css = "preview";
                break;
            case LAYOUT:
                url = ((InRenApplication) getApplication()).getLayoutUrl(picture.getDigest(), RequestCycle.get());
                css = "layout";
                break;
            default:
                throw new RuntimeException("Unknown dimension " + variant);
        }
        final WebComponent wc = new WebComponent("pic");
        wc.add(AttributeModifier.replace("alt", picture.getTitle()));
        wc.add(AttributeModifier.replace("title", picture.getTitle()));
        wc.add(AttributeModifier.replace("src", url));
        wc.add(AttributeModifier.replace("class", css));
        add(wc);
    }
}
