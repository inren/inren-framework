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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.navigation.NavListItem;
import de.inren.service.folder.gui.FolderGM;

/**
 * The "left side" menu. It was designed for that purpose, but maybe in the
 * future we can use it for more. Who knows?
 * 
 * @author Ingo Renner
 *
 */
public class MenuList extends ABasePanel<FolderGM> {

	private static final Logger log = LoggerFactory.getLogger(MenuList.class);

	public MenuList(String id, IModel<FolderGM> model) {
		super(id, model);
	}


	@Override
	protected void onInitialize() {
		super.onInitialize();

		// Root
		final FolderGM data = getModel().getObject();
		PageParameters parameters = new PageParameters();
		parameters.add("id", data.getId());
		add(new NavListItem<FileManagerPage>("root",
				FileManagerPage.class, parameters, Model.of(data
						.getName())));
		
		// Subs
		ListView<FolderGM> listview = new ListView<FolderGM>("nav-ul", getModel().getObject().getFolders()) {

			@Override
			protected void populateItem(ListItem<FolderGM> item) {
				FolderGM data = item.getModel().getObject();
				PageParameters parameters = new PageParameters();
				parameters.add("id", data.getId());
				item.add(new NavListItem<FileManagerPage>("nav-li",
						FileManagerPage.class, parameters, Model.of(data
								.getName())));

				if (item.getModel().getObject().getFolders().isEmpty()) {
					item.add(new Label("nav-sub", "").setVisible(false));
				} else {
					item.add(new MenuList("nav-sub", item.getModel()));
				}

			}
		};
		add(listview);
	}

}
