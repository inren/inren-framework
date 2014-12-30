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

package de.inren.data.domain.blogpost;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.security.AuthorizedDomainObject;

/**
 * @author Ingo Renner
 *
 */

@Entity(name = "BlogPost")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BlogPost extends AuthorizedDomainObject {

    @Column(nullable = false)
    private Long uid;
	
	@Column(nullable = false)
	private Date created;
	
	@Column(nullable = false, unique=true)
	private String title;
	
	@Column(nullable = true)
	private String extract;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = true)
	private String tags;
	
	@Column(nullable = false)
	private PublishState state;
	
	@Column(nullable = false)
	private boolean availableForComment;

	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExtract() {
		return extract;
	}

	public void setExtract(String extract) {
		this.extract = extract;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public PublishState getState() {
		return state;
	}

	public void setState(PublishState state) {
		this.state = state;
	}

	public boolean isAvailableForComment() {
		return availableForComment;
	}

	public void setAvailableForComment(boolean availableForComment) {
		this.availableForComment = availableForComment;
	}

}
