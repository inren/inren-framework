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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import wicket.contrib.tinymce.TinyMceBehavior;
import de.inren.data.domain.comment.Comment;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.comment.CommentService;

/**
 * @author Ingo Renner
 *
 */
public abstract class CommentEditorPanel extends ABasePanel<Comment> {

	@SpringBean
	private CommentService commentService;
	
	public CommentEditorPanel(String id, IModel<Comment> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
        Form<Comment> form = new Form<Comment>("form", new CompoundPropertyModel<Comment>(getModel()));

        StringResourceModel lTitle = new StringResourceModel("title.label", CommentEditorPanel.this, null);
        form.add(new Label("title.label", lTitle));
        form.add(new TextField<String>("title", String.class).setRequired(true).setLabel(lTitle).setRequired(false).setLabel(lTitle));

        StringResourceModel lContent = new StringResourceModel("content.label", CommentEditorPanel.this, null);
        form.add(new Label("content.label", lContent));
        // form.add(new TextField<String>("content", String.class).setRequired(true).setLabel(lContent).setRequired(false).setLabel(lContent));

        TextArea<String> textArea = new TextArea<String>("content");
        textArea.add(new TinyMceBehavior());
        form.add(textArea);
        
        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
            }
        });

        form.add(new Button("submit") {
            @Override
			public void onSubmit() {
            	CommentEditorPanel.this.onSubmit(CommentEditorPanel.this.getModel());

            }
        });

        add(form);
		
	}
	
	public abstract void onSubmit(IModel<Comment> model); 
}
