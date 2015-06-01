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
package de.inren.frontend.common.date;

import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * @author Ingo Renner
 *
 */
public class MonthPaginatorPanel extends Panel {

    private IModel<Date> dateModel;

    public MonthPaginatorPanel(String id, IModel<Date> model) {
        super(id);
        this.dateModel = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final Label monthLabel = new Label("month", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateModel.getObject());
                int month = cal.get(Calendar.MONTH);
                return getString("month.label." + month);
            }
        });
        monthLabel.setOutputMarkupId(true);
        add(monthLabel);

        add(new AjaxLink<Date>("up", dateModel) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(getModelObject());
                cal.add(Calendar.MONTH, 1);
                getModel().setObject(cal.getTime());
                target.add(monthLabel);
                onChange(target);
            }
        });

        add(new AjaxLink<Date>("down", dateModel) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(getModelObject());
                cal.add(Calendar.MONTH, -1);
                getModel().setObject(cal.getTime());
                target.add(monthLabel);
                onChange(target);
            }
        });

    }

    protected void onChange(AjaxRequestTarget target) {
        // override me
    }
}
