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
package de.inren.data.domain.picture;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.core.DataFileContainer;
import de.inren.data.domain.security.AuthorizedDomainObject;

/**
 * @author Ingo Renner
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Picture extends AuthorizedDomainObject implements DataFileContainer {
    private String title;

    @Column(length = 4096)
    private String description;

    @Column(nullable = false, unique = true)
    private String digest;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    @Override
    public String getHash() {
        return getDigest();
    }

    @Override
    public void setHash(String hash) {
        setDigest(hash);
    }

    @Override
    public String getFilename() {
        if (getTitle().toLowerCase(Locale.getDefault()).endsWith(".jpg")) {
            return getTitle();
        } else {
            return getTitle() + ".jpg";
        }
    }

    @Override
    public void setFilename(String filename) {
        setTitle(filename);
    }
}
