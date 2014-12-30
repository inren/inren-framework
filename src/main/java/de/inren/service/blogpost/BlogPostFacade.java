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
package de.inren.service.blogpost;

import java.util.List;

import javax.annotation.Resource;

import net.bull.javamelody.MonitoredWithSpring;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.blogpost.BlogPost;
import de.inren.data.domain.comment.Comment;
import de.inren.service.comment.CommentService;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "blogPostFacade")
@Transactional(readOnly = true)
@MonitoredWithSpring
public class BlogPostFacade {
    @Resource
    private BlogPostService blogPostService;

    @Resource
    private CommentService  commentService;

    public List<BlogPost> loadAllBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.loadAllBlogPosts();
        return blogPosts;
    };

    public BlogPost save(BlogPost blogPost) {
        return blogPostService.save(blogPost);
    };

    public BlogPost load(Long id) {
        BlogPost blogPost = blogPostService.load(id);
        return blogPost;
    }

    public List<Comment> loadComments(Long id) {
        List<Comment> comments = commentService.loadCommentsFor(id);
        return comments;
    }

    public Comment addComment(BlogPost blogPost, Comment comment) {
        comment.setParentId(blogPost.getId());
        return commentService.save(comment);
    };

}
