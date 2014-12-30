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

package de.inren.service.folder.gui;

import java.io.Serializable;

/**
 * @author Ingo Renner
 *
 */
public class FolderMenuGM implements Serializable {
	
	private long  currentFolderId;
	
	private FolderGM root;

	public long getCurrentFolderId() {
		return currentFolderId;
	}

	public void setCurrentFolderId(long currentFolderId) {
		this.currentFolderId = currentFolderId;
	}

	public FolderGM getRoot() {
		return root;
	}

	public void setRoot(FolderGM root) {
		this.root = root;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FolderMenuGM [currentFolderId=")
				.append(currentFolderId).append(", root=").append(root)
				.append("]");
		return builder.toString();
	}
}
