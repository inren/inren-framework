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

package de.inren.data.domain.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Ingo Renner
 *
 */
public class UserSpecification implements Specification<User> {

	private User filter;
	
	public UserSpecification(User filter) {
		this.filter = filter;
	}

	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		if(filter.getFirstname()!=null) {
			if(filter.getFirstname().contains("%")){
				predicates.add(cb.like(root.get("firstname"),filter.getFirstname()));
			} else {
				predicates.add(cb.equal(root.get("firstname"),filter.getFirstname()));
			}
		}
		
		return cb.and(predicates.toArray(new Predicate[]{}));
	}


}
