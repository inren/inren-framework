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
package de.inren.data.domain.health;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "measurement")

public class Measurement extends DomainObject {

	@Column(name = "uid")
	private long uid;

	@Column(name = "date")
	private Date date;

	@Column(name = "weight")
	private double weight;

	@Column(name = "weightDelta")
	private double weightDelta;

	@Column(name = "fat")
	private double fat;

	@Column(name = "fatDelta")
	private double fatDelta;

	@Column(name = "water")
	private double water;

	@Column(name = "waterDelta")
	private double waterDelta;

	@Column(name = "muscle")
	private double muscle;

	@Column(name = "muscleDelta")
	private double muscleDelta;

	@Column(name = "bone")
	private double bone;

	@Column(name = "boneDelta")
	private double boneDelta;


	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeightDelta() {
		return weightDelta;
	}

	public void setWeightDelta(double weightDelta) {
		this.weightDelta = weightDelta;
	}

	public double getFat() {
		return fat;
	}

	public void setFat(double fat) {
		this.fat = fat;
	}

	public double getFatDelta() {
		return fatDelta;
	}

	public void setFatDelta(double fatDelta) {
		this.fatDelta = fatDelta;
	}

	public double getWater() {
		return water;
	}

	public void setWater(double water) {
		this.water = water;
	}

	public double getWaterDelta() {
		return waterDelta;
	}

	public void setWaterDelta(double waterDelta) {
		this.waterDelta = waterDelta;
	}

	public double getMuscle() {
		return muscle;
	}

	public void setMuscle(double muscle) {
		this.muscle = muscle;
	}

	public double getMuscleDelta() {
		return muscleDelta;
	}

	public void setMuscleDelta(double muscleDelta) {
		this.muscleDelta = muscleDelta;
	}

	public double getBone() {
		return bone;
	}

	public void setBone(double bone) {
		this.bone = bone;
	}

	public double getBoneDelta() {
		return boneDelta;
	}

	public void setBoneDelta(double boneDelta) {
		this.boneDelta = boneDelta;
	}

	@Override
	public String toString() {
		return "Measurement [uid=" + uid + ", date=" + date + ", weight="
				+ weight + ", weightDelta=" + weightDelta + ", fat=" + fat
				+ ", fatDelta=" + fatDelta + ", water=" + water
				+ ", waterDelta=" + waterDelta + ", muscle=" + muscle
				+ ", muscleDelta=" + muscleDelta + ", bone=" + bone
				+ ", boneDelta=" + boneDelta + "]";
	}

}
