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

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import de.inren.data.domain.health.HealthSettings;
import de.inren.data.repositories.health.MeasurementRepository;
import de.inren.frontend.common.templates.SecuredPage;
import de.inren.frontend.jqplot.IJqplotDefinition;
import de.inren.service.health.HealthSettingsService;

/**
 * @author Ingo Renner
 *
 */
@MountPath(value = "/healthbmi")
public class BmiWikiPage extends SecuredPage<IJqplotDefinition> {
    
    @SpringBean
    private MeasurementRepository measurementRepository;
    
    @SpringBean
    private HealthSettingsService healthSettingsService;

    @Override
    public Component createPanel(String wicketId) {
        return new BmiChartPanel(wicketId, createJqplotModel());
    }

    final IModel<IJqplotDefinition> createJqplotModel() {
        HealthSettings s = new HealthSettings();
        try {
             s = healthSettingsService.loadByUser(getSession().getUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Model<IJqplotDefinition>(new BmiJqplotDefinition(measurementRepository, s, "weight", getSession().getUser().getId()));
    }
}
