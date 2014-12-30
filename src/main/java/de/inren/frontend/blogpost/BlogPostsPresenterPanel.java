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

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import de.inren.data.domain.blogpost.BlogPost;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.blogpost.BlogPostService;

/**
 * @author Ingo Renner
 *
 */
public class BlogPostsPresenterPanel extends ABasePanel{

	@SpringBean
	private BlogPostService blogPostService;
	
	
	public BlogPostsPresenterPanel(String id) {
		super(id);
		
		ListView<BlogPost> blogPosts = new BootstrapListView<BlogPost>("blogPosts", blogPostService.loadAllBlogPosts()) {

			@Override
			protected void populateItem(ListItem<BlogPost> item) {
				item.add(new BlogPostPanel("blogPost", item.getModel()));
			}
		};
		
		add(blogPosts);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
	}

}
