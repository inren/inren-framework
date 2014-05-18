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
public class HealthSettings  extends DomainObject {
    
    @Column(nullable = false, unique=true)
    private Long uid;

    @Column(name = "height")
    private Double height;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "male")
    private Boolean male;
    
    @Column(name = "targetWeightMin")
    private Double targetWeightMin;
    
    @Column(name = "targetWeightMax")
    private Double targetWeightMax;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Boolean getMale() {
		return male;
	}

	public void setMale(Boolean male) {
		this.male = male;
	}

	public Double getTargetWeightMin() {
		return targetWeightMin;
	}

	public void setTargetWeightMin(Double targetWeightMin) {
		this.targetWeightMin = targetWeightMin;
	}

	public Double getTargetWeightMax() {
		return targetWeightMax;
	}

	public void setTargetWeightMax(Double targetWeightMax) {
		this.targetWeightMax = targetWeightMax;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HealthSettings [uid=").append(uid).append(", height=")
				.append(height).append(", birthday=").append(birthday)
				.append(", male=").append(male).append(", targetWeightMin=")
				.append(targetWeightMin).append(", targetWeightMax=")
				.append(targetWeightMax).append(", toString()=")
				.append(super.toString()).append("]");
		return builder.toString();
	}
}
