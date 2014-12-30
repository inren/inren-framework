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

package de.inren.service.folder;

import java.util.Collection;

import net.bull.javamelody.MonitoredWithSpring;
import de.inren.data.domain.folder.Folder;
import de.inren.data.domain.security.AuthorizedDomainObject;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
@MonitoredWithSpring
public interface FolderService extends Initializable {

    Folder getRootFolder();

    Folder getTrashFolder();

    Folder getFolder(long id);

    Folder saveFolder(Folder folder);

    Folder insertIntoFolder(Folder folder, AuthorizedDomainObject item);

    Folder moveToTrash(Folder srcFolder, AuthorizedDomainObject item);

    Folder removeFromFolder(Folder srcFolder, AuthorizedDomainObject item);

    Folder moveToTrash(Folder folder);

    Folder removeFromFolder(Folder srcFolder, Folder delFolder);

    Folder moveIntoFolder(Folder srcFolder, Folder destFolder);

    Folder moveIntoFolder(Folder srcFolder, Folder destFolder, AuthorizedDomainObject item);

    Collection<Folder> findFolderForItem(AuthorizedDomainObject item);

}
