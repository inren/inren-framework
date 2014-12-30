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

package de.inren.data.domain.folder;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.security.AuthorizedDomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity(name = "Folder")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Folder extends AuthorizedDomainObject {

	FolderType type;
	
    @Column(nullable = false, unique = true)
    private String name;
	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "folder_item", joinColumns = { @JoinColumn(name = "folder_item_folderid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "folder_item_itemid", referencedColumnName = "id") })
    private Collection<AuthorizedDomainObject> items;

    public FolderType getType() {
		return type;
	}

	public void setType(FolderType type) {
		this.type = type;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Collection<AuthorizedDomainObject> getItems() {
        if (items == null) {
            this.items = new ArrayList<AuthorizedDomainObject>();
        }
        return items;
    }

    public void setItems(Collection<AuthorizedDomainObject> items) {
        this.items = new ArrayList<AuthorizedDomainObject>();
        this.items.addAll(items);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Folder [type=").append(type).append(", name=")
				.append(name).append(", items=").append(items).append("]");
		return builder.toString();
	}
}
