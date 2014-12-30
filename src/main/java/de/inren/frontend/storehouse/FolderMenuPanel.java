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
package de.inren.frontend.storehouse;

import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.navigation.NavListItem;
import de.inren.service.folder.gui.FolderFacade;
import de.inren.service.folder.gui.FolderGM;
import de.inren.service.folder.gui.FolderMenuGM;

/**
 * @author Ingo Renner
 *
 */
public class FolderMenuPanel extends ABasePanel<Long> {

    @SpringBean
    private FolderFacade         folderFacade;

    private NestedTree<FolderGM> tree;

    public FolderMenuPanel(String id, IModel<Long> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        IModel<FolderMenuGM> fm = getFolderMenuModel();
        add(createTree(new FolderGMProvider(new PropertyModel<FolderGM>(fm, "root")), new ExpansionModel()));
    }

    protected AbstractTree<FolderGM> createTree(FolderGMProvider provider, IModel<Set<FolderGM>> state) {

        tree = new NestedTree<FolderGM>("tree", provider, state) {

            @Override
            protected Component newContentComponent(String id, IModel<FolderGM> node) {
                FolderGM data = node.getObject();
                PageParameters parameters = new PageParameters();
                parameters.add("id", data.getId());
                return new NavListItem<FileManagerPage>(id, FileManagerPage.class, parameters, Model.of(data.getName()));
            }
        };
        // tree.add(new HumanTheme());
        tree.add(new WindowsTheme());
        return tree;
    }

    private IModel<FolderMenuGM> getFolderMenuModel() {

        IModel<FolderMenuGM> res = new LoadableDetachableModel<FolderMenuGM>() {
            @Override
            protected FolderMenuGM load() {
                return folderFacade.getFolderMenu();
            }
        };
        return res;
    }

    private class ExpansionModel extends AbstractReadOnlyModel<Set<FolderGM>> {
        @Override
        public Set<FolderGM> getObject() {
            return FolderGMExpansionState.get();
        }
    }
}
