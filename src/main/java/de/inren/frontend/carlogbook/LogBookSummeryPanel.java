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

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.car.CarLogEntryService;

/**
 * @author Ingo Renner
 *
 */
public class LogBookSummeryPanel extends ABasePanel {

    private static Logger      log = LoggerFactory.getLogger(EditOrCreateCarPanel.class);

    @SpringBean
    private CarLogEntryService carLogBookService;

    public LogBookSummeryPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        Map<String, String> statistic = carLogBookService.generateStatistic(carLogBookService.loadAllCars().get(0));
        add(new Label("totalDelta", statistic.get("totalDelta")));
        add(new Label("deltaSum", statistic.get("deltaSum")));
        add(new Label("fuelSum", statistic.get("fuelSum")));
        add(new Label("costSum", statistic.get("costSum")));
        add(new Label("pro100", statistic.get("pro100")));
        super.onInitialize();
    }
}
