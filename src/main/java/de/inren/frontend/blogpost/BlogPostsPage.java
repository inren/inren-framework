package de.inren.frontend.blogpost;

import org.apache.wicket.Component;

import de.inren.data.domain.blogpost.BlogPost;
import de.inren.frontend.common.templates.SecuredPage;

public class BlogPostsPage extends SecuredPage<BlogPost> {

	@Override
    public Component createPanel(String wicketId) {
        return new BlogPostsPresenterPanel(wicketId);
    }

}
