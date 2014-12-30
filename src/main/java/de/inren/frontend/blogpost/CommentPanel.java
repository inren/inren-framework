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

package de.inren.frontend.blogpost;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import de.inren.data.domain.comment.Comment;
import de.inren.frontend.common.panel.ABasePanel;

/**
 * @author Ingo Renner
 *
 */
public class CommentPanel extends ABasePanel<Comment> {

	public CommentPanel(String id, IModel<Comment> model) {
		super(id, new CompoundPropertyModel<Comment>(model));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(DateLabel.forDatePattern("created", "HH:mm dd.mm.YYYY"));
		
		add(new Label("title"));
		
		add(new Label("content").setEscapeModelStrings(false));
		
	}
}
