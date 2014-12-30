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

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import de.inren.data.domain.blogpost.BlogPost;
import de.inren.data.domain.comment.Comment;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.blogpost.BlogPostFacade;

/**
 * @author Ingo Renner
 *
 */
public class BlogPostPanel extends ABasePanel<BlogPost> {

	@SpringBean
	private BlogPostFacade blogPostFacade;
	private ListView<Comment> comments;
	
	public BlogPostPanel(String id, IModel<BlogPost> model) {
		super(id, new CompoundPropertyModel<BlogPost>(model));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setOutputMarkupId(true);
		add(DateLabel.forDatePattern("created", "HH:mm dd.mm.YYYY"));
		
		add(new Label("title"));
		
		add(new Label("extract"));
		
		add(new Label("content").setEscapeModelStrings(false));
		
		add(new Label("tags"));
		
		// TODO availableForComment Comment Button einblenden
		
		add(new CommentEditorPanel("newComment", Model.of(new Comment())) {

			@Override
			public void onSubmit(IModel<Comment> model) {
				try {
					Comment comment = model.getObject();
					comment.setUid(getUser().getId());
					comment.setCreated(new Timestamp(Calendar.getInstance()
							.getTime().getTime()));
					blogPostFacade.addComment(BlogPostPanel.this.getModel().getObject(), comment);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		});
		
		comments = new BootstrapListView<Comment>("comments", 
				blogPostFacade.loadComments(((BlogPost) getDefaultModel().getObject()).getId())) {
			@Override
			protected void populateItem(ListItem<Comment> item) {
				item.add(new CommentPanel("comment", item.getModel()));		
			}
		};
			
		add(comments);
		
	}
}
