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
package de.inren.service.health;

import net.bull.javamelody.MonitoredWithSpring;
import de.inren.data.domain.health.HealthSettings;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
@MonitoredWithSpring
public interface HealthSettingsService extends Initializable {

    HealthSettings load(long id);

    HealthSettings loadByUser(Long uid);

    HealthSettings save(HealthSettings healthSettings);

    void deleteHealthSettings(HealthSettings healthSettings);

}
