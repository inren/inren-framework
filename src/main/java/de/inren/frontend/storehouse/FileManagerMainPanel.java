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

package de.inren.frontend.storehouse;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.folder.Folder;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.folder.FolderService;

/**
 * @author Ingo Renner
 *
 */
public class FileManagerMainPanel extends ABasePanel<Folder> {

    @SpringBean
    private FolderService folderService;

    public FileManagerMainPanel(String id) {
        super(id);
        setDefaultModel(Model.of(folderService.getRootFolder()));
    }

    public FileManagerMainPanel(String id, IModel<Long> model) {
        super(id);
        if (model.getObject() == null) {
            setDefaultModel(Model.of(folderService.getRootFolder()));
        } else {
            setDefaultModel(Model.of(folderService.getFolder(model.getObject())));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createActions("actions"));
        add(createContent("content"));
    }

    private Component createContent(String id) {
        return new FolderContentPanel(id, getModel());
    }

    private Component createActions(String id) {
        return new ActionPanel(id, getModel());
    }
}
