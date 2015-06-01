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

package de.inren.data.domain.banking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Ingo Renner
 *
 */
public class TransactionDateSpecification implements Specification<Transaction> {

    private Date from;
    private Date until;

    public TransactionDateSpecification(Date from, @Nullable Date until) {
        this.from = from;
        this.until = until;
    }

    @Override
    public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(cb.greaterThanOrEqualTo(root.<Date> get("accountingDate"), from));
        if (until != null) {
            predicates.add(cb.lessThanOrEqualTo(root.<Date> get("accountingDate"), until));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

}
