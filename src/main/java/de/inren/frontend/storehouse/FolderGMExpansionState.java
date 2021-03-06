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
package de.inren.frontend.storehouse;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

import de.inren.service.folder.gui.FolderGM;

/**
 * @author Ingo Renner
 *
 */
public class FolderGMExpansionState implements Set<FolderGM>, Serializable {
    private static final long                          serialVersionUID = 1L;

    private static MetaDataKey<FolderGMExpansionState> KEY              = new MetaDataKey<FolderGMExpansionState>() {
                                                                            private static final long serialVersionUID = 1L;
                                                                        };

    private Set<Long>                                  ids              = new HashSet<Long>();

    private boolean                                    inverse;

    public void expandAll() {
        ids.clear();

        inverse = true;
    }

    public void collapseAll() {
        ids.clear();

        inverse = false;
    }

    @Override
    public boolean add(FolderGM folderGM) {
        if (inverse) {
            return ids.remove(folderGM.getId());
        } else {
            return ids.add(folderGM.getId());
        }
    }

    @Override
    public boolean remove(Object o) {
        FolderGM folderGM = (FolderGM) o;

        if (inverse) {
            return ids.add(folderGM.getId());
        } else {
            return ids.remove(folderGM.getId());
        }
    }

    @Override
    public boolean contains(Object o) {
        FolderGM folderGM = (FolderGM) o;

        if (inverse) {
            return !ids.contains(folderGM.getId());
        } else {
            return ids.contains(folderGM.getId());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> A[] toArray(A[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<FolderGM> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends FolderGM> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the expansion for the session.
     * 
     * @return expansion
     */
    public static FolderGMExpansionState get() {
        FolderGMExpansionState expansion = Session.get().getMetaData(KEY);
        if (expansion == null) {
            expansion = new FolderGMExpansionState();
            // Default is all expanded
            expansion.expandAll();
            Session.get().setMetaData(KEY, expansion);
        }
        return expansion;
    }
}
