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

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.car.CarLogEntry;
import de.inren.frontend.admin.gui.AgFormPanel;
import de.inren.frontend.admin.gui.AgTextFieldInputPanel;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.car.CarLogEntryService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateCarLogEntryPanel extends ABasePanel implements IAdminPanel {

    private static Logger                         log = LoggerFactory.getLogger(EditOrCreateCarLogEntryPanel.class);
    @SpringBean
    private CarLogEntryService                     carLogBookService;

    private final IWorktopManageDelegate<CarLogEntry> delegate;

    final CarLogEntry                                 logBook;

    public EditOrCreateCarLogEntryPanel(String componentId, IModel<CarLogEntry> m, IWorktopManageDelegate<CarLogEntry> delegate) {
        super(componentId);
        if (m != null) {
            logBook = m.getObject();
        } else {
            logBook = new CarLogEntry();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        AgFormPanel<CarLogEntry> form = new AgFormPanel<CarLogEntry>("form", new CompoundPropertyModel<CarLogEntry>(logBook));
        form.addInput(new AgTextFieldInputPanel<Integer>(AgFormPanel.COMPONENT_ID, "totalKm", new PropertyModel<Integer>(logBook, "totalKm")))
                .addInput(new AgTextFieldInputPanel<Integer>(AgFormPanel.COMPONENT_ID, "deltaKm", new PropertyModel<Integer>(logBook, "deltaKm")))
                .addInput(new AgTextFieldInputPanel<Double>(AgFormPanel.COMPONENT_ID, "fuel", new PropertyModel<Double>(logBook, "fuel")))
                .addInput(new AgTextFieldInputPanel<Double>(AgFormPanel.COMPONENT_ID, "oil", new PropertyModel<Double>(logBook, "oil")))
                .addInput(new AgTextFieldInputPanel<String>(AgFormPanel.COMPONENT_ID, "service", new PropertyModel<String>(logBook, "service")))
                .addInput(new AgTextFieldInputPanel<Double>(AgFormPanel.COMPONENT_ID, "price", new PropertyModel<Double>(logBook, "price")))
                .addInput(new AgTextFieldInputPanel<Date>(AgFormPanel.COMPONENT_ID, "pitStopDate", new PropertyModel<Date>(logBook, "pitStopDate")));

        form.setCancel(new AjaxLink<Void>(AgFormPanel.CANCEL_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                delegate.switchToComponent(target, delegate.getManagePanel());
            }
        });

        form.setSubmit(new AjaxButton(AgFormPanel.SUBMIT_ID) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    CarLogEntry u = carLogBookService.saveLogEntry((CarLogEntry) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateCarLogEntryPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", EditOrCreateCarLogEntryPanel.this, null).getString());
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
