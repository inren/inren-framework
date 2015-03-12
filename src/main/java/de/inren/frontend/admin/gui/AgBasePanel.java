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

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Default panel for admin panels
 * 
 * @author Ingo Renner
 *
 */
public abstract class AgBasePanel<T extends Serializable> extends Panel {

    public AgBasePanel(String id) {
        super(id);
    }

    public AgBasePanel(String id, IModel<T> model) {
        super(id, model);
    }

    protected StringResourceModel createDefaultStringResourceModel(String prefix, Component component) {
        return new StringResourceModel(prefix + ".label", component, getDefaultModel());
    }

}
