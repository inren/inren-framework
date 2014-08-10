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

package de.inren.data.domain.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author Ingo Renner
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity(name = "ComponentAccess")
public class ComponentAccess extends AuthorizedDomainObject {

    public ComponentAccess() {
    }

    @Column(nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    private Set<SimpleGrantedAuthority> authorities;
    
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        if (authorities == null) {
            initAuthorities();
        }
        return authorities;
    }
    
    private void initAuthorities() {
        this.authorities = new HashSet<SimpleGrantedAuthority>();
        Collection<Role> roles = getGrantedRoles();
        for (Role role : roles) {
            authorities.addAll(role.asAuthority());
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ComponentAccess [name=");
        builder.append(name);
        builder.append(", authorities=");
        builder.append(authorities);
        builder.append("]");
        return builder.toString();
    }

}
