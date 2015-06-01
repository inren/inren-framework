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
package de.inren.frontend.carlogbook;

import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.car.CarLogEntry;
import de.inren.data.repositories.car.CarLogEntryRepository;
import de.inren.frontend.common.date.MonthPaginatorPanel;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;

/**
 * @author Ingo Renner
 *
 */
public class CarLogEntriesUserPanel extends ABasePanel<CarLogEntry> implements IAdminPanel {

    @SpringBean
    private CarLogEntryRepository logBookRepository;

    private final IModel<Date>    dateModel = Model.of(new Date());

    private LogBookSummeryPanel   logBookSummeryPanel;

    private Component             monthlySummery;

    public CarLogEntriesUserPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        monthlySummery = new MonthlySummeryPanel("monthlySummery", dateModel);
        monthlySummery.setOutputMarkupId(true);
        add(monthlySummery);
        logBookSummeryPanel = new LogBookSummeryPanel("summery");
        logBookSummeryPanel.setOutputMarkupId(true);
        add(logBookSummeryPanel);
        add(new MonthPaginatorPanel("month", dateModel) {
            @Override
            protected void onChange(AjaxRequestTarget target) {
                target.add(monthlySummery);
            }
        });
    }

}
