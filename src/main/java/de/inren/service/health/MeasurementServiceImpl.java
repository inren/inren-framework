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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.health.Measurement;
import de.inren.data.domain.user.User;
import de.inren.data.repositories.health.MeasurementRepository;
import de.inren.frontend.common.dataprovider.RepositoryDataProvider;
import de.inren.service.user.UserService;

/**
 * @author Ingo Renner
 * 
 */
@Service
@Transactional(readOnly = true)
public class MeasurementServiceImpl implements MeasurementService {
	
	private final static Logger log = LoggerFactory.getLogger(MeasurementServiceImpl.class);

	
    @Autowired
    UserService userService;

    @Autowired
    private MeasurementRepository measurementRepository;

	@Override
	public void init() {
        userService.init();
        if (false) {
            if (measurementRepository.count() == 0L) {
                List<User> users = userService.loadAllUser();
                Random r = new Random();
                for (User u : users) {
                    Calendar cal = Calendar.getInstance();
                    final int max = 200;
                    cal.add(Calendar.DAY_OF_YEAR, -max);
                    int fat = 30;
                    int water = 40;
                    int weight = 120;

                    for (int i = 0; i < max; i++) {
                        Measurement m = new Measurement();
                        int pm = r.nextBoolean() ? 1 : -1;
                        fat = fat + pm * r.nextInt(2);
                        water = water + pm * r.nextInt(2);
                        weight = weight + pm * r.nextInt(5);
                        m.setUid(u.getId());
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                        m.setDate(new Date(cal.getTime().getTime()));
                        m.setFat(fat);
                        m.setWater(water);
                        m.setWeight(weight);
                        saveMeasurement(m);
                    }
                }
            }
        }
        log.info("created auto generated measurement.");
    };
    
    @Override
    public Measurement saveMeasurement(Measurement measurement) {
        try {
            if (measurement.getDate() == null) {
                measurement.setDate(Calendar.getInstance().getTime());
            }
            adjustDeltas(measurement);
            measurementRepository.save(measurement);
            return measurement;
        } catch (Exception e) {
            log.error("error saving measurement: " + measurement, e);
            throw new RuntimeException("MeasurementService", e);
        }
    }

    private void adjustDeltas(Measurement measurement) {
        // TODO Sorry, this is a quick one, needs a rewrite.
        Order o = new Order("date");
        List<Measurement> all = measurementRepository.findByUid(measurement.getUid(), new Sort(o));
        Measurement prev = null;
        for (Measurement m2 : all) {
            if (m2.getDate().before(measurement.getDate())) {
                prev = m2;
            } else {
                break;
            }
        }
        if (prev!=null) {
            measurement.setWeightDelta(measurement.getWeight() - prev.getWeight());  
            measurement.setFatDelta(measurement.getFat() - prev.getFat());  
            measurement.setWaterDelta(measurement.getWater() - prev.getWater());
            measurement.setMuscleDelta(measurement.getMuscle() - prev.getMuscle());
            measurement.setBoneDelta(measurement.getBone() - prev.getBone());
        } else {
            measurement.setWeightDelta(0);  
            measurement.setFatDelta(0);  
            measurement.setWaterDelta(0);
            measurement.setMuscle(0);
            measurement.setBone(0);
        }
    }
   

    @Override
    public List<Measurement> findAll() {
	List<Measurement> res = new ArrayList<Measurement>();
	Iterator<Measurement> iterator = measurementRepository.findAll().iterator();
	while (iterator.hasNext()) {
	    res.add(iterator.next());
	}
	return res;
    }
    
    @Override
    public ISortableDataProvider<Measurement, String> getDataProvider() {
	return new RepositoryDataProvider<Measurement>(measurementRepository);
    }

}
