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
package de.inren.frontend.health;

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

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.inren.data.domain.health.Measurement;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.health.MeasurementService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateMeasurementPanel extends ABasePanel implements IAdminPanel {

    @SpringBean
    private MeasurementService measurementService;

    private final IWorktopManageDelegate<Measurement> delegate;

    public EditOrCreateMeasurementPanel(String id, IModel<Measurement> m, IWorktopManageDelegate<Measurement> delegate) {
        super(id, (m != null && m.getObject() != null) ? new CompoundPropertyModel<Measurement>(m.getObject()) : new CompoundPropertyModel<Measurement>(
                new Measurement()));
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<Measurement> form = new Form<Measurement>("form");
        form.setDefaultModel(getDefaultModel());

        StringResourceModel lDate = new StringResourceModel("date.label", EditOrCreateMeasurementPanel.this, null);
        form.add(new Label("date.label", lDate));
        form.add(new DateTextField("date").setRequired(true).setLabel(lDate));

        StringResourceModel lWeight = new StringResourceModel("weight.label", EditOrCreateMeasurementPanel.this, null);
        form.add(new Label("weight.label", lWeight));
        form.add(new TextField<String>("weight", String.class).setRequired(true).setLabel(lWeight));

        StringResourceModel lFat = new StringResourceModel("fat.label", EditOrCreateMeasurementPanel.this, null);
        form.add(new Label("fat.label", lFat));
        form.add(new TextField<String>("fat", String.class).setRequired(true).setLabel(lFat));

        StringResourceModel lWater = new StringResourceModel("water.label", EditOrCreateMeasurementPanel.this, null);
        form.add(new Label("water.label", lWater));
        form.add(new TextField<String>("water", String.class).setRequired(true).setLabel(lWater));

        StringResourceModel lMuscle = new StringResourceModel("muscle.label", EditOrCreateMeasurementPanel.this, null);
        form.add(new Label("muscle.label", lMuscle));
        form.add(new TextField<String>("muscle", String.class).setRequired(true).setLabel(lMuscle));

        StringResourceModel lBone = new StringResourceModel("bone.label", EditOrCreateMeasurementPanel.this, null);
        form.add(new Label("bone.label", lBone));
        form.add(new TextField<String>("bone", String.class).setRequired(true).setLabel(lBone));

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
                    Measurement measurement = (Measurement) form.getModelObject();
                    measurement.setUid(getUser().getId());

                    Measurement u = measurementService.saveMeasurement(measurement);
                    form.info(new StringResourceModel("feedback.success", EditOrCreateMeasurementPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateMeasurementPanel.this, null).getString());
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
