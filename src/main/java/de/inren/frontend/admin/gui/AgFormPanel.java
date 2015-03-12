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

package de.inren.frontend.admin.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * 
 * 
 * @author Ingo Renner
 *
 */
public class AgFormPanel<T> extends Panel {

    public static final String CANCEL_ID    = "cancel";
    public static final String SUBMIT_ID    = "submit";
    public static final String COMPONENT_ID = "id";

    private List<Component>    components   = new ArrayList<Component>();
    private Component          submit;
    private Component          cancel;

    public AgFormPanel(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = new Form<>("form");
        add(form);

        form.add(new ListView<Component>("components", Model.ofList(components)) {

            @Override
            protected void populateItem(ListItem<Component> item) {
                item.add((Component) item.getDefaultModelObject());
            }
        });
        if (submit == null) {
            form.add(new SubmitLink(SUBMIT_ID));
        } else {
            if (!SUBMIT_ID.equals(submit.getId())) {
                throw new IllegalStateException("Expected submit component with id 'submit'.");
            }
            form.add(submit);
        }
        if (cancel == null) {
            form.add(new EmptyPanel(CANCEL_ID));
        } else {
            if (!CANCEL_ID.equals(cancel.getId())) {
                throw new IllegalStateException("Expected cancel component with id 'cancel'.");
            }
            form.add(cancel);
        }
    }

    public AgFormPanel<T> addInput(Component component) {
        components.add(component);
        return AgFormPanel.this;
    }

    public AgFormPanel<T> setSubmit(Component submit) {
        this.submit = submit;
        return AgFormPanel.this;
    }

    public AgFormPanel<T> setCancel(Component cancel) {
        this.cancel = cancel;
        return AgFormPanel.this;
    }
}
