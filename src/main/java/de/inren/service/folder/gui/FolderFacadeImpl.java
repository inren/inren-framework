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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.inren.data.domain.folder.Folder;
import de.inren.data.domain.security.AuthorizedDomainObject;
import de.inren.service.folder.FolderService;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "folderFacade")
public class FolderFacadeImpl implements FolderFacade {

	@Autowired
	private FolderService folderService;

	@Override
	public FolderMenuGM getFolderMenu() {
		FolderMenuGM menu = new FolderMenuGM();
		
		Folder root = folderService.getRootFolder();
		FolderGM rootGM = mapToGM(root);
		menu.setRoot(rootGM);
		menu.setCurrentFolderId(rootGM.getId());
		return menu;
	}

	private FolderGM mapToGM(Folder folder) {
		FolderGM gm = new FolderGM();
		gm.setId(folder.getId());
		gm.setType(folder.getType());
		gm.setName(folder.getName());
		gm.setRoles(folder.getGrantedRoles());
		List<FolderGM> folders = new ArrayList<FolderGM>();
		gm.setFolders(folders);
		for (AuthorizedDomainObject obj : folder.getItems()) {
			if (obj instanceof Folder) {
				folders.add(mapToGM((Folder) obj));
			}
		}
		return gm;
	}
	

}

