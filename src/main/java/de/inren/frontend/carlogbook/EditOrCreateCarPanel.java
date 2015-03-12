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
/**
 * Copygroup 2014 the original author or authors.
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

package de.inren.frontend.carlogbook;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.car.Car;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.car.CarLogEntryService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateCarPanel extends ABasePanel implements IAdminPanel {

    private static Logger                     log = LoggerFactory.getLogger(EditOrCreateCarPanel.class);
    @SpringBean
    private CarLogEntryService                 carLogBookService;

    private final IWorktopManageDelegate<Car> delegate;

    final Car                                 car;

    public EditOrCreateCarPanel(String componentId, IModel<Car> m, IWorktopManageDelegate<Car> delegate) {
        super(componentId);
        if (m != null) {
            car = m.getObject();
        } else {
            car = new Car();
            car.setUid(getUser().getId());
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<Car> form = new Form<Car>("form", new CompoundPropertyModel<Car>(car));

        StringResourceModel lPlate = new StringResourceModel("plate.label", EditOrCreateCarPanel.this, null);
        form.add(new Label("plate.label", lPlate));
        form.add(new TextField<String>("plate", String.class).setRequired(true).setLabel(lPlate).setRequired(false).setLabel(lPlate));

        StringResourceModel lDescription = new StringResourceModel("description.label", EditOrCreateCarPanel.this, null);
        form.add(new Label("description.label", lDescription));
        form.add(new TextField<String>("description", String.class).setRequired(false).setLabel(lDescription).setRequired(false).setLabel(lDescription));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                delegate.switchToComponent(target, delegate.getManagePanel());
            }
        });

        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    Car u = carLogBookService.saveCar((Car) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateCarPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", EditOrCreateCarPanel.this, null).getString());
                    target.add(getFeedback());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                FeedbackPanel f = getFeedback();
                if (target != null && f != null) {
                    target.add(f);
                }
            }
        });

        add(form);
    }
}
