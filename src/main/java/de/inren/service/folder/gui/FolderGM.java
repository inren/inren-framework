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

package de.inren.service.folder.gui;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import de.inren.data.domain.folder.FolderType;
import de.inren.data.domain.security.Role;

/**
 * @author Ingo Renner
 *
 */
public class FolderGM implements Serializable {
	
	private long id;
	private String name;
	private FolderType type;
	private Collection<Role> roles;
	private List<FolderGM> folders;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FolderType getType() {
		return type;
	}
	public void setType(FolderType type) {
		this.type = type;
	}
	public Collection<Role> getRoles() {
		return roles;
	}
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
	public List<FolderGM> getFolders() {
		return folders;
	}
	public void setFolders(List<FolderGM> folders) {
		this.folders = folders;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FolderGM [id=").append(id).append(", name=")
				.append(name).append(", type=").append(type).append(", roles=")
				.append(roles).append(", folders=").append(folders).append("]");
		return builder.toString();
	}
}
