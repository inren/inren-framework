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
/**
 * Copygroup 2014 the original author or authors.
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

package de.inren.frontend.banking.category;

import org.apache.wicket.Component;
import org.wicketstuff.annotation.mount.MountPath;

import de.inren.data.domain.group.Group;
import de.inren.frontend.common.panel.WorktopPanel;
import de.inren.frontend.common.templates.SecuredPage;

/**
 * @author Ingo Renner
 * 
 */
@MountPath(value = "/categories")
public class ManageCategoriesPage extends SecuredPage<Group> {

    @Override
    public Component createPanel(String wicketId) {
        final WorktopPanel w = new WorktopPanel(wicketId);
        w.setDelegate(new CategoryWorktopManageDelegate(w));
        return w;
    }
}