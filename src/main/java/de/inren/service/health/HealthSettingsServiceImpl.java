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
package de.inren.service.health;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.health.HealthSettings;
import de.inren.data.domain.user.User;
import de.inren.data.repositories.health.HealthSettingsRepository;
import de.inren.service.user.UserService;

/**
 * @author Ingo Renner
 * 
 */
@Service(value = "healthSettingsService")
@Transactional(readOnly = true)
public class HealthSettingsServiceImpl implements HealthSettingsService {
	private final static Logger log = LoggerFactory.getLogger(HealthSettingsServiceImpl.class);
	
    @Autowired
    UserService userService;

    @Resource
    HealthSettingsRepository healthSettingsRepository;

    @Override
    public void init() {
        userService.init();
        if (false) {
            if (healthSettingsRepository.count() == 0L) {
                List<User> users = userService.loadAllUser();
                for (User u : users) {
                    HealthSettings s = new HealthSettings();
                    s.setUid(u.getId());
                    s.setMale(true);
                    s.setHeight(1.89);
                    Calendar cal = Calendar.getInstance();
                    cal.set(1969, 02, 22);
                    s.setBirthday(cal.getTime());
                    healthSettingsRepository.save(s);
                }
            }
        }
        log.info("Health settings service initialized");
    };

    @Override
    public HealthSettings load(long id) {
        return healthSettingsRepository.findOne(id);
    }

    @Override
    public HealthSettings loadByUser(Long uid) {
        return healthSettingsRepository.findByUid(uid);
    }

    @Override
    public HealthSettings save(HealthSettings healthSettings) {
        try {
            return healthSettingsRepository.save(healthSettings);
        } catch (Exception e) {
            log.error("error saving healthSettings: " + healthSettings, e);
            throw new RuntimeException("HealthSettingsService", e);
        }
    }

    @Override
    public void deleteHealthSettings(HealthSettings healthSettings) {
        healthSettingsRepository.delete(healthSettings);
    }

}
