package de.inren.service.blogpost;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.blogpost.BlogPost;
import de.inren.data.domain.comment.Comment;
import de.inren.data.repositories.blogpost.BlogPostRepository;
import de.inren.data.repositories.comment.CommentRepository;
import de.inren.service.security.RoleService;

@Service(value = "blogPostService")
@Transactional(readOnly = true)
public class BlogPostServiceImpl implements BlogPostService {

    private final static Logger log = LoggerFactory.getLogger(BlogPostServiceImpl.class);

    private boolean initDone = false;

    @Resource
    private RoleService roleService;

    @Resource
    private BlogPostRepository blogPostRepository;

    @Override
    public void init() {
        if (!initDone) {
            log.info("BlogPostService init start.");
            roleService.init();
            initDone = true;
            log.info("BlogPostService init done.");
        }
    }

    @Override
    public List<BlogPost> loadAllBlogPosts() {
        List<BlogPost> res = new ArrayList<BlogPost>();
        CollectionUtils.addAll(res, blogPostRepository.findAll().iterator());
        return res;
    }

    @Override
    public BlogPost save(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

	@Override
	public BlogPost load(Long id) {
		return blogPostRepository.findOne(id);
	}

}
