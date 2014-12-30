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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.blogpost.BlogPost;
import de.inren.data.domain.blogpost.PublishState;
import de.inren.data.domain.security.Role;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.blogpost.BlogPostService;
import de.inren.service.security.RoleService;

/**
 * @author Ingo Renner
 *
 */
public class EditOrCreateBlogPostPanel extends ABasePanel implements IAdminPanel {

    private static Logger log = LoggerFactory.getLogger(EditOrCreateBlogPostPanel.class);
    @SpringBean
    private BlogPostService blogPostService;

    @SpringBean
    private RoleService roleService;

    private final IWorktopManageDelegate<BlogPost> delegate;

    final BlogPost blogPost;

    public EditOrCreateBlogPostPanel(String componentId, IModel<BlogPost> m, IWorktopManageDelegate<BlogPost> delegate) {
        super(componentId);
        if (m != null) {
            blogPost = m.getObject();
        } else {
            blogPost = new BlogPost();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<BlogPost> form = new Form<BlogPost>("form", new CompoundPropertyModel<BlogPost>(blogPost));

        StringResourceModel lTitle = new StringResourceModel("title.label", EditOrCreateBlogPostPanel.this, null);
        form.add(new Label("title.label", lTitle));
        form.add(new TextField<String>("title", String.class).setRequired(true).setLabel(lTitle).setRequired(false).setLabel(lTitle));

        StringResourceModel LExtract = new StringResourceModel("extract.label", EditOrCreateBlogPostPanel.this, null);
        form.add(new Label("extract.label", LExtract));
        form.add(new TextField<String>("extract", String.class).setRequired(false).setLabel(LExtract).setRequired(false).setLabel(LExtract));

        StringResourceModel lContent = new StringResourceModel("content.label", EditOrCreateBlogPostPanel.this, null);
        form.add(new Label("content.label", lContent));
        form.add(new TextField<String>("content", String.class).setRequired(true).setLabel(lContent).setRequired(false).setLabel(lContent));

        
        
        List<Role> allRoles = new ArrayList<Role>();
        try {
            allRoles = roleService.loadAllRoles();
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        StringResourceModel lRoles = new StringResourceModel("roles.label", EditOrCreateBlogPostPanel.this, null);
        form.add(new Label("roles.label", lRoles));

        form.add(new Palette<Role>("roles", new ListModel<Role>(allRoles), new ChoiceRenderer<Role>("name", "id"), 5, false));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                delegate.switchToComponent(target, delegate.getManagePanel());
            }
        });

        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    BlogPost blogPost = (BlogPost) form.getModelObject();
                    blogPost.setUid(getUser().getId());
        			blogPost.setCreated(new Timestamp(Calendar.getInstance().getTime().getTime()));
        			blogPost.setState(PublishState.PUBLISHED);
        			blogPost.setAvailableForComment(true);
					BlogPost u = blogPostService.save(blogPost);
                    form.info(new StringResourceModel("feedback.success", EditOrCreateBlogPostPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", EditOrCreateBlogPostPanel.this, null).getString());
                    target.add(getFeedback());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                FeedbackPanel f = getFeedback();
                if (target != null && f != null) {
                    target.add(f);
                }
            }
        });

        add(form);
    }
}
