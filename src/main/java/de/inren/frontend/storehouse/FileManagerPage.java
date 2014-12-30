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

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.frontend.common.templates.SecuredPage;

/**
 * 
 * File manage for storehouse objects, managed by folders.
 * 
 * @author Ingo Renner
 *
 */
public class FileManagerPage extends SecuredPage<Long> {

    private static final Logger log = LoggerFactory.getLogger(FileManagerPage.class);

    public FileManagerPage() {
        super();
        setModel(new AbstractReadOnlyModel<Long>() {
            @Override
            public Long getObject() {
                final StringValue folderId = getPageParameters().get("id");
                return folderId.isEmpty() ? null : folderId.toLongObject();
            }
        });

    }

    @Override
    public Component createPanel(String wicketId) {
        return new FileManagerMainPanel(wicketId, getModel());
    }

    @Override
    protected Component getLeftComponent(String id) {
        return new FolderMenuPanel(id, getModel());
    }
}
