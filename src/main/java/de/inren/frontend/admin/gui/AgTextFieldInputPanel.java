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
package de.inren.frontend.admin.gui;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author Ingo Renner
 * @param <T>
 *
 */
public class AgTextFieldInputPanel<T extends Serializable> extends AgBasePanel<T> {

    private String       prefix;
    private TextField<T> textField;

    public AgTextFieldInputPanel(String id, IModel<T> model) {
        this(id, id, model);

    }

    public AgTextFieldInputPanel(String id, String prefix, IModel<T> model) {
        super(id, model);
        this.prefix = prefix;
        this.textField = new TextField<T>("input", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final StringResourceModel label = createDefaultStringResourceModel(prefix, AgTextFieldInputPanel.this);
        add(new Label("input.label", label));
        textField.setLabel(label);
        add(textField);
    }
}
