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

package de.inren.data.domain.security;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import de.inren.data.domain.core.DomainObject;

/**
 * Simple right to be assigned for permission of some action.
 * 
 * @author Ingo Renner
 * 
 */
@Entity(name = "ListOfRights")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ListOfRights",
// include = "all")
// Right is not allowed with mysql.
public class Right extends DomainObject {

    @Column(nullable = false, unique = true)
    private String           name;

    @Column(nullable = true)
    private String           description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = { @JoinColumn(name = "right_roleid", referencedColumnName = "id") }, name = "role_right", inverseJoinColumns = { @JoinColumn(name = "role_rightid", referencedColumnName = "id") })
    private Collection<Role> roles;

    public Right() {
    }

    public Right(String name) {
        super();
        this.name = name;
    }

    public Right(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Right [name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", roles=");
        String sep = "";
        for (Role role : roles) {
            builder.append(sep).append(role.getName()).append(" [").append(role.getId()).append("]");
            sep = ", ";
        }
        builder.append("]");
        return builder.toString();
    }

}
