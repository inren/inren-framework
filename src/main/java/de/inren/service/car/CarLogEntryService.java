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

import java.util.List;
import java.util.Map;

import de.inren.data.domain.car.Car;
import de.inren.data.domain.car.CarLogEntry;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
public interface CarLogEntryService extends Initializable {

    List<Car> loadAllCars();

    List<CarLogEntry> loadAllLogEntries();

    List<Car> loadCarsForUser(Long userId);

    List<CarLogEntry> loadLogEntriesForCar(Long carId);

    Car saveCar(Car car);

    CarLogEntry saveLogEntry(CarLogEntry logBook);

    Car loadCar(Long id);

    CarLogEntry loadLogEntry(Long id);

    Map<String, String> generateStatistic(Car car);
}
