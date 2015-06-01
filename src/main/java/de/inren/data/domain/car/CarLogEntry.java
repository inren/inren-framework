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
package de.inren.data.domain.car;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.core.DomainObject;

/**
 * 
 * The entry for a car logbook.
 * 
 * @author Ingo Renner
 *
 */
@Entity(name = "CarLogEntry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CarLogEntry extends DomainObject {

    @Column(nullable = false)
    private Long    carId;

    @Column(nullable = false)
    private Integer totalKm;

    @Column(nullable = true)
    private Double  deltaKm;

    // in liter
    @Column(nullable = true)
    private Double  fuel;

    // in liter
    @Column(nullable = true)
    private Double  oil;

    // Some service
    @Column(nullable = true)
    private String  service;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date    pitStopDate;

    // in Euro
    @Column(nullable = false)
    private Double  price;

    private String  station;

    public Integer getTotalKm() {
        return totalKm;
    }

    public String getTotalKmLabel() {
        int startKm = totalKm - deltaKm.intValue();
        return "(" + startKm + ") " + totalKm;
    }

    public void setTotalKm(Integer totalKm) {
        this.totalKm = totalKm;
    }

    public Double getDeltaKm() {
        return deltaKm;
    }

    public void setDeltaKm(Double deltaKm) {
        this.deltaKm = deltaKm;
    }

    public Double getFuel() {
        return fuel;
    }

    public Double getFuel100() {
        return fuel * 100.0 / deltaKm;
    }

    public void setFuel(Double fuel) {
        this.fuel = fuel;
    }

    public Double getOil() {
        return oil;
    }

    public void setOil(Double oil) {
        this.oil = oil;
    }

    public Date getPitStopDate() {
        return pitStopDate;
    }

    public void setPitStopDate(Date pitStopDate) {
        this.pitStopDate = pitStopDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "CarLogEntry [carId=" + carId + ", totalKm=" + totalKm + ", deltaKm=" + deltaKm + ", fuel=" + fuel + ", oil=" + oil + ", service=" + service
                + ", pitStopDate=" + pitStopDate + ", price=" + price + ", station=" + station + "]";
    }

}
