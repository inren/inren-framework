/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.frontend.common.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;

/**
 * @author Ingo Renner
 */
public abstract class AActionLink extends Panel {

    private final boolean             ajax;
    private final GlyphIconType       iconType;

    private final StringResourceModel label;
    private final StringResourceModel message;

    public AActionLink(boolean ajax, GlyphIconType iconType) {
        this(ajax, iconType, null);
    }

    public AActionLink(boolean ajax, GlyphIconType iconType, StringResourceModel message) {
        super("item");
        this.ajax = ajax;
        this.iconType = iconType;
        this.message = message;
        this.label = null;
    }

    public AActionLink(boolean ajax, GlyphIconType iconType, StringResourceModel message, StringResourceModel label) {
        super("item");
        this.ajax = ajax;
        this.iconType = iconType;
        this.message = message;
        this.label = label;
    }

    @Override
    protected void onBeforeRender() {
        if (!hasBeenRendered()) {
            initGui();
        }
        super.onBeforeRender();
    }

    private void initGui() {
        if (ajax) {
            ajaxInit();
        } else {
            init();
        }
    }

    private void ajaxInit() {
        AjaxLink<Object> link = new AjaxLink<Object>("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AActionLink.this.onClick(target);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                AjaxCallListener myAjaxCallListener = new AjaxCallListener() {
                    @Override
                    public AjaxCallListener onPrecondition(CharSequence script) {
                        CharSequence precondition = getMessage() == null ? script : "if(!confirm('" + getMessage().getString() + "')) return false;" + script;
                        return super.onPrecondition(precondition);
                    }
                };
                attributes.getAjaxCallListeners().add(myAjaxCallListener);
            }
        };

        link.add(new Icon(iconType));
        if (getLabel() != null) {
            link.add(new Label("label", getLabel()).setRenderBodyOnly(true));
            link.add(new CssClassNameAppender("btn"));
        } else {
            link.add(new Label("label", "").setRenderBodyOnly(true));
        }
        add(link);
    }

    private void init() {
        Link<Object> link = new Link<Object>("link") {
            @Override
            public void onClick() {
                AActionLink.this.onClick(null);
            }
        };
        if (getMessage() != null) {
            link.add(new AttributeModifier("", "return confirm('" + getMessage().getString() + "');"));
        }
        link.add(new Icon(iconType));
        if (getLabel() != null) {
            link.add(new Label("label", getLabel()).setRenderBodyOnly(true));
            link.add(new CssClassNameAppender("btn"));
        } else {
            link.add(new Label("label", "").setRenderBodyOnly(true));
        }
        add(link);
    }

    public abstract void onClick(AjaxRequestTarget target);

    public StringResourceModel getMessage() {
        return message;
    }

    public StringResourceModel getLabel() {
        return label;
    }

}
