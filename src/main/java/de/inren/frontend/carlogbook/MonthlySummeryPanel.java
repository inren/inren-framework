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

package de.inren.frontend.carlogbook;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import de.inren.data.domain.car.Car;
import de.inren.data.domain.car.CarLogEntry;
import de.inren.security.BasicAuthenticationSession;
import de.inren.service.car.CarLogEntryService;

/**
 * @author Ingo Renner
 *
 */
public class MonthlySummeryPanel extends Panel {

    private IModel<Date>       dateModel;

    Map<String, String>        summary = new HashMap<String, String>();

    @SpringBean
    private CarLogEntryService carLogEntryService;

    private Car                car;

    public MonthlySummeryPanel(String id, IModel<Date> model) {
        super(id);
        this.dateModel = model;
        List<Car> cars = carLogEntryService.loadCarsForUser(BasicAuthenticationSession.get().getUser().getId());
        car = cars.get(0);
        List<CarLogEntry> data = carLogEntryService.loadLogEntriesForCarAndMonth(car.getId(), dateModel.getObject());
        calculateSummary(data);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        List<CarLogEntry> data = carLogEntryService.loadLogEntriesForCarAndMonth(car.getId(), dateModel.getObject());
        calculateSummary(data);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("liter", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return summary.get("liter");
            }
        }));
        add(new Label("preis", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return summary.get("preis");
            }
        }));
        add(new Label("km", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return summary.get("km");
            }
        }));
        add(new Label("proHundert", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return summary.get("proHundert");
            }
        }));
    }

    private void calculateSummary(List<CarLogEntry> data) {

        summary.clear();

        float liter = 0.0f;
        float preis = 0.0f;
        float km = 0.0f;
        float proHundert = 0.0f;

        for (CarLogEntry entry : data) {
            if (Strings.isEmpty(entry.getService())) {
                liter += entry.getFuel();
                preis += entry.getPrice();
                km += entry.getDeltaKm();
            }
        }
        summary.put("liter", String.format("%.2f", liter));
        summary.put("preis", String.format("%.2f", preis));
        summary.put("km", String.format("%.2f", km));

        proHundert = liter * 100.0f / km;

        summary.put("proHundert", String.format("%.2f", proHundert));
    }
}
