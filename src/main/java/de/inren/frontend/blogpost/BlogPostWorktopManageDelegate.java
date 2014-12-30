package de.inren.frontend.blogpost;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import de.inren.data.domain.blogpost.BlogPost;
import de.inren.frontend.common.manage.AWorktopManageDelegate;
import de.inren.frontend.common.panel.WorktopPanel;

public class BlogPostWorktopManageDelegate  extends AWorktopManageDelegate<BlogPost> {

    public BlogPostWorktopManageDelegate(WorktopPanel<BlogPost> panel) {
        super(panel);
    }

    @Override
    public Panel getManagePanel() {
        final ManageBlogPostsPanel p = new ManageBlogPostsPanel(getPanel().getComponentId(), this);
        p.setOutputMarkupId(true);
        return p;
    }

    @Override
    public Panel getEditPanel(IModel<BlogPost> m) {
        final EditOrCreateBlogPostPanel p = new EditOrCreateBlogPostPanel(getPanel().getComponentId(), m, this);
        p.setOutputMarkupId(true);
        return p;
    }

}
