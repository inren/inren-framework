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

package de.inren.service.car;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.car.Car;
import de.inren.data.domain.car.CarLogEntry;
import de.inren.data.domain.user.User;
import de.inren.data.repositories.car.CarLogEntryRepository;
import de.inren.data.repositories.car.CarRepository;
import de.inren.service.user.UserService;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "carLogBookService")
@Transactional(readOnly = true)
public class CarLogEntryServiceImpl implements CarLogEntryService {

    private final static Logger   log      = LoggerFactory.getLogger(CarLogEntryServiceImpl.class);

    private boolean               initDone = false;

    @Resource
    private UserService           userService;

    @Resource
    private CarRepository         carRepository;

    @Resource
    private CarLogEntryRepository carLogEntryRepository;

    @Override
    public void init() {
        if (!initDone) {
            log.info("CarLogEntryService init start.");
            userService.init();

            User user = userService.loadByIdent("admin@localhost");
            List<Car> cars = carRepository.findByUid(user.getId());
            Car car;
            if (cars.isEmpty()) {
                car = saveCar(readInitCar(user.getId()));
            } else {
                car = cars.get(0);
            }

            if (carLogEntryRepository.findByCarId(car.getId()).isEmpty()) {
                // Alte Tankzettel
                List<CarLogEntry> books = readInitlogBooks(car.getId());
                carLogEntryRepository.save(books);
            }
            initDone = true;
            log.info("CarLogBookService init done.");
        }
    }

    @Override
    public List<Car> loadAllCars() {
        List<Car> res = new ArrayList<Car>();
        CollectionUtils.addAll(res, carRepository.findAll().iterator());
        return res;
    }

    @Override
    public List<CarLogEntry> loadAllLogEntries() {
        List<CarLogEntry> res = new ArrayList<CarLogEntry>();
        CollectionUtils.addAll(res, carLogEntryRepository.findAll().iterator());
        return res;
    }

    @Override
    public List<Car> loadCarsForUser(Long userId) {
        List<Car> res = new ArrayList<Car>();
        CollectionUtils.addAll(res, carRepository.findByUid(userId).iterator());
        return res;
    }

    @Override
    public List<CarLogEntry> loadLogEntriesForCar(Long carId) {
        List<CarLogEntry> res = new ArrayList<CarLogEntry>();
        CollectionUtils.addAll(res, carLogEntryRepository.findByCarId(carId).iterator());
        return res;
    }

    @Override
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    public CarLogEntry saveLogEntry(CarLogEntry logBook) {
        return carLogEntryRepository.save(logBook);
    }

    @Override
    public Car loadCar(Long id) {
        return carRepository.findOne(id);
    }

    @Override
    public CarLogEntry loadLogEntry(Long id) {
        return carLogEntryRepository.findOne(id);
    }

    @Override
    public Map<String, String> generateStatistic(Car car) {
        Map<String, String> res = new HashMap<>();
        List<CarLogEntry> logs = carLogEntryRepository.findByCarIdOrderByPitStopDateAsc(car.getId());
        int deltaSum = 0;
        Double fuelSum = 0.0;
        Double costSum = 0.0;
        int totalDelta = logs.get(logs.size() - 1).getTotalKm() - logs.get(0).getTotalKm();
        for (CarLogEntry logBook : logs) {
            deltaSum += logBook.getDeltaKm().intValue();
            fuelSum += logBook.getFuel();
            costSum += logBook.getPrice();
        }
        res.put("totalDelta", String.valueOf(totalDelta));
        res.put("deltaSum", String.valueOf(deltaSum));
        res.put("fuelSum", String.valueOf(fuelSum));
        res.put("costSum", String.valueOf(costSum));
        res.put("pro100", String.valueOf(fuelSum * 100.0 / deltaSum));
        return res;
    }

    private Car readInitCar(Long uid) {
        Map<Integer, List<String>> map = readCSvFile("/home/ingo/.auto.csv");
        Car car = new Car();
        car.setUid(uid);
        car.setPlate(map.get(0).get(0));
        car.setDescription(map.get(0).get(1));
        return car;
    }

    private List<CarLogEntry> readInitlogBooks(Long cid) {
        List<CarLogEntry> res = new LinkedList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

        Map<Integer, List<String>> map = readCSvFile("/home/ingo/.tanken.csv");
        for (Integer key : map.keySet()) {
            List<String> values = map.get(key);
            CarLogEntry book = new CarLogEntry();
            book.setCarId(cid);
            book.setStation(values.get(0).trim());
            book.setFuel(Double.parseDouble(values.get(1).trim()));
            book.setPrice(Double.parseDouble(values.get(2).trim()));
            try {
                book.setPitStopDate(formatter.parse(values.get(3).trim()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            book.setDeltaKm(Double.parseDouble(values.get(4).trim()));
            book.setTotalKm(Integer.parseInt(values.get(5).trim()));
            res.add(book);
        }
        return res;
    }

    private Map<Integer, List<String>> readCSvFile(final String csvFile) {

        Map<Integer, List<String>> res = new HashMap<Integer, List<String>>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            int key = 0;
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] values = line.split(cvsSplitBy);
                res.put(key, Arrays.asList(values));
                key++;
                System.out.println("values = " + values);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
}
