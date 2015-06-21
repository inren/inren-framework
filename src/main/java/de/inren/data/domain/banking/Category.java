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
package de.inren.data.domain.banking;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity(name = "Category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category extends DomainObject {

    @Column(nullable = false, unique = true)
    private String               name;

    // income or expense (default)
    private boolean              income;

    // Can be used to find start and end date for a (money) month period (time
    // from
    // one income to another).
    private boolean              marksMonth;

    @Column(nullable = true)
    private String               description;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<CategoryFilter> filter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CategoryFilter> getFilter() {
        return filter;
    }

    public void setFilter(List<CategoryFilter> filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "Category [name=" + name + ", income=" + income + ", description=" + description + ", filter=" + filter + ", getId()=" + getId() + ", isNew()="
                + isNew() + ", toString()=" + super.toString() + ", hashCode()=" + hashCode() + ", getClass()=" + getClass() + "]";
    }

}
