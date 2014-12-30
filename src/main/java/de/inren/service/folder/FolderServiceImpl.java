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

package de.inren.service.folder;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.folder.Folder;
import de.inren.data.domain.folder.FolderType;
import de.inren.data.domain.security.AuthorizedDomainObject;
import de.inren.data.domain.security.Role;
import de.inren.data.repositories.comment.CommentRepository;
import de.inren.data.repositories.folder.FolderRepository;
import de.inren.service.comment.CommentService;
import de.inren.service.comment.CommentServiceImpl;
import de.inren.service.security.RoleService;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "folderService")
@Transactional(readOnly = true)
public class FolderServiceImpl implements FolderService {

	private final static Logger log = LoggerFactory
			.getLogger(FolderServiceImpl.class);

	private boolean initDone = false;

    @Autowired
    RoleService roleService;

	@Resource
	FolderRepository folderRepository;

	@Override
	public void init() {
		if (!initDone) {
			log.info("FolderService init start.");
			roleService.init();
			// Check for root folder
			Collection<Folder> folders = folderRepository.findByType(FolderType.ROOT);
			Folder root;
			if(folders==null || folders.isEmpty()) {
				Role roleAdmin = roleService.findRoleByName(Roles.ADMIN);
				root = new Folder();
				root.setType(FolderType.ROOT);
				root.setName(FolderType.ROOT.name());
				root.getRoles().add(roleAdmin);
				root = folderRepository.save(root);
				log.info("Created root folder: " + root);
			} else {
				root = folders.iterator().next();
			}
			
			// Check for trash folder
			folders = folderRepository.findByType(FolderType.TRASH);
			if(folders==null || folders.isEmpty()) {
				Role roleUser = roleService.findRoleByName(Roles.USER);
				Folder trash = new Folder();
				trash.setType(FolderType.TRASH);
				trash.setName(FolderType.TRASH.name());
				trash.getRoles().add(roleUser);
				trash = folderRepository.save(trash);
				root.getItems().add(trash);
				root = folderRepository.save(root);
				log.info("Created trash folder: " + trash);
			}
			initDone = true;
			log.info("FolderService init done.");
		}
	}

	@Override
	public Folder saveFolder(Folder folder) {
		return folderRepository.save(folder);
	}

	@Override
	public Folder getRootFolder() {
		Collection<Folder> folders = folderRepository.findByType(FolderType.ROOT);
		if(folders==null || folders.size()!=1) {
			throw new IllegalStateException("There should be exact 1 root folder but found " + (folders==null? "0" : folders.size() ));
		}
		return folders.iterator().next();
	}

	@Override
	public Folder getTrashFolder() {
		Collection<Folder> folders = folderRepository.findByType(FolderType.TRASH);
		if(folders==null || folders.size()!=1) {
			throw new IllegalStateException("There should be exact 1 trash folder but found " + (folders==null? "0" : folders.size() ));
		}
		return folders.iterator().next();
	}

	@Override
	public Folder getFolder(long id) {
		return folderRepository.findOne(id);
	}

	@Override
	public Folder insertIntoFolder(Folder folder, AuthorizedDomainObject item) {
		folder = getFolder(folder.getId());
		folder.getItems().add(item);
		return saveFolder(folder);
	}

	@Override
	public Folder moveToTrash(Folder srcFolder, AuthorizedDomainObject item) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Folder removeFromFolder(Folder srcFolder, AuthorizedDomainObject item) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Folder moveToTrash(Folder folder) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Folder removeFromFolder(Folder srcFolder, Folder delFolder) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Folder moveIntoFolder(Folder srcFolder, Folder destFolder) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Folder moveIntoFolder(Folder srcFolder, Folder destFolder, AuthorizedDomainObject item) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Collection<Folder> findFolderForItem(AuthorizedDomainObject item) {
		throw new RuntimeException("not implemented");
	}

}
