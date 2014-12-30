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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.inren.data.domain.folder.Folder;
import de.inren.data.domain.picture.Picture;
import de.inren.data.domain.security.AuthorizedDomainObject;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.storehouse.dynamic.TextImagePicturePanel;
import de.inren.frontend.storehouse.dynamic.TextImageResource;
import de.inren.frontend.storehouse.picture.PictureVariantType;
import de.inren.frontend.storehouse.picture.SimplePicturePanel;

/**
 * @author Ingo Renner
 *
 */
public class FolderContentPanel extends ABasePanel<Folder> {

    public FolderContentPanel(String id, IModel<Folder> model) {
        super(id, model);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        GridView<AuthorizedDomainObject> gridView = new GridView<AuthorizedDomainObject>("gridView", createDataProvider()) {

            @Override
            protected void populateEmptyItem(Item<AuthorizedDomainObject> item) {
                item.add(new Label("id", ""));
            }

            @Override
            protected void populateItem(Item<AuthorizedDomainObject> item) {
                AuthorizedDomainObject object = item.getModel().getObject();
                if (object instanceof Picture) {
                    item.add(createPreviewThumbnail("id", (Picture) object));

                } else {
                    if (object instanceof Folder) {
                        final String title = object.getClass().getSimpleName() + "\n" + ((Folder) object).getName();
                        item.add(new TextImagePicturePanel("id", new TextImageResource(100, 100, title)));

                    } else {
                        final String title = object.getClass().getSimpleName() + "\n" + object.getId();
                        item.add(new TextImagePicturePanel("id", new TextImageResource(100, 100, title)));
                    }
                }
            }
        };
        gridView.setColumns(5);
        add(gridView);
    }

    protected Component createPreviewThumbnail(String id, Picture picture) {
        return new SimplePicturePanel(id, PictureVariantType.THUMBNAIL, picture).setOutputMarkupId(true);
    }

    private SortableDataProvider<AuthorizedDomainObject, String> createDataProvider() {
        return new SortableDataProvider<AuthorizedDomainObject, String>() {

            @Override
            public Iterator<? extends AuthorizedDomainObject> iterator(long first, long count) {
                List<AuthorizedDomainObject> res = new ArrayList<AuthorizedDomainObject>();
                res.addAll(getModel().getObject().getItems());
                return res.subList((int) first, (int) (first + count)).iterator();
            }

            @Override
            public long size() {
                return getModel().getObject().getItems().size();
            }

            @Override
            public IModel<AuthorizedDomainObject> model(AuthorizedDomainObject object) {
                return Model.of(object);
            }
        };
    }
}
