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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.health.HealthSettings;
import de.inren.data.domain.health.Measurement;
import de.inren.data.domain.user.User;
import de.inren.data.repositories.health.HealthSettingsRepository;
import de.inren.data.repositories.health.MeasurementRepository;
import de.inren.frontend.common.backuprestore.EndMarker;
import de.inren.service.user.UserService;

/**
 * Service to backup and restore all health data into xml.
 * 
 * @author Ingo Renner
 *
 */
@Service(value = "healthXmlBackupRestoreService")
@Transactional(readOnly = true)

public class HealthXmlBackupRestoreServiceImpl implements HealthXmlBackupRestoreService {
	
	private final static Logger log = LoggerFactory.getLogger(HealthXmlBackupRestoreServiceImpl.class);
	
    private static final String INITIAL_HEALTH_SERVICE_DATA_XML = "initialHealthServiceData.xml";

    @Autowired
    private UserService userService;

    @Autowired
    private HealthSettingsService healthSettingsService;
    
    @Autowired
    private MeasurementRepository measurementRepository;
    
    @Resource
    HealthSettingsRepository healthSettingsRepository;

    @Override
    public void init(){
        userService.init();
        healthSettingsService.init();
        initDataBase();
        log.info("Health backup and restore service initialized");
    }
    
    @Override
    public String dumpDbToXml(String email) {
        XMLEncoder e;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        e = new XMLEncoder(new BufferedOutputStream(bos));
        List<User> users = new ArrayList<User>();
        if (email==null) {
            users.addAll(userService.loadAllUser());
        } else {
            User u = userService.loadUserByEmail(email);
            if (u!=null) {
                users.add(u);
            }
        }
        for (User u : users) {
            UidMapping um = new UidMapping(u.getId(), u.getEmail());
            e.writeObject(um);
            HealthSettings s = healthSettingsRepository.findByUid(u.getId());
            if (s!=null) {
                e.writeObject(s);
            }
            List<Measurement> measurements = measurementRepository.findByUid(u.getId());
            for (Measurement m : measurements) {
                e.writeObject(m);
            }
        }
        e.writeObject(new EndMarker());
        e.close();
        try {
            return bos.toString("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public String dumpDbToXml() {
        return dumpDbToXml(null);
    }

    @Override
    public void restoreFromXmlFile(File file) {
        // TODO Rechteprüfung
        XMLDecoder d;
        try {
            d = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("restoreFromXmlFileFailed", e);
        }
        boolean finished = false;
        Long currentId = null;
        UidMapping currentMapping = null;
        HealthSettings currentSettings = null;
        Measurement currentMeasurement = null;
        do {
            try {
                Object o = d.readObject();
                log.info("readObject: " + o);
                if (o instanceof UidMapping) {
                    currentMapping = (UidMapping) o;
                    User user = userService.loadUserByEmail(currentMapping.getEmail());
                    currentId = user==null ? null : user.getId();
                    if (currentId != null) {
                        HealthSettings s = healthSettingsService.loadByUser(currentId);
                        if (s != null) {
                            healthSettingsService.deleteHealthSettings(s);
                        }
                        List<Measurement> ms = measurementRepository.findByUid(currentId);
                        for (Measurement m : ms) {
                            measurementRepository.delete(m);
                        }
                    } else {
                        // TODO handle unknown user in mapping
                    }
                }
                if (o instanceof HealthSettings && currentId!=null) {
                    currentSettings = (HealthSettings) o;
                    currentSettings.setUid(currentId);
                    healthSettingsService.save(currentSettings);
                }
                if (o instanceof Measurement && currentId!=null) {
                    currentMeasurement = (Measurement) o;
                    currentMeasurement.setUid(currentId);
                    measurementRepository.save(currentMeasurement);
                }
                if (o instanceof EndMarker) {
                    finished = true;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                d.close();
                throw new RuntimeException("restoreFromXmlFileFailed", e);
            }
        } while (!finished);
        d.close();
    }
    
    private void initDataBase() {
        if (healthSettingsRepository.count() == 0) {
            File file = new File(getInitialConfigurationFolder(), INITIAL_HEALTH_SERVICE_DATA_XML);
            if (file.exists()) {
                log.info("HealthService is filled with initial data from: " + file.getAbsolutePath());
                restoreFromXmlFile(file);
            } else {
                log.info("If you need to init your db with custom data, put it here: " + file.getAbsolutePath());
            }
        }
    }

	private String getInitialConfigurationFolder() {
		return "./";
	}
}
