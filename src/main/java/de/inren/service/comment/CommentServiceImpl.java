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

package de.inren.service.comment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.comment.Comment;
import de.inren.data.repositories.comment.CommentRepository;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "commentService")
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

	private final static Logger log = LoggerFactory
			.getLogger(CommentServiceImpl.class);

	private boolean initDone = false;

	@Resource
	CommentRepository commentRepository;

	@Override
	public void init() {
		if (!initDone) {
			log.info("CommentService init start.");
			initDone = true;
			log.info("CommentService init done.");
		}
	}

	@Override
	public List<Comment> loadAllComments() {
		List<Comment> res = new ArrayList<Comment>();
		CollectionUtils.addAll(res, commentRepository.findAll().iterator());
		return res;
	}

	@Override
	public Comment save(Comment comment) {
		return commentRepository.save(comment);
	}

	@Override
	public Comment load(Long id) {
		Comment comment = commentRepository.findOne(id);
		addComments(comment);
		return comment;
	}

	@Override
	public List<Comment> loadCommentsFor(Long id) {
		List<Comment> comments = commentRepository.findByParentId(id);
		for (Comment comment : comments) {
			addComments(comment);
		}
		return comments;
	}
	
	private void addComments(Comment comment) {
		List<Comment> comments = commentRepository.findByParentId(comment.getId());
		comment.setComments(comments);
		for (Comment c : comments) {
			addComments(c);
		}
	}

}
